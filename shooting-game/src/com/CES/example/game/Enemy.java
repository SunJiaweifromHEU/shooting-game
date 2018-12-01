package com.CES.example.game;

import javax.microedition.lcdui.game.Sprite;

import android.graphics.Bitmap;


public class Enemy extends Sprite {
	
	public final static int TYPE1 = 1;
	public final static int TYPE2 = 2;
	public final static int TYPE3 = 3;
	
	private int type;
	private int score;
	private int speed;
	private int live;
	private boolean isAlive;
	
	private int[] flySequence;	
	private int[] bombSequence;	
	private int hitFrame;		

	
	private Enemy(Bitmap image, int frameWidth, int frameHeight, int type) {
		super(image, frameWidth, frameHeight);
		
		this.type = type;
		this.live = 0;
		this.isAlive = false;
		
		if(type == TYPE1) {
			flySequence = new int[]{0};
			bombSequence = new int[]{1, 2, 3, 4, 4};
			hitFrame = 0;
			score = 100;
			defineCollisionRectangle(5, 10, 45, 30);
		}
		else if(type == TYPE2){
			flySequence = new int[]{0, 1};
			bombSequence = new int[]{2, 3, 4, 5, 5};
			hitFrame = 1;
			score = 500;
			defineCollisionRectangle(15, 15, 50, 70);
		}
		else if(type == TYPE3) {
			flySequence = new int[]{0, 1, 2};
			bombSequence = new int[]{3, 4, 5, 6, 7, 8, 8};
			hitFrame = 2;
			score = 1000;
			defineCollisionRectangle(20, 20, 130, 220);
		}
		
		setFrameSequence(flySequence);
	}
	
	
	public static Enemy createEnemy(int type) {
		Bitmap image = null;
		int frameWidth = 0;
		int frameHeight = 0;
		
		if(type == TYPE1) {
			image = GameHelper.getBitmap("enemy1.png");
			frameWidth = image.getWidth() / 5;
			frameHeight = image.getHeight();
		}
		else if(type == TYPE2) {
			image = GameHelper.getBitmap("enemy2.png");
			frameWidth = image.getWidth() / 6;
			frameHeight = image.getHeight();
		}
		else if(type == TYPE3) {
			image = GameHelper.getBitmap("enemy3.png");
			frameWidth = image.getWidth() / 9;
			frameHeight = image.getHeight();
		}
		else {
			throw new IllegalArgumentException("Unkown type enemy.");
		}
		
		Enemy enemy = new Enemy(image, frameWidth, frameHeight, type);
		
		return enemy;
	}
	
	
	@Override
	public void nextFrame() {
		super.nextFrame();
		if(isAlive && hitFrame != 0 && getFrame() == hitFrame)
			nextFrame();
		if(!isAlive || isVisible()) {
			int index = getFrame();
			if(index == bombSequence.length - 1)
				setVisible(false);
		}
	}

	
	public void relive(int speed, int x, int y) {
		this.speed = speed;
		this.live = (type == TYPE1)? 1 : (type == TYPE2)? 15 : 40;
		this.isAlive = true;
		setPosition(x, y);
		setFrameSequence(flySequence);
		setVisible(true);
	}
	
	
	public void move() {
		if(!isAlive || !isVisible())
			return;
		
		move(0, speed);
		if(getY() > GameHelper.screenHeight)
			setVisible(false);
	}
	
	
	public void hited() {
		if(!isAlive || !isVisible())
			return;
		
		setFrame(hitFrame);
		if(--live == 0) {
			isAlive = false;
			setFrameSequence(bombSequence);
			if(type == TYPE1)
				GameHelper.playSound(R.raw.enemy1_down);
			else if(type == TYPE2)
				GameHelper.playSound(R.raw.enemy2_down);
			else if(type == TYPE3)
				GameHelper.playSound(R.raw.enemy3_down);
		}
	}
	
	
	public void bombed() {
		if(!isAlive || !isVisible())
			return;
		
		live = 0;
		isAlive = false;
		setFrameSequence(bombSequence);
	}

	
	public boolean isAlive() {
		return isAlive;
	}

	
	public int getSpeed() {
		return speed;
	}
	
	
	public int getScore() {
		return score;
	}

	
	public int getType() {
		return type;
	}

}
