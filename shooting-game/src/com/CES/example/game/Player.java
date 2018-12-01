package com.CES.example.game;

import java.util.List;

import javax.microedition.lcdui.game.Sprite;

import android.graphics.Bitmap;


public class Player extends Sprite {
	
	private boolean isAlive;
	private int doubleGunTime;
	private int bombCount;
	private int fireCount;
	
	private int[] flySequence;	
	private int[] bombSequence;	

	
	private Player(Bitmap image, int frameWidth, int frameHeight) {
		super(image, frameWidth, frameHeight);
		this.isAlive = true;
		this.flySequence = new int[]{0, 1};
		this.bombSequence = new int[]{2, 3, 4, 4};
		this.setFrameSequence(flySequence);
		this.defineReferencePixel(frameWidth / 2, frameHeight / 2);
		this.defineCollisionRectangle(20, 20, 70, 80);
	}
	
	
	public static Player createPlayer() {
		Bitmap image = GameHelper.getBitmap("player.png");
		int frameCount = 5;
		int frameWidth = image.getWidth() / frameCount;
		int frameHeight = image.getHeight();
		Player player = new Player(image, frameWidth, frameHeight);
		return player;
	}
	
	
	@Override
	public void move(int dx, int dy) {
		if(!isAlive || !isVisible())
			return;
		
		int x = getX();
		int y = getY();
		int w = getWidth();
		int h = getHeight();
		
		if(x + dx < 0)
			dx = -x;
		if(x + dx + w > GameHelper.screenWidth)
			dx = GameHelper.screenWidth - x - w;
		if(y + dy < 0)
			dy = -y;
		if(y + dy + w > GameHelper.screenHeight)
			dy = GameHelper.screenHeight - y - h;
		
		super.move(dx, dy);
	}

	
	@Override
	public void nextFrame() {
		super.nextFrame();
		if(!isAlive || isVisible()) {
			int index = getFrame();
			if(index == bombSequence.length - 1)
				setVisible(false);
		}
	}

	
	public void moveTo(int x, int y) {
		if(!isAlive || !isVisible())
			return;
		
		int w = getWidth();
		int h = getHeight();
		if(x - w / 2 < 0)
			x = w / 2;
		if(x + w / 2 > GameHelper.screenWidth)
			x = GameHelper.screenWidth - w / 2;
		if(y - h / 2 < 0)
			y = h / 2;
		if(y + h / 2 > GameHelper.screenHeight)
			y = GameHelper.screenHeight - h / 2;
		setRefPixelPosition(x, y);
	}
	
	
	public void knocked() {
		if(!isAlive || !isVisible())
			return;
		isAlive = false;
		setFrameSequence(bombSequence);
		GameHelper.playSound(R.raw.game_over);
	}
	
	
	public void fire(List<Bullet> bullets) {
		if(!isAlive || !isVisible())
			return;
		
		if(doubleGunTime > 0) {
			Bullet b = Bullet.createBullet(Bullet.TYPE2);
			b.setPosition(getX() + getWidth() / 2 - b.getWidth() / 2 - 15,
					getY() - (fireCount % 6) * 25 - b.getHeight());
			b.setVisible(true);
			bullets.add(b);
			
			b = Bullet.createBullet(Bullet.TYPE2);
			b.setPosition(getX() + getWidth() / 2 - b.getWidth() / 2 + 15,
					getY() - (fireCount % 6) * 25 - b.getHeight());
			b.setVisible(true);
			bullets.add(b);
			doubleGunTime--;
		}
		else {
			Bullet b = Bullet.createBullet(Bullet.TYPE1);
			b.setPosition(getX() + getWidth() / 2 - b.getWidth() / 2,
					getY() - (fireCount % 6) * 25 - b.getHeight());
			b.setVisible(true);
			bullets.add(b);
		}
		fireCount++;
		GameHelper.playSound(R.raw.fire);
	}

	
	public void relive() {
		this.isAlive = true;
		this.doubleGunTime = 0;
		this.bombCount = 0;
		this.fireCount = 0;
		setFrameSequence(flySequence);
	}

	
	public boolean isAlive() {
		return isAlive;
	}
	
	
	public void doubleGun() {
		doubleGunTime = 200;
		GameHelper.playSound(R.raw.get_double_gun);
	}
	
	
	public void addBomb() {
		bombCount++;
		GameHelper.playSound(R.raw.get_bomb);
	}
	
	
	public boolean useBomb() {
		if(bombCount > 0) {
			bombCount--;
			GameHelper.playSound(R.raw.use_bomb);
			return true;
		}
		else
			return false;
	}

	
	public int getBombCount() {
		return bombCount;
	}

}
