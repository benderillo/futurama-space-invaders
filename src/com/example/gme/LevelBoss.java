package com.example.gme;

import java.util.Random;

public class LevelBoss extends EnemyFlight {

	public class Vertex {
		public float x;
		public float y;
		
		public float getX() {
			return x;
		}
		public void setX(float x) {
			this.x = x;
		}
		public float getY() {
			return y;
		}
		public void setY(float y) {
			this.y = y;
		}
	};

	protected static final int MAX_POINTS = 16;
	protected Vertex[] path = new Vertex[MAX_POINTS];

	private float[] MinX = {0f,   0.5f, 0f,   0.5f};
	private float[] MaxX = {0.5f, 1f,   0.5f, 1f};

	private float[] MinY = {0f,   0,    0.5f, 0.5f};
	private float[] MaxY = {0.5f, 0.5f, 1f,   1f};

	public LevelBoss(int left, int top, int right, int bottom, int speed,
			int color) {
		super(left, top, right, bottom, speed, color);

		Random rand = new Random();		
		rand.setSeed(System.currentTimeMillis());

		float minX, minY, maxX, maxY = 0;

		for (int j = 0; j < MAX_POINTS; j+=4) {
			for (int i = 0; i < MAX_POINTS/4; ++i) {
				minX = MinX[j/4];
				minY = MinY[j/4];
				maxX = MaxX[j/4];
				maxY = MaxY[j/4];

				path[j+i] = new Vertex();
				path[j+i].setX(minX + rand.nextFloat()*(maxX - minX));
				path[j+i].setY(minY + rand.nextFloat()*(maxY - minY));
			}
		}
		
		//now we need to set the boss on the path
	}

	private boolean defeated = false;
	
	public boolean isDefeated() { return defeated; }
	public void setDefeated(boolean value) { defeated = value; }
}
