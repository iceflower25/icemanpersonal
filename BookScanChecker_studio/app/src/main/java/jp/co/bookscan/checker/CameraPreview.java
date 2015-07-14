/*
 * Barebones implementation of displaying camera preview.
 * 
 * Created by lisah0 on 2012-02-24
 */
package jp.co.bookscan.checker;

import java.io.IOException;

import android.os.Build;
import android.util.Log;

import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.annotation.SuppressLint;
import android.content.Context;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private Camera camera;
    private PreviewCallback previewCallback;
    private AutoFocusCallback autoFocusCallback;
    private boolean isPreviewing;
    
    @SuppressLint("InlinedApi")
	@SuppressWarnings({ "deprecation" })
	public CameraPreview(Context context, Camera camera1,
                         PreviewCallback previewCb,
                         AutoFocusCallback autoFocusCb) {
        super(context);
        camera = camera1;
        previewCallback = previewCb;
        autoFocusCallback = autoFocusCb;
        isPreviewing = true;
        
        // API level must be >=9 
        Camera.Parameters parameters = camera.getParameters();
        for (String f : parameters.getSupportedFocusModes()) {
        	if (f == Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) {
        		parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        		autoFocusCallback = null;
        		break;
        	}
        }
        
	    // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        holder = getHolder();
        holder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
        	holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            Log.d("CameraPreview", "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void surfaceChanged(SurfaceHolder holder1, int format, int width, int height) {
        /*
         * If your preview can change or rotate, take care of those events here.
         * Make sure to stop the preview before resizing or reformatting it.
         */

    	if (holder.getSurface() == null){
          // preview surface does not exist
          return;
        }

    	
        // stop preview before making changes
        try {
            camera.cancelAutoFocus();
            camera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }

        try {
            // Hard code camera surface rotation 90 degs to match Activity view in portrait
            camera.setDisplayOrientation(90);
            camera.setPreviewDisplay(holder);
            if (isPreviewing) {
                camera.setPreviewCallback(previewCallback);
                camera.startPreview();
                camera.autoFocus(autoFocusCallback);
            }
        } catch (Exception e){
            Log.d("CameraPreview", "Error starting camera preview: " + e.getMessage());
        }
    }

    public void startPreview() {
    	if (isPreviewing == false) {
    		isPreviewing = true;
    		camera.setPreviewCallback(previewCallback);
    		camera.startPreview();
    		camera.autoFocus(autoFocusCallback);
    	}
    }

    public void stopPreview() {
    	if (isPreviewing == true) {
    		isPreviewing = false;
    		camera.setPreviewCallback(null);
    		camera.stopPreview();
    		camera.cancelAutoFocus();
    	}
    }
}
