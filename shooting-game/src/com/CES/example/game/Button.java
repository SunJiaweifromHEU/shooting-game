package com.CES.example.game;

import javax.microedition.lcdui.game.Layer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;


public class Button extends Layer {
	
	private int id;
	private Bitmap src;
	private String text;
	private int step = 0;
	
	private Paint paint;
	private RectF rect;
	private Rect rectSrc;
	private int centerX;
	private int centerY;
	private int type;
	
	private OnClickListener listener;

	
	public Button(int id, String text, int width, int height) {
		super(width, height);
		this.id = id;
		this.text = text;
		this.type = 1;

		this.paint = new Paint();
		this.paint.setAntiAlias(true);
		this.paint.setDither(true);
		this.paint.setStrokeJoin(Paint.Join.ROUND);
		this.paint.setStrokeCap(Paint.Cap.ROUND);
		this.paint.setTextSize(25.0f);
		
		rect = new RectF();
		rectSrc = new Rect();
		centerX = (getX() + width) / 2;
		centerY = (getY() + height) / 2;
	}
	
	
	public Button(int id, Bitmap src) {
		super(src.getWidth(), src.getHeight());
		this.id = id;
		this.src = src;
		this.type = 2;
		
		rectSrc = new Rect();
		centerX = (getX() + src.getWidth()) / 2;
		centerY = (getY() + src.getHeight()) / 2;
	}

	
	@Override
	public void paint(Canvas canvas) {
		if(type == 1)
			paintType1(canvas);
		else
			paintType2(canvas);
	}
	
	
	private void paintType1(Canvas canvas) {
		if(isVisible())
			step = (step < 3)? step + 1 : 3;
		else
			step = (step > 0)? step - 1 : 0;
		
		if(step <= 0)
			return;

		int alpha = 0;
		int w = getWidth();
		int h = getHeight();
		if(step == 1) {
			rect.set(centerX - w / 6, centerY - h / 6, 
					centerX + w / 6, centerY + h / 6);
			alpha = 20;
		}
		else if(step == 2) {
			rect.set(centerX - w / 3, centerY - h / 3, 
					centerX + w / 3, centerY + h / 3);
			alpha = 80;
		}
		else if(step == 3) {
			rect.set(centerX - w / 2, centerY - h / 2, 
					centerX + w / 2, centerY + h / 2);
			alpha = 255;
		}
		
		paint.setColor(Color.argb(alpha, 220, 220, 220));
		paint.setStyle(Paint.Style.FILL);
		canvas.drawRoundRect(rect, rect.height() / 2, rect.height() / 2, paint);
		paint.setColor(Color.argb(alpha, 0, 0, 0));
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3.0f);
		canvas.drawRoundRect(rect, rect.height() / 2, rect.height() / 2, paint);
		if(step == 3) {
			paint.setStyle(Paint.Style.FILL);
			paint.setStrokeWidth(1.0f);
			paint.getTextBounds(text, 0, text.length(), rectSrc);
			canvas.drawText(text, centerX - rectSrc.width() / 2, 
					centerY + rectSrc.height() / 2 - 2, paint);
		}
	}
	
	
	private void paintType2(Canvas canvas) {
		if(!isVisible())
			return;
		
		rectSrc.left = getX();
		rectSrc.top = getY();
		rectSrc.right = rectSrc.left + getWidth();
		rectSrc.bottom = rectSrc.top + getHeight();
		
		canvas.drawBitmap(src, null, rectSrc, null);
	}
	
	
	public void onTouch(int act, int x, int y) {
		if(!isVisible())
			return;
		
		if(act == MotionEvent.ACTION_UP || act == MotionEvent.ACTION_POINTER_UP) {
			int left = getX();
			int right = left + getWidth();
			int top = getY();
			int bottom = top + getHeight();
			if(listener != null && 
					x >= left && x < right && y >= top && y < bottom) {
				GameHelper.playSound(R.raw.button);
				listener.onClick(id);
			}
		}
	}

	
	public void setText(String text) {
		this.text = text;
	}
	
	
	public void setCenterPosition(int x, int y) {
		int dx = x - centerX;
		int dy = y - centerY;
		move(dx, dy);
		centerX = x;
		centerY = y;
	}
	
	
	public void setOnClickListener(OnClickListener l) {
		this.listener = l;
	}

	
	public interface OnClickListener {
		void onClick(int id);
	}

}
