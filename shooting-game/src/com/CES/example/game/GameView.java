package com.CES.example.game;

import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


@SuppressLint("ViewConstructor")
public class GameView extends SurfaceView implements SurfaceHolder.Callback, 
		Runnable, Button.OnClickListener {
	
	
	private final static int STATE_IDLE		= 0;
	private final static int STATE_PLAY		= 1;
	private final static int STATE_PAUSE	= 2;
	private final static int STATE_OVER		= 3;
	
	
	private final static int BN_START		= 1;
	private final static int BN_BACK		= 2;
	private final static int BN_RESTART		= 3;
	private final static int BN_PAUSE		= 4;
	private final static int BN_RESUME		= 5;
	private final static int BN_BOMB		= 6;
	private final static int BN_FINISH		= 7;
	private final static int BN_SURE		= 8;
	private final static int BN_CANCEL		= 9;
	
	
	private final static int FRAME_TIME		= 50;
	
	private BackGround backGround;
	private Player player;
	private Equipment equip;
	private List<Enemy> enemys;
	private List<Bullet> bullets;
	private int score;
	
	private Button bnStart;
	private Button bnBack;
	private Button bnRestart;
	private Button bnResume;
	private Button bnPause;
	private Button bnBomb;
	private Dialog dlgFinish;
	private Dialog dlgRestart;
	private Dialog dlgOvre;
	
	private SurfaceHolder holder;
	private Handler handler;
	private int state = STATE_IDLE;
	private boolean isRunning = false;
	
	private Paint paint;
	private int touchX = -1;
	private int touchY = -1;
	private int frameCount = 0;

	
	public GameView(Context context, Handler handler, 
			int screenWidth, int screenHeight) {
		super(context);
		this.handler = handler;
		GameHelper.init(context, screenWidth, screenHeight);
		
		
		backGround = new BackGround(5);
		player = Player.createPlayer();
		enemys = new LinkedList<Enemy>();
		bullets = new LinkedList<Bullet>(); 
		
	
		bnStart = new Button(BN_START, "开始游戏", screenWidth / 2, 40);
		bnBack = new Button(BN_BACK, "退出游戏", screenWidth / 2, 40);
		bnRestart = new Button(BN_RESTART, "重新开始", screenWidth / 2, 40);
		bnResume = new Button(BN_RESUME, "返回游戏", screenWidth / 2, 40);
		bnPause = new Button(BN_PAUSE, GameHelper.getBitmap("pause.png"));
		bnBomb = new Button(BN_BOMB, GameHelper.getBitmap("bomb.png"));
		
		
		dlgFinish = new Dialog("注意", "是否退出游戏", 
				new int[]{BN_FINISH, BN_CANCEL}, 
				new String[]{"退出", "取消"}, 
				new Button.OnClickListener[]{this, this});
		dlgRestart = new Dialog("注意", "是否重新开始游戏", 
				new int[]{BN_SURE, BN_CANCEL}, 
				new String[]{"确定", "取消"}, 
				new Button.OnClickListener[]{this, this});
		dlgOvre = new Dialog("最终得分", "", 
				new int[]{BN_CANCEL}, 
				new String[]{"继续"}, 
				new Button.OnClickListener[]{this});
		
		
		bnStart.setOnClickListener(this);
		bnBack.setOnClickListener(this);
		bnRestart.setOnClickListener(this);
		bnResume.setOnClickListener(this);
		bnPause.setOnClickListener(this);
		bnBomb.setOnClickListener(this);
		
		paint = new Paint();
		paint.setTextSize(32.0f);
		
		getHolder().addCallback(this);
		gameIdle();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}

	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		this.holder = holder;
		(new Thread(this)).start();
	}

	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isRunning = false;
	}

	
	@Override
	public void run() {
		System.out.println("Game start.");
		isRunning = true;
		
		try {
			while(isRunning) {
				long t1 = System.currentTimeMillis();
				Canvas canvas = holder.lockCanvas();
				
				try {
					
					onPaint(canvas);	
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				holder.unlockCanvasAndPost(canvas);
				long t2 = System.currentTimeMillis();
				if(t2 - t1 < FRAME_TIME) {
					try {
						Thread.sleep(FRAME_TIME - (t2 - t1));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				frameCount++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		isRunning = false;
		System.out.println("Game finish.");
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			if(state == STATE_IDLE)
				onClick(BN_BACK);
			else if(state == STATE_PLAY)
				onClick(BN_PAUSE);
			else if(state == STATE_PAUSE) {
				if(dlgRestart.isVisible())
					onClick(BN_CANCEL);
				else
					onClick(BN_BACK);
			}
			else if(state == STATE_OVER)
				onClick(BN_CANCEL);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int index = event.getActionIndex();
		int id = event.getPointerId(index);
		if(id != 0)
			return super.onTouchEvent(event);
		
		int act = event.getActionMasked();
		int x = (int) event.getX(index);
		int y = (int) event.getY(index);
		
		if(state == STATE_IDLE) {
			bnStart.onTouch(act, x, y);
			bnBack.onTouch(act, x, y);
			dlgFinish.onTouch(act, x, y);
		}
		else if(state == STATE_PLAY) {
			bnPause.onTouch(act, x, y);
			bnBomb.onTouch(act, x, y);
			
			
			if(act == MotionEvent.ACTION_DOWN || 
					act == MotionEvent.ACTION_POINTER_DOWN) {
				touchX = x;
				touchY = y;
			}
			else if(act == MotionEvent.ACTION_MOVE) {
				int dx = x - touchX;
				int dy = y - touchY;
				touchX = x;
				touchY = y;
				player.move(dx, dy);
			}
			else {
				touchX = -1;
				touchY = -1;
			}
		}
		else if(state == STATE_PAUSE) {
			bnBack.onTouch(act, x, y);
			bnRestart.onTouch(act, x, y);
			bnResume.onTouch(act, x, y);
			dlgFinish.onTouch(act, x, y);
			dlgRestart.onTouch(act, x, y);
		}
		else if(state == STATE_OVER) {
			dlgOvre.onTouch(act, x, y);
		}
		else {
			return super.onTouchEvent(event);
		}
		return true;
	}
	
	
	@Override
	public void onClick(int id) {
		switch(id) {
		case BN_START:	
			gameStart();
			break;
		case BN_BACK:	
			bnStart.setVisible(false);
			bnBack.setVisible(false);
			bnResume.setVisible(false);
			bnRestart.setVisible(false);
			dlgFinish.setVisible(true);
			break;
		case BN_RESTART:
			bnResume.setVisible(false);
			bnRestart.setVisible(false);
			bnBack.setVisible(false);
			dlgRestart.setVisible(true);
			break;
		case BN_PAUSE:	
			gamePause();
			break;
		case BN_RESUME:	
			gameResume();
			break;
		case BN_BOMB:	
			if(state == STATE_PLAY) {
				score += useBomb();
			}
			break;
		case BN_FINISH:	
			handler.sendEmptyMessage(0);
			break;
		case BN_SURE:	
			gameStart();
			break;
		case BN_CANCEL:	
			dlgFinish.setVisible(false);
			dlgRestart.setVisible(false);
			dlgOvre.setVisible(false);
			if(state == STATE_IDLE || state == STATE_OVER)
				gameIdle();
			if(state == STATE_PAUSE)
				gamePause();
			break;
		}
	}
	
	
	public void onPause() {
		gamePause();
	}

	private void onPaint(Canvas canvas) {
		backGround.paint(canvas);
		
		
		if(state == STATE_PAUSE || state == STATE_PLAY) {
			
			player.paint(canvas);
			
			for(Enemy e : enemys)
				e.paint(canvas);
			
			for(Bullet b : bullets)
				b.paint(canvas);
			
			if(equip != null)
				equip.paint(canvas);
			
			
			if(state == STATE_PLAY) {
				if(!player.isVisible())
					gameOver();
				
				bnPause.paint(canvas);
				
				
				score += GameHelper.collideDetect(bullets, enemys);
				GameHelper.collideDetect(player, equip);
				GameHelper.collideDetect(player, enemys);
			
				if(frameCount % 2 == 0)
					GameHelper.refreshEnemy(enemys, true);
				GameHelper.time += FRAME_TIME;
				
				
				backGround.scroll();
				if(frameCount % 2 == 0) player.nextFrame();
				for(Enemy e : enemys) {
					e.move();
					if(frameCount % 2 == 0) e.nextFrame();
				}
				if(equip != null && equip.isVisible())
					equip.move();

				
				Bullet.clearBullets(bullets);
				if(frameCount % 2 == 0) player.fire(bullets);
				
				
				if(GameHelper.time % (10000 * 3) == 0)
					equip = GameHelper.createEquipment();
			}
			
			
			canvas.drawText(Integer.toString(score), bnPause.getWidth() + 10, 
					40, paint);
			int bombCount = player.getBombCount();
			if(bombCount > 0) {
				bnBomb.setVisible(true);
				bnBomb.paint(canvas);
				canvas.drawText("X " + bombCount, bnBomb.getWidth() + 10, 
						bnBomb.getY() + 45, paint);
			}
			else
				bnBomb.setVisible(false);
		}

		
		bnStart.paint(canvas);
		bnBack.paint(canvas);
		bnResume.paint(canvas);
		bnRestart.paint(canvas);
		bnBack.paint(canvas);
		
		
		dlgFinish.paint(canvas);
		dlgRestart.paint(canvas);
		dlgOvre.paint(canvas);
	}
	
	
	private void gameIdle() {
		state = STATE_IDLE;
		
		player.setVisible(false);
		
		bnStart.setVisible(true);
		bnBack.setVisible(true);
		bnRestart.setVisible(false);
		bnResume.setVisible(false);
		bnPause.setVisible(false);
		bnBomb.setVisible(false);
		
		dlgFinish.setVisible(false);
		dlgRestart.setVisible(false);
		dlgOvre.setVisible(false);
		
		bnStart.setCenterPosition(GameHelper.screenWidth / 2, 
				GameHelper.screenHeight / 2 - 50);
		bnBack.setCenterPosition(GameHelper.screenWidth / 2, 
				GameHelper.screenHeight / 2 + 50);
	}
	
	
	private void gameStart() {
		state = STATE_PLAY;
		score = 0;
		GameHelper.time = 0;
		
		player.relive();
		player.setRefPixelPosition(GameHelper.screenWidth / 2, 
				GameHelper.screenHeight - player.getHeight() / 2);
		player.setVisible(true);
		
		for(Enemy e : enemys)
			e.setVisible(false);
		GameHelper.refreshEnemy(enemys, false);
		bullets.clear();

		bnStart.setVisible(false);
		bnBack.setVisible(false);
		bnRestart.setVisible(false);
		bnResume.setVisible(false);
		bnPause.setVisible(true);
		bnBomb.setVisible(false);
		
		dlgFinish.setVisible(false);
		dlgRestart.setVisible(false);
		dlgOvre.setVisible(false);
		
		bnPause.setPosition(0, 0);
		bnBomb.setPosition(0, GameHelper.screenHeight - bnBomb.getHeight());
	}
	

	private void gamePause() {
		state = STATE_PAUSE;

		bnStart.setVisible(false);
		bnBack.setVisible(true);
		bnRestart.setVisible(true);
		bnResume.setVisible(true);
		bnPause.setVisible(false);

		dlgFinish.setVisible(false);
		dlgRestart.setVisible(false);
		dlgOvre.setVisible(false);
		
		bnResume.setCenterPosition(GameHelper.screenWidth / 2, 
				GameHelper.screenHeight / 2 - 100);
		bnRestart.setCenterPosition(GameHelper.screenWidth / 2, 
				GameHelper.screenHeight / 2);
		bnBack.setCenterPosition(GameHelper.screenWidth / 2, 
				GameHelper.screenHeight / 2 + 100);
	}
	
	
	private void gameResume() {
		state = STATE_PLAY;

		bnStart.setVisible(false);
		bnBack.setVisible(false);
		bnRestart.setVisible(false);
		bnResume.setVisible(false);
		bnPause.setVisible(true);

		dlgFinish.setVisible(false);
		dlgRestart.setVisible(false);
		dlgOvre.setVisible(false);
		
		bnPause.setPosition(0, 0);
		bnBomb.setPosition(0, GameHelper.screenHeight - bnBomb.getHeight());
	}
	
	
	private void gameOver() {
		state = STATE_OVER;

		bnStart.setVisible(false);
		bnBack.setVisible(false);
		bnRestart.setVisible(false);
		bnResume.setVisible(false);
		bnPause.setVisible(false);

		dlgFinish.setVisible(false);
		dlgRestart.setVisible(false);
		dlgOvre.setVisible(true);
		
		dlgOvre.setText(Integer.toString(score));
	}
	
	
	private int useBomb() {
		int score = 0;
		if(player.isVisible() && player.isAlive() && player.useBomb()) {
			for(Enemy e : enemys) {
				if(e.isAlive() && e.isVisible()) {
					e.bombed();
					score += e.getScore();
				}
			}
		}
		return score;
	}

}
