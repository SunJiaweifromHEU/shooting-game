package com.CES.example.game;

import javax.microedition.lcdui.game.TiledLayer;

import android.graphics.Bitmap;
import android.graphics.Canvas;


public class BackGround {
	
	private TiledLayer layer;
	private int speed;
	
	private int tileColumns, tileRows;
	private int layerColumns, layerRows;

	
	public BackGround(int speed) {
		int tileWidth = 40;
		int tileHeight = 50;
		Bitmap image = GameHelper.getBitmap("background.png");

		tileColumns = image.getWidth() / tileWidth;
		tileRows = image.getHeight() / tileHeight;
		layerColumns = GameHelper.screenWidth / tileWidth + 1;
		layerRows = GameHelper.screenHeight / tileHeight + 1;

		
		layer = new TiledLayer(layerColumns, layerRows, 
				image, tileWidth, tileHeight);
		for(int i = 0; i < layerRows; i++) {
			for(int j = 0; j < layerColumns; j++) {
				int tile = (i % tileRows) * tileColumns + ((j % tileColumns) + 1);
				layer.setCell(j, i, tile);
			}
		}

		this.speed = speed;
	}
	
	
	public void scroll() {
		int y = layer.getY() + speed;
		int cellH = layer.getCellHeight();
		int dr = (y + cellH) / cellH;
		
		y -= dr * cellH;
		layer.setPosition(layer.getX(), y);

		int tileCount = tileColumns * tileRows;
		for(int i = 0; i < layerRows; i++) {
			for(int j = 0; j < layerColumns; j++) {
				int currenTile = layer.getCell(j, i);
				currenTile -= dr * tileColumns;
				if(currenTile <= 0)
					currenTile += tileCount;
				layer.setCell(j, i, currenTile);
			}
		}
	}
	
	
	public void paint(Canvas canvas) {
		layer.paint(canvas);
	}

	
	public int getSpeed() {
		return speed;
	}

	
	public void setSpeed(int speed) {
		this.speed = speed;
	}

}
