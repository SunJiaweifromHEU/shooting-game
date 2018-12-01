package com.CES.example.game;

import java.util.LinkedList;
import java.util.List;

import javax.microedition.lcdui.game.Sprite;

import android.graphics.Bitmap;


public class Bullet extends Sprite {
	
	public final static int TYPE1 = 0;
	public final static int TYPE2 = 1;
	
	private static int[][] sequence = new int[][]{{0}, {1}};
	
	private static List<Bullet> tmp;

	
	private Bullet(Bitmap image, int frameWidth, int frameHeight, int type) {
		super(image, frameWidth, frameHeight);
		setFrameSequence(sequence[type]);
	}
	
	
	public static Bullet createBullet(int type) {
		if(type != TYPE1 && type != TYPE2)
			throw new IllegalArgumentException("Unkown type.");
		
		Bitmap image = GameHelper.getBitmap("bullet.png");
		int frameWidth = image.getWidth() / 2;
		int frameHeight = image.getHeight();
		Bullet bullet = new Bullet(image, frameWidth, frameHeight, type);
		return bullet;
	}
	
	
	public static void clearBullets(List<Bullet> bullets) {
		if(tmp == null)
			tmp = new LinkedList<Bullet>();
		for(Bullet b : bullets) {
			if(!b.isVisible())
				tmp.add(b);
		}
		bullets.removeAll(tmp);
		tmp.clear();
	}
	
	
	public void move() {
		if(!isVisible())
			return;
		
		move(0, -getHeight());
		if(getY() < -getHeight())
			setVisible(false);
	}

}
