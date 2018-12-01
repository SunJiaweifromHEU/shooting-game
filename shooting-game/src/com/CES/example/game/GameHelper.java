package com.CES.example.game;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;


@SuppressLint("UseSparseArrays")
public class GameHelper {
	
	static Context context;
	static int screenWidth;
	static int screenHeight;
	
	static MediaPlayer mediaPlayer;
	static SoundPool soundPool;
	static HashMap<Integer, Integer> soundPoolMap;
	
	static String[] fileNames;
	static Bitmap[] images;
	
	static List<Enemy> dustEnemys;
	static Random random;
	static long time;
	
	
	public static void init(Context c, int w, int h) {
		context = c;
		screenWidth = w;
		screenHeight = h;
		
		
	    mediaPlayer = MediaPlayer.create(c, R.raw.game_music); 
	    mediaPlayer.setVolume(1f, 1f);
		mediaPlayer.setLooping(true); 
		
		
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
	    soundPoolMap = new HashMap<Integer, Integer>();
	    soundPoolMap.put(R.raw.button, 
	    		soundPool.load(c, R.raw.button, 1));
	    soundPoolMap.put(R.raw.enemy1_down, 
	    		soundPool.load(c, R.raw.enemy1_down, 1));
	    soundPoolMap.put(R.raw.enemy2_down, 
	    		soundPool.load(c, R.raw.enemy2_down, 1));
	    soundPoolMap.put(R.raw.enemy3_down, 
	    		soundPool.load(c, R.raw.enemy3_down, 1));
	    soundPoolMap.put(R.raw.fire, 
	    		soundPool.load(c, R.raw.fire, 1));
	    soundPoolMap.put(R.raw.game_over, 
	    		soundPool.load(c, R.raw.game_over, 1));
	    soundPoolMap.put(R.raw.get_bomb, 
	    		soundPool.load(c, R.raw.get_bomb, 1));
	    soundPoolMap.put(R.raw.get_double_gun, 
	    		soundPool.load(c, R.raw.get_double_gun, 1));
	    soundPoolMap.put(R.raw.use_bomb, 
	    		soundPool.load(c, R.raw.use_bomb, 1));
		
	    
	    fileNames = new String[] {"background.png", "player.png", 
				"enemy1.png", "enemy2.png", "enemy3.png", "equip.png",
				"bullet.png", "bomb.png", "pause.png"};
		images = new Bitmap[fileNames.length];
		try {
			AssetManager am = c.getResources().getAssets();
			for(int i = 0; i < images.length; i++) {
				InputStream is = am.open(fileNames[i]);
				images[i] = BitmapFactory.decodeStream(is);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		dustEnemys = new LinkedList<Enemy>();
		random = new Random();
		time = 0;
	}
	
	
	public static void playSound(int id) {
		Integer ID = soundPoolMap.get(id);
		if(ID == null)
			return;
		
		int soundId = ID.intValue();
		soundPool.play(soundId, 1, 1, 0, 0, 1);
	}
	
	
	public static Bitmap getBitmap(String fileName){
		Bitmap image = null;
		for(int i = 0; i < fileNames.length; i++) {
			if(fileName.equals(fileNames[i])) {
				image = images[i];
				break;
			}
		}
		return image;
	}
	
	
	public static Equipment createEquipment() {
		int r = random.nextInt(0xFFFF);
		int type = r & 0x1;
		Equipment e = Equipment.createEquipment(type);
		int x = (r >>> 1) % (screenWidth - e.getWidth());
		e.setPosition(x, -50);
		return e;
	}
	
	
	public static void refreshEnemy(List<Enemy> enemys, boolean isNew) {
		for(Enemy e : enemys) {
			if(!e.isVisible())
				dustEnemys.add(e);
		}
		enemys.removeAll(dustEnemys);
		
		if(!isNew)
			return;

		
		int newEnemy = (time < 20000)? 30 : (time < 60000)? 50 : 
			(time < 240000)? 70 : (time < 6000000)? 80 : 90;
		int maxEnemy2 = (time < 10000)? 0 : (time < 60000)? 1 : 
			(time < 180000)? 2 : (time < 360000)? 3 : (time < 720000)? 4 : 5;
		int maxEnemy3 = (time < 20000)? 0 : (time < 600000)? 1 : 2;
		int maxSpeed1 = (time < 20000)? 1 : (time < 60000)? 2 : 3;
		int maxSpeed2 = (time < 60000)? 1 : (time < 300000)? 2 : 3;
		int maxSpeed3 = (time < 240000)? 1 : 2;
		
		int enemy2 = 0, enemy3 = 0;
		for(Enemy e : enemys) {
			int t = e.getType();
			if(t == 2) enemy2++;
			else if(t == 3) enemy3++;
		}

		int r = random.nextInt(0xFFFFFF);
		
		
		int ne = r & 0xFF;
		if(ne >= newEnemy)
			return;
		
		
		int type = (r >>> 8) & 0x3;
		if(type == 0)
			type = 1;
		if((type == 2 && enemy2 >= maxEnemy2) || (type == 3 && enemy3 >= maxEnemy3))
			type = 1;
		
		
		int speed = (r >>> 10) & 0x3;
		if(speed == 0 || (type == 1 && speed > maxSpeed1) || 
				(type == 2 && speed > maxSpeed2) || (type == 3 && speed > maxSpeed3))
			speed = 1;
		if(type == 1) speed = 2 + 6 * speed;
		else if(type == 2) speed = 2 + 4 * speed;
		else if(type == 3) speed = 1 + 3 * speed;
		
		int i;
		for(i = 0; i < dustEnemys.size(); i++) {
			if(dustEnemys.get(i).getType() == type)
				break;
		}
		Enemy e = null;
		if(i < dustEnemys.size())
			e = dustEnemys.remove(i);
		else
			e = Enemy.createEnemy(type);
		
		
		int x = (r >>> 12) % (screenWidth - e.getWidth());
		int y = -e.getHeight();
		e.relive(speed, x, y);
		enemys.add(e);
	}
	
	
	public static void collideDetect(Player player, List<Enemy> enemys) {
		for(Enemy e : enemys) {
			if(player.isAlive() && e.isAlive() && player.collidesWith(e, false)) {
				e.hited();
				player.knocked();
			}
		}
	}
	
	
	public static int collideDetect(List<Bullet> bullets, List<Enemy> enemys) {
		int score = 0;
		for(int i = 0; i < 7; i++) {
			for(Bullet b : bullets) {
				b.move();
				for(Enemy e : enemys) {
					if(b.isVisible() && e.isAlive() && e.collidesWith(b, false)) {
						e.hited();
						b.setVisible(false);
						if(!e.isAlive()) {
							score += e.getScore();
						}
					}
				}
			}
		}
		return score;
	}
	
	
	public static void collideDetect(Player player, Equipment equip) {
		if(!player.isAlive() || equip == null || !equip.isVisible())
			return;
		
		if(player.collidesWith(equip, false)){
			int type = equip.getType();
			
			if(type == Equipment.TYPE_DOUBLE)
				player.doubleGun();
			else if(type == Equipment.TYPE_BOMB)
				player.addBomb();
			equip.setVisible(false);
		}
	}
}
