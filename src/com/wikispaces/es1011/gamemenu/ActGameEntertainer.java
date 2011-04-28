package com.wikispaces.es1011.gamemenu;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActGameEntertainer extends Activity implements
		SensorEventListener, IUpdatable, SurfaceHolder.Callback {

	public static Display display;
	public TextView tv;
	private SensorManager mSensorManager;
	private PowerManager mPowerManager;
	private WindowManager mWindowManager;
	private FrameLayout lGameFrame;

	private Game_SurfaceThread thread;

	private Game_Status gs;
	private IGameSurface actuallyShownView;
	private Game_GameSurfaceView gsw;
	private Game_ReadySurfaceView rsw;

	private LinearLayout gameLayout;
	private Sensor mAccelerometer;

	private Game_Ball ball;
	private Game_Pad pad;
	private Game_BrickMatrix brickMatrix;
	private Rect underRect;
	private int viewHeight, viewWidth;
	private int brickNum = 20;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		display = getWindowManager().getDefaultDisplay();
		// Get an instance of the SensorManager
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		// Get an instance of the PowerManager
		mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);

		// Get an instance of the WindowManager
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		mWindowManager.getDefaultDisplay();

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
				getClass().getName());

		/**
		 * Creo il thread per il disegno su schermo
		 */
		thread = new Game_SurfaceThread(this);

		/**
		 * Crea le surface view
		 */
		gs = new Game_Status();
		gsw = new Game_GameSurfaceView(this, ball, pad, brickMatrix, underRect);
		gsw.getHolder().addCallback(this);
		// rsw = new Game_ReadySurfaceView(this,viewHeight, viewWidth, ball,pad,
		// brickMatrix,thread);

		/**
		 * Istanzio l'accelerometro
		 */
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		mSensorManager.registerListener((SensorEventListener) this,
				mAccelerometer, SensorManager.SENSOR_DELAY_GAME);

		lGameFrame = new FrameLayout(this);
		// lGameFrame.addView(rsw);
		lGameFrame.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		actuallyShownView = gsw;

		gs.setState(Game_Status.Status.READY);

		setContentView((View) actuallyShownView);
	}

	public void init() {
		// create a graphic
		ball = new Game_Ball(viewWidth, viewHeight, gsw);
		pad = new Game_Pad(viewWidth, viewHeight, gsw);
		brickMatrix = new Game_BrickMatrix(viewWidth, viewHeight, gsw, brickNum);
		underRect = new Rect(0, viewHeight - pad.getHeight(), viewWidth,
				viewHeight);
	}

	@Override
	public void onPause() {
		super.onPause();

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() != Sensor.TYPE_ORIENTATION) {
			return;
		}
		pad.directionX = (int) -event.values[2];
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
	}

	public void update() {

		Rect[] box = pad.getBox();
		if (box[0].intersect(ball.getBox())) {
			ball.directionX = -4;
			ball.directionY = -2;
		}
		if (box[1].intersect(ball.getBox())) {
			ball.directionX = -3;
			ball.directionY = -2;
		}
		if (box[2].intersect(ball.getBox())) {
			ball.directionX = -5;
			ball.directionY = 0;
		}
		if (box[3].intersect(ball.getBox())) {
			ball.directionX = 3;
			ball.directionY = -2;
		}
		if (box[4].intersect(ball.getBox())) {
			ball.directionX = 4;
			ball.directionY = -2;
		}

		for (int j = 0; j < brickMatrix.getRow(); j++) {
			for (int i = 0; i < brickMatrix.getColumn(); i++) {
				if (brickMatrix.getBrick(i, j).isVisible()
						&& ball.getBox().intersect(
								brickMatrix.getBrick(i, j).getBox())) {
					brickMatrix.getBrick(i, j).Update(gs.getGameTime(), false);

					if (ball.getBox().intersect(
							brickMatrix.getBrick(i, j).getBox().left,
							brickMatrix.getBrick(i, j).getBox().top,
							brickMatrix.getBrick(i, j).getBox().left + 1,
							brickMatrix.getBrick(i, j).getBox().bottom)
							|| ball
									.getBox()
									.intersect(
											brickMatrix.getBrick(i, j).getBox().right - 1,
											brickMatrix.getBrick(i, j).getBox().top,
											brickMatrix.getBrick(i, j).getBox().right,
											brickMatrix.getBrick(i, j).getBox().bottom)) {
						ball.directionX = -ball.directionX;
					} else {
						ball.directionY = -ball.directionY;
					}

					gs.setScore(100);
				}

			}
		}

		// TODO rimuovere dipendenza dal tempo di ball e pad
		if (!ball.Update(gs.getGameTime())) {
			ball = new Game_Ball(viewWidth, viewHeight, gsw);
			if (gs.getLives() > 0) {
				gs.setLives(gs.getLives() - 1);
			} else // lose:
			{
				gs.setState(Game_Status.Status.LOSE);
			}
		}
		pad.Update(gs.getGameTime());

	}

	public void forceUpdate() {
		gs.setGameTime(System.currentTimeMillis());
		actuallyShownView.viewUpdate();
	}

	// ----------------------------------------------------------------------------SurfaceHolder.Callback
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	public void surfaceCreated(SurfaceHolder holder) {
		viewWidth = holder.getSurfaceFrame().width();
		viewHeight = holder.getSurfaceFrame().height();
		
		actuallyShownView.initDimension(viewWidth, viewHeight);

		/**
		 * Crea gli oggetti del gioco
		 */
		init();

		thread.setRunning(true);
		thread.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		thread.setRunning(false);
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}
}
