package com.camera.camera2testbed;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String TAG = "Camera2TestBed";
    private TextureView mTextureView;
    private CameraManager mCameraManager = null;
    private Size mPreviewSize;
    private String mCameraId = "null";
    private CameraDevice mCameraDevice;
    private int mPreviewFormat = -1;
    private ImageReader mPreviewReader = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }
    private void initData() {
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
    }
    private void initView() {
        mTextureView = (TextureView) findViewById(R.id.tv_camera);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (null == mCameraManager) {
            Log.e(TAG, "camera manager is null...!");
            return;
        }
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                // 1.TextureView可用的时候配置相机
                initCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initCamera() {
        // 2.配置前置相机，获取尺寸及id
        if (false == getCameraIdAndPreviewSizeByFacing(CameraCharacteristics.LENS_FACING_FRONT)) {
            return;
        }
        // 3.打开相机
        openCamera();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void openCamera() {
        try {
            // 4.权限检查
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestCameraPermission();
                return;
            }
            // 5.真正打开相机
            mCameraManager.openCamera(mCameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    mCameraDevice = camera;// 打开成功，保存代表相机的CameraDevice实例
                    SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
                    surfaceTexture.setDefaultBufferSize(mTextureView.getWidth(), mTextureView.getHeight());
                    Surface surface = new Surface(surfaceTexture);
                    ArrayList<Surface> previewList = new ArrayList<>();
                    previewList.add(surface);
                    previewList.add(mPreviewReader.getSurface());
                    try {
                        // 6.将TextureView的surface传递给CameraDevice
                        mCameraDevice.createCaptureSession(previewList, new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(@NonNull CameraCaptureSession session) {
                                try {
                                    CaptureRequest.Builder builder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                                    builder.addTarget(surface); // 必须设置才能正常预览
                                    builder.addTarget(mPreviewReader.getSurface());
                                    CaptureRequest captureRequest = builder.build();
                                    // 7.CameraCaptureSession与CaptureRequest绑定（这是最后一步，已可显示相机预览）
                                    session.setRepeatingRequest(captureRequest, new CameraCaptureSession.CaptureCallback() {
                                        @Override
                                        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
                                            super.onCaptureProgressed(session, request, partialResult);
                                        }

                                        @Override
                                        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                                            super.onCaptureCompleted(session, request, result);
                                        }
                                    },null);
                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                            }
                        },null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    releaseCamera();
                    if (mPreviewReader != null) {
                        mPreviewReader.close();
                    }
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    releaseCamera();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    private void releaseCamera() {
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    /*获取cameraId及相机预览的最佳尺寸*/
    private boolean getCameraIdAndPreviewSizeByFacing(int lensFacingFront) {
        try {
            String[] cameraIdList = mCameraManager.getCameraIdList();
            for (String cameraId : cameraIdList) {
                CameraCharacteristics cameraCharacteristics = mCameraManager.getCameraCharacteristics(cameraId);
                int facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                if(facing != lensFacingFront) {
                    Log.e(TAG, "camera not support facing front...!");
                    continue;
                }
                StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                int[] formats = streamConfigurationMap.getOutputFormats();
                for (int format : formats) {
                    Log.d(TAG, "camera support format:" + format);
                }
                for (int format : formats) {
                    if (ImageFormat.NV21 == format) {
                        mPreviewFormat = format;
                        Log.d(TAG, "camera support format NV21!");
                        break;
                    } else if (ImageFormat.YUV_420_888 == format) {
                        mPreviewFormat = format;
                        Log.d(TAG, "camera support format YUV420_888!");
                        break;
                    }
                }
                if (-1 == mPreviewFormat) {
                    return false;
                }
                Size[] outputSizes = streamConfigurationMap.getOutputSizes(mPreviewFormat);
                for(Size size : outputSizes) {
                    Log.d(TAG,"camera support preview size width:" + size.getWidth() + " height:" + size.getHeight());
                }
                mCameraId = cameraId;
                // 找到camera支持的尺寸最接近textView的尺寸
                mPreviewSize = setOptimalPreviewSize(outputSizes, mTextureView.getMeasuredWidth(), mTextureView.getMeasuredHeight());
                Log.d(TAG, "preview width:" + mPreviewSize.getWidth() + " preview height:" + mPreviewSize.getHeight());
                mPreviewReader = ImageReader.newInstance(mPreviewSize.getWidth(), mPreviewSize.getHeight(), mPreviewFormat,3);
                if (null == mPreviewReader) {
                    Log.e(TAG, "ImageReader new instance failed...!");
                    return false;
                }
                mPreviewReader.setOnImageAvailableListener(mPreviewImageAvailableListener, null);
                Log.d(TAG,"最佳预览尺寸width:" + mPreviewSize.getWidth() + " height:" + mPreviewSize.getHeight() + ", 相机id：" + mCameraId);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return mCameraId != "null";
    }
    /*根据相机可用的预览尺寸和用户给定的TextureView的显示尺寸选择最接近的预览尺寸*/
    private Size setOptimalPreviewSize(Size[] sizes, int previewViewWidth, int previewViewHeight) {
        List<Size> bigEnoughSizes = new ArrayList<>();
        List<Size> notBigEnoughSizes = new ArrayList<>();
        for (Size size : sizes) {
            if (size.getWidth() >= previewViewWidth && size.getHeight() >= previewViewHeight) {
                bigEnoughSizes.add(size);
            } else {
                notBigEnoughSizes.add(size);
            }
        }
        if (bigEnoughSizes.size() > 0) {
            return Collections.min(bigEnoughSizes, new Comparator<Size>() {
                @Override
                public int compare(Size lhs, Size rhs) {
                    return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                            (long) rhs.getWidth() * rhs.getHeight());
                }
            });
        } else if (notBigEnoughSizes.size() > 0) {
            return Collections.max(notBigEnoughSizes, new Comparator<Size>() {
                @Override
                public int compare(Size lhs, Size rhs) {
                    return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                            (long) rhs.getWidth() * rhs.getHeight());
                }
            });
        } else {
            Log.d(TAG, "未找到合适的预览尺寸");
            return sizes[0];
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }
    // 权限请求结果处理 权限通过 打开相机
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "请授予相机权限！", Toast.LENGTH_SHORT).show();
            } else {
                openCamera();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    private ImageReader.OnImageAvailableListener mPreviewImageAvailableListener = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader imageReader) {
            final Image img = imageReader.acquireNextImage();
            Log.d(TAG, "read image width:" + img.getWidth() + " height:" + img.getHeight());
            Log.d(TAG,"Capture Y size " + img.getPlanes()[0].getBuffer().remaining());
            Log.d(TAG,"Capture U size " + img.getPlanes()[1].getBuffer().remaining());
            Log.d(TAG,"Capture V size " + img.getPlanes()[2].getBuffer().remaining());
            img.close();
        }
    };
}