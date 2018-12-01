package javax.microedition.lcdui.game;

import android.graphics.Canvas;

public class LayerManager {

	
	private int nlayers;

	
	private Layer component[] = new Layer[4];

	
	private int viewX, viewY, viewWidth, viewHeight; 
	
	public LayerManager() {
		setViewWindow(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	
	public void append(Layer l) {
		
		removeImpl(l);
		addImpl(l, nlayers);
	}

	
	public void insert(Layer l, int index) {
		
		if ((index < 0) || (index > nlayers)) {
			throw new IndexOutOfBoundsException();
		}
		removeImpl(l);
		
	}

	
	public Layer getLayerAt(int index) {
		if ((index < 0) || (index >= nlayers)) {
			throw new IndexOutOfBoundsException();
		}
		return component[index];
	}

	
	public int getSize() {
		return nlayers;
	}

	
	public void remove(Layer l) {
		removeImpl(l);
	}

	
	public void paint(Canvas canvas, int x, int y) {
		canvas.translate(x - viewX, y - viewY);
		canvas.clipRect(viewX, viewY, viewX + viewWidth, viewY + viewHeight);
		for (int i = nlayers; --i >= 0;) {
			Layer comp = component[i];
			if (comp.visible) {
				comp.paint(canvas);
			}
		}
		canvas.translate(-x + viewX, -y + viewY);

		canvas.restore();
	}

	
	public void setViewWindow(int x, int y, int width, int height) {

		if (width < 0 || height < 0) {
			throw new IllegalArgumentException();
		}

		viewX = x;
		viewY = y;
		viewWidth = width;
		viewHeight = height;
	}

	
	private void addImpl(Layer layer, int index) {
		if (nlayers == component.length) {
			Layer newcomponents[] = new Layer[nlayers + 4];
			System.arraycopy(component, 0, newcomponents, 0, nlayers);
			System.arraycopy(component, index, newcomponents, index + 1,
					nlayers - index);
			component = newcomponents;
		} else {
			System.arraycopy(component, index, component, index + 1, nlayers
					- index);
		}

		component[index] = layer;
		nlayers++;
	}

	
	private void removeImpl(Layer l) {
		if (l == null) {
			throw new NullPointerException();
		}
		for (int i = nlayers; --i >= 0;) {
			if (component[i] == l) {
				remove(i);
			}
		}
	}


	private void remove(int index) {
		System.arraycopy(component, index + 1, component, index, nlayers
				- index - 1);
		component[--nlayers] = null;
	}
	
}
