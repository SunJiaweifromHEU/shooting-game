package com.CES.example.game;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.annotation.SuppressLint;
import android.app.Activity;


@SuppressLint("HandlerLeak")
public class MainActivity extends Activity {
	
	private GameView view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();
		
		
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					finish();
					break;
				}
			}
		};
		
		
		view = new GameView(this, handler, screenWidth, screenHeight);
		setContentView(view);
	}

	@Override
	protected void onDestroy() {
	    GameHelper.mediaPlayer.stop();
		GameHelper.mediaPlayer.release();
		GameHelper.soundPool.release();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		GameHelper.mediaPlayer.pause();
		view.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		GameHelper.mediaPlayer.start();
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			if(view != null) {
				if(view.dispatchKeyEvent(event))
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
