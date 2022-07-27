package com.camera.camera1testbed;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BasicCamera1 {
    private static final String TAG = "Camera1TestBed";
    private Camera mCamera = null;
    private SurfaceHolder mHolder = null;
    private int mPreviewWidth = 1280;
    private int mPreviewHeight = 720;
    private int mSaveCount = 10;
    private int mCount = 0;
    public boolean initCamera(int width, int height, SurfaceHolder holder, int id) {
        mPreviewWidth = width;
        mPreviewHeight = height;
        mHolder = holder;
        try {
            mCamera = Camera.open(id);
            Log.d(TAG, "SupportedPreviewFormats:" + mCamera.getParameters().getSupportedPreviewFormats().toString());
            mCamera.setPreviewDisplay(mHolder);
            mCamera.addCallbackBuffer(new byte[mPreviewWidth * mPreviewHeight * 3 / 2]);
            mCamera.setPreviewCallbackWithBuffer(this.mPreviewCallback);
            mCamera.getParameters().setPreviewSize(mPreviewWidth, mPreviewHeight);
            //mCamera.getParameters().setPreviewFormat(ImageFormat.NV21); // 不需要调用
            mCamera.startPreview();
        } catch (Exception e) {
            Log.e(TAG, "open camera failed:" + e.toString());
            return false;
        }
        return true;
    }
    public void closeCamera(){
        if (null == mCamera) {
            return;
        }
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }
    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] bytes, Camera camera) {
            camera.addCallbackBuffer(bytes);
            if (++mCount > mSaveCount) {
                return;
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            timeStamp += "_" + System.currentTimeMillis();
            String fileName = "/sdcard/" + "NV12" + "_" + timeStamp + "_" + "1280" + "x" + "720" + "." + "NV12";
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(fileName, true);
                output.write(bytes);
                Log.d(TAG, "preview frame size:" + bytes.length);
            } catch (IOException e) {
                Log.e(TAG, "YUV:file error:" + e.toString());
            }
            finally {
                try {
                    output.close();
                } catch (IOException e) {
                }
            }
        }
    };
}
