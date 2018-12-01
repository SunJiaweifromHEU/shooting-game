package javax.microedition.lcdui.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class TiledLayer extends Layer {

	private int cellHeight; 

	private int cellWidth; 

	private int rows; 

	private int columns; 

	private int[][] cellMatrix; 

	Bitmap sourceImage; 

	private int numberOfTiles; 

	int[] tileSetX;

	int[] tileSetY;

	private int[] anim_to_static; 

	private int numOfAnimTiles;

	public TiledLayer(int columns, int rows, Bitmap image, int tileWidth,
			int tileHeight) {
		
		super(columns < 1 || tileWidth < 1 ? -1 : columns * tileWidth, rows < 1
				|| tileHeight < 1 ? -1 : rows * tileHeight);

		if (((image.getWidth() % tileWidth) != 0)
				|| ((image.getHeight() % tileHeight) != 0)) {
			throw new IllegalArgumentException();
		}
		this.columns = columns;
		this.rows = rows;

		cellMatrix = new int[rows][columns];

		int noOfFrames = (image.getWidth() / tileWidth)
				* (image.getHeight() / tileHeight);
		
		createStaticSet(image, noOfFrames + 1, tileWidth, tileHeight, true);
	}

	public int createAnimatedTile(int staticTileIndex) {
		if (staticTileIndex < 0 || staticTileIndex >= numberOfTiles) {
			throw new IndexOutOfBoundsException();
		}

		if (anim_to_static == null) {
			anim_to_static = new int[4];
			numOfAnimTiles = 1;
		} else if (numOfAnimTiles == anim_to_static.length) {
			int new_anim_tbl[] = new int[anim_to_static.length * 2];
			System.arraycopy(anim_to_static, 0, new_anim_tbl, 0,
					anim_to_static.length);
			anim_to_static = new_anim_tbl;
		}
		anim_to_static[numOfAnimTiles] = staticTileIndex;
		numOfAnimTiles++;
		return (-(numOfAnimTiles - 1));
	}

	public void setAnimatedTile(int animatedTileIndex, int staticTileIndex) {
		if (staticTileIndex < 0 || staticTileIndex >= numberOfTiles) {
			throw new IndexOutOfBoundsException();
		}
		animatedTileIndex = -animatedTileIndex;
		if (anim_to_static == null || animatedTileIndex <= 0
				|| animatedTileIndex >= numOfAnimTiles) {
			throw new IndexOutOfBoundsException();
		}

		anim_to_static[animatedTileIndex] = staticTileIndex;

	}

	public int getAnimatedTile(int animatedTileIndex) {
		animatedTileIndex = -animatedTileIndex;
		if (anim_to_static == null || animatedTileIndex <= 0
				|| animatedTileIndex >= numOfAnimTiles) {
			throw new IndexOutOfBoundsException();
		}

		return anim_to_static[animatedTileIndex];
	}

	public void setCell(int col, int row, int tileIndex) {

		if (col < 0 || col >= this.columns || row < 0 || row >= this.rows) {
			throw new IndexOutOfBoundsException();
		}

		if (tileIndex > 0) {
			if (tileIndex >= numberOfTiles) {
				throw new IndexOutOfBoundsException();
			}
		} else if (tileIndex < 0) {
			if (anim_to_static == null || (-tileIndex) >= numOfAnimTiles) {
				throw new IndexOutOfBoundsException();
			}
		}

		cellMatrix[row][col] = tileIndex;

	}

	public int getCell(int col, int row) {
		if (col < 0 || col >= this.columns || row < 0 || row >= this.rows) {
			throw new IndexOutOfBoundsException();
		}
		return cellMatrix[row][col];
	}

	public void fillCells(int col, int row, int numCols, int numRows,
			int tileIndex) {

		if (col < 0 || col >= this.columns || row < 0 || row >= this.rows
				|| numCols < 0 || col + numCols > this.columns || numRows < 0
				|| row + numRows > this.rows) {
			throw new IndexOutOfBoundsException();
		}

		if (tileIndex > 0) {
			if (tileIndex >= numberOfTiles) {
				throw new IndexOutOfBoundsException();
			}
		} else if (tileIndex < 0) {
			if (anim_to_static == null || (-tileIndex) >= numOfAnimTiles) {
				throw new IndexOutOfBoundsException();
			}
		}

		for (int rowCount = row; rowCount < row + numRows; rowCount++) {
			for (int columnCount = col; columnCount < col + numCols; columnCount++) {
				cellMatrix[rowCount][columnCount] = tileIndex;
			}
		}
	}
	public final int getCellWidth() {
		return cellWidth;
	}
	public final int getCellHeight() {
		return cellHeight;
	}
	public final int getColumns() {
		return columns;
	}
	public final int getRows() {
		return rows;
	}
	private void createStaticSet(Bitmap image, int noOfFrames, int tileWidth,
			int tileHeight, boolean maintainIndices) {

		cellWidth = tileWidth;
		cellHeight = tileHeight;

		int imageW = image.getWidth();
		int imageH = image.getHeight();

		sourceImage = image;

		numberOfTiles = noOfFrames;
		tileSetX = new int[numberOfTiles];
		tileSetY = new int[numberOfTiles];

		if (!maintainIndices) {
			for (rows = 0; rows < cellMatrix.length; rows++) {
				int totalCols = cellMatrix[rows].length;
				for (columns = 0; columns < totalCols; columns++) {
					cellMatrix[rows][columns] = 0;
				}
			}
			anim_to_static = null;
		}

		int currentTile = 1;

		for (int y = 0; y < imageH; y += tileHeight) {
			for (int x = 0; x < imageW; x += tileWidth) {

				tileSetX[currentTile] = x;
				tileSetY[currentTile] = y;

				currentTile++;
			}
		}
	}

	public final void paint(Canvas canvas) {

		if (canvas == null) {
			throw new NullPointerException();
		}

		if (visible) {
			int tileIndex = 0;

			int ty = this.y;
			for (int row = 0; row < cellMatrix.length; row++, ty += cellHeight) {

				int tx = this.x;
				int totalCols = cellMatrix[row].length;

				for (int column = 0; column < totalCols; column++, tx += cellWidth) {

					tileIndex = cellMatrix[row][column];
					
					if (tileIndex == 0) { 
						continue;
					} else if (tileIndex < 0) {
						tileIndex = getAnimatedTile(tileIndex);
					}

					drawImage(canvas, tx, ty, sourceImage, tileSetX[tileIndex],
							tileSetY[tileIndex], cellWidth, cellHeight);

				}
			}
		}
	}

	
	public void setStaticTileSet(Bitmap image, int tileWidth, int tileHeight) {
		if (tileWidth < 1 || tileHeight < 1
				|| ((image.getWidth() % tileWidth) != 0)
				|| ((image.getHeight() % tileHeight) != 0)) {
			throw new IllegalArgumentException();
		}
		setWidthImpl(columns * tileWidth);
		setHeightImpl(rows * tileHeight);

		int noOfFrames = (image.getWidth() / tileWidth)
				* (image.getHeight() / tileHeight);
		if (noOfFrames >= (numberOfTiles - 1)) {
			createStaticSet(image, noOfFrames + 1, tileWidth, tileHeight, true);
		} else {
			createStaticSet(image, noOfFrames + 1, tileWidth, tileHeight, false);
		}
	}

}
