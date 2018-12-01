package javax.microedition.lcdui.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public abstract class Layer {
	
	int x; 
	
	int y; 
	
	int width; 
	
	int height; 
	
	
	boolean visible = true;
	
	Rect rect_src, rect_dst;
	
	
	
	protected Layer(int width, int height) {
	    setWidthImpl(width);
	    setHeightImpl(height);
	}	
	
	
	public void setPosition(int x, int y) {
	    this.x = x;
	    this.y = y;
	}
	
	
	public void move(int dx, int dy) {	
	    x += dx;
	    y += dy;
	}
	
	
	public final int getX() {
	    return x;
	}
	
	
	public final int getY() {
	    return y;
	}
	
	
	public final int getWidth() {
	return width;
	}
	
	
	public final int getHeight() {
	return height;
	}
	
	
	public void setVisible(boolean visible) {
	    this.visible = visible;
	}
	
	
	public final boolean isVisible() {
	    return visible;
	}
	
	
	public abstract void paint(Canvas canvas);
	
	
	void setWidthImpl(int width) { 
	    if (width < 0) {
	        throw new IllegalArgumentException();
	    }
	    this.width = width;
	}
	
	
	
	void setHeightImpl(int height) {
	    if (height < 0) {
	        throw new IllegalArgumentException();
	    }
	    this.height = height;
	}
	
	protected void drawImage(Canvas canvas, int x, int y,
			Bitmap bsrc, int sx, int sy, int w, int h) {
		if(rect_src == null)
			rect_src = new Rect();
		rect_src.left = sx;
		rect_src.right = sx + w;
		rect_src.top = sy;
		rect_src.bottom = sy + h;

		if(rect_dst == null)
			rect_dst = new Rect();
		rect_dst.left = x;
		rect_dst.right = x + w;
		rect_dst.top = y;
		rect_dst.bottom = y + h;
		canvas.drawBitmap(bsrc, rect_src, rect_dst, null);
	}

}
