package com.wikispaces.es1011.gamemenu;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;

public class CameraShooter extends SurfaceView implements SurfaceHolder.Callback, OnClickListener {
	
	public CameraShooter(Context context) {
		super(context);
	}

	static final int FOTO_MODE = 0;
	Camera mCamera;
	boolean mPreviewRunning = false;
	SurfaceHolder sHolder;
	int iW, iH;
	
	Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] imageData, Camera c) {

			if (imageData != null) {

				//Intent mIntent = new Intent();

				//FileUtilities.StoreByteImage(mContext, imageData,
				//		 50, "ImageName");
				
				//mCamera.startPreview();
				
				//setResult(FOTO_MODE,mIntent);
				//finish();
				mPreviewRunning = false;
				mCamera.stopPreview();
			}
		}
	};

	public void surfaceCreated(SurfaceHolder holder) {
		
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		if (mPreviewRunning) {
			mCamera.stopPreview();
		
			Camera.Parameters p = mCamera.getParameters();
			p.setPreviewSize(160, 120);
			mCamera.setParameters(p);
			try {
				mCamera.setPreviewDisplay(holder);
			} catch (IOException e) {}
			mCamera.startPreview();
			mPreviewRunning = true;
		}
		
		sHolder = holder;
		iW = w;
		iH = h;
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		if(mPreviewRunning)
		{
			mCamera.stopPreview();
			mPreviewRunning = false;
			mCamera.release();
		}
	}

	public void onClick(View arg0)
	{
		if(mPreviewRunning)
			mCamera.takePicture(null, mPictureCallback, mPictureCallback);
		else
		{
			if(mCamera == null)
				mCamera = Camera.open();
			else
			{
				mCamera.release();
				mCamera = Camera.open();
			}
			
			Camera.Parameters p = mCamera.getParameters();
			p.setPreviewSize(160, 120);
			mCamera.setParameters(p);
			try {
				mCamera.setPreviewDisplay(sHolder);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			mCamera.startPreview();
			mPreviewRunning = true;
		}
	}
}