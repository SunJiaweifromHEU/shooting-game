package com.CES.example.game;

import javax.microedition.lcdui.game.Sprite;

import android.graphics.Bitmap;


public class Equipment extends Sprite {
	
	public final static int TYPE_DOUBLE	= 0;
	public final static int TYPE_BOMB	= 1;
	
	private static int[][] sequence = new int[][]{{0}, {1}};
	private static int[] speed = new int[]{50, 50, 40, 25, -10,
		-40, -40, -35, 0, 40, 45, 50, 60};
	
	private int type;
	private int speedIndex;

	
	private Equipment(Bitmap image, int frameWidth, int frameHeight, int type) {
		super(image, frameWidth, frameHeight);
		setFrameSequence(sequence[type]);
		defineCollisionRectangle(10, 10, 50, 90);
		this.type = type;
		this.speedIndex = 0;
	}
	
	
	public static Equipment createEquipment(int type) {
		if(type != TYPE_DOUBLE && type != TYPE_BOMB)
			throw new IllegalArgumentException("Unkown type.");
		
		Bitmap image = GameHelper.getBitmap("equip.png");
		int frameWidth = image.getWidth() / 2;
		int frameHeight = image.getHeight();
		Equipment equip = new Equipment(image, frameWidth, frameHeight, type);
		
		return equip;
	}

	
	public int getType() {
		return type;
	}
	
	
	public void move() {
		move(0, speed[speedIndex]);
		speedIndex = (speedIndex == speed.length - 1)? speedIndex : speedIndex + 1;
		if(getY() > GameHelper.screenHeight)
			setVisible(false);
	}

}
