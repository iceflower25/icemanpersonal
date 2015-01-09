package jp.co.bookscan.checker;



import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;



public class ReaderActivity extends FragmentActivity {
    private Camera camera;
    private CameraPreview preview;
    private Handler autoFocusHandler;
    
    private ImageView ivOverlay;
    private Bitmap bmOverlay = null;
    private BookInfoTask biTask = null;
    String strISBNCode;
    
    ImageScanner scanner;

    boolean isPaused = false;

    private boolean previewing = true;
    private boolean hasFocus = false;
    
    static {
        System.loadLibrary("iconv");
    } 
   
   @SuppressLint("NewApi")
   @Override
    protected void onCreate(Bundle state) {

    	super.onCreate(state);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.reader);
        
        /*ActionBarの左のアイコンがクリックできるため追加*/        
        if(android.os.Build.VERSION.SDK_INT >= 14) {
        	getActionBar().setHomeButtonEnabled(true);
        }
        
        autoFocusHandler = new Handler();
        camera = getCameraInstance();

        if (camera == null) {
            //no back-facing camera
    		return;
        }
             
        int DENSITY = 1;
        /* Instance barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, DENSITY);
        scanner.setConfig(0, Config.Y_DENSITY, DENSITY);
        scanner.setConfig(0, Config.ENABLE, 0);
        scanner.setConfig(Symbol.ISBN13, Config.ENABLE, 1);
        scanner.setConfig(Symbol.EAN13, Config.ENABLE, 1);

        preview = new CameraPreview(this, camera, previewCb, autoFocusCB);

		FrameLayout flPreview = (FrameLayout)findViewById(R.id.cameraPreview);
        flPreview.addView(preview);
        ivOverlay = new ImageView(this);
        flPreview.addView(ivOverlay);
        
        //for reconstruction
		previewing = false;
		ivOverlay.setVisibility(View.GONE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);

        if (bmOverlay == null)
            setOverlayImage();

        //skip at finishing
        if (isPaused) return;
                
        this.hasFocus = hasFocus;

		if (hasFocus) {
            previewing = true;
            ivOverlay.setVisibility(View.VISIBLE);
            preview.startPreview();
		} else {
			previewing = false;
			ivOverlay.setVisibility(View.GONE);
			/*
			preview.stopPreview();
        	autoFocusHandler.removeCallbacks(doAutoFocus);
        	*/
		}
    }


    public void onPause() {

    	isPaused = true;
    	
        super.onPause();
        if (biTask != null && biTask.getStatus() != AsyncTask.Status.FINISHED) {
        	biTask.cancel(true);
        }
        
        FrameLayout flPreview = (FrameLayout)findViewById(R.id.cameraPreview);
        flPreview.removeView(ivOverlay);
        flPreview.removeView(preview);
        ivOverlay.setVisibility(View.GONE);
        preview.stopPreview(); //neccessary?
        autoFocusHandler.removeCallbacks(doAutoFocus);
        preview = null;
        releaseCamera();
    }

    @Override
    protected void onResume() {
    	if (isPaused) isPaused = false;
    	
        super.onResume();

        if (camera == null)
            camera = getCameraInstance();

        if (camera == null) {
            //android device has no back-facing camera
        	return;
        }
        if (preview == null) {
        	//not after reconstruction (onCreate())

			previewing = true;
        	preview = new CameraPreview(this, camera, previewCb, autoFocusCB);
        	FrameLayout flPreview = (FrameLayout)findViewById(R.id.cameraPreview);
        	flPreview.addView(preview);
        	flPreview.addView(ivOverlay);
    		ivOverlay.setVisibility(View.VISIBLE);
    		
    		if (hasFocus == false) { //for Android 2.3 resume
        		//preview.stopPreview();
            	//preview keeps previwing internally
        		previewing = false;
            	ivOverlay.setVisibility(View.GONE);
        	}
       }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.readermenu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	int id = item.getItemId();
    	boolean ret;
        switch (id) {
        case android.R.id.home:
            //Do stuff
        	startActivity(new Intent(ReaderActivity.this.getApplicationContext(), IsbnActivity.class));
        	finish();
        	ret = true;
        	break;
		case R.id.action_info:
            //Do stuff
            //return true;			
			showAlertDialog(R.string.appinfo_title, R.string.appinfo_message);
			ret = true;
			break;
		default:
			ret = super.onOptionsItemSelected(item);
			break;  
        }
        return ret;
    }
    
    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); //get the first back-facing camera
            if (c == null) { //no back-facing camera
                //c = Camera.open(0);
            	return null;
            }
        } catch (Exception e){
            Log.d("Checker#getCameraInstance()", e.getMessage());
            return null;
        }
        return c;
    }

    private void releaseCamera() {
        if (camera != null) {
            previewing = false;
            camera.cancelAutoFocus();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                camera.autoFocus(autoFocusCB);
        }
    };

    PreviewCallback previewCb = new PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (previewing == false) {
                preview.stopPreview();
            } else {
                Camera.Parameters parameters = camera.getParameters();
                Size size = parameters.getPreviewSize();

                Image barcode = new Image(size.width, size.height, "Y800");
                barcode.setData(data);

                int result = scanner.scanImage(barcode);

                if (result != 0) {
                	boolean isbnScanned = false;
                	
                	SymbolSet syms = scanner.getResults();
                    for (Symbol sym : syms) {
                        if (sym.getType() == Symbol.ISBN13) {
                            strISBNCode = sym.getData().toString();
                            isbnScanned = true;
                        }
                    }

                    if (isbnScanned) {

                    	previewing = false;
                    	preview.stopPreview();
                    	autoFocusHandler.removeCallbacks(doAutoFocus);
                    	ivOverlay.setVisibility(View.GONE);

                    	showBookInfo(strISBNCode);
                    }
                }
            }
        }
    };

    // Mimic continuous auto-focusing
    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    private void showBookInfo(String strISBN) {
        if (biTask != null && biTask.getStatus() != AsyncTask.Status.FINISHED) {
        	biTask.cancel(true);
        }
    	biTask = new BookInfoTask(this);
        biTask.execute(strISBN);
    }

    private void setOverlayImage() {
        int w = preview.getWidth();
        int h = preview.getHeight();

        bmOverlay = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmOverlay);

        Paint paint = new Paint();
        Path path = new Path();
        PorterDuffXfermode modeSrc = new PorterDuffXfermode(PorterDuff.Mode.SRC);
        PorterDuffXfermode modeSrcOver = new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);

        float wUnit = w / 16;
        float hUnit = h / 24;

        //c.drawColor(0x9f000000);
        c.drawColor(0x99444444);        
        
        //トップ不透明エリア
        paint.setXfermode(modeSrc);
        paint.setColor(0xff444444);

        float h0 = 0;
        float h1 = hUnit * 5;
        ////c.drawRect(0 , h0, wUnit * 16, h1, paint);
        c.drawRect(0 , h0, w, h1, paint);
        
        //透明エリア
        paint.setXfermode(modeSrc);
        paint.setColor(0);

        h0 = hUnit * 9;
        h1 = hUnit * 15;
        ////c.drawRect(0 , h0, wUnit * 16, h1, paint);
        c.drawRect(0 , h0, w, h1, paint);
        
        //ボトム不透明エリア
        paint.setXfermode(modeSrc);
        paint.setColor(0xff444444);

        h0 = hUnit * 19;
        h1 = hUnit * 24;
        ////c.drawRect(0 , h0, wUnit * 16, h1, paint);
        c.drawRect(0 , h0, w, h, paint);
        
        /*
        paint.setXfermode(modeSrcOver);

        paint.setColor(0xffff0000);
        paint.setStrokeWidth(4);
        c.drawLine(wUnit * 4, hUnit * 12 - 2, wUnit * 12, hUnit * 12 - 2, paint);

        float d = hUnit * 0.25f;
        paint.setColor(0xffff7f3f);
        c.drawRect(wUnit * 3, h0 - d * 2, wUnit * 13, h0 - d, paint);
        c.drawRect(wUnit * 3, h1 + d, wUnit * 13, h1 + d * 2, paint);

        paint.setStyle(Paint.Style.FILL);
        path.moveTo(wUnit * 3, h0);
        path.lineTo(wUnit * 3, h0 - d);
        path.lineTo(wUnit * 3 + d, h0 - d);
        path.close();
        c.drawPath(path,paint);

        path.moveTo(wUnit * 13, h0);
        path.lineTo(wUnit * 13, h0 - d);
        path.lineTo(wUnit * 13 - d, h0 - d);
        path.close();
        c.drawPath(path,paint);

        path.moveTo(wUnit * 3, h1);
        path.lineTo(wUnit * 3, h1 + d);
        path.lineTo(wUnit * 3 + d, h1 + d);
        path.close();
        c.drawPath(path,paint);

        path.moveTo(wUnit * 13, h1);
        path.lineTo(wUnit * 13, h1 + d);
        path.lineTo(wUnit * 13 - d, h1 + d);
        path.close();
        c.drawPath(path,paint);
        */        

        String strMain0 = getResources().getString(R.string.msgtxt_guidemain0);
        String strMain1 = getResources().getString(R.string.msgtxt_guidemain1);
        String strSub0 = getResources().getString(R.string.msgtxt_guidesub0);
        String strSub1 = getResources().getString(R.string.msgtxt_guidesub1);

        paint.setColor(0xffffffff);
        paint.setAntiAlias(true);
        paint.setTextAlign(Align.CENTER);
        
        paint.setTextSize(wUnit * 4 / 6);
        c.drawText(strMain0, wUnit * 8, (float)(hUnit * 2.2), paint);
        c.drawText(strMain1, wUnit * 8, (float)(hUnit * 3.2), paint);

        paint.setTextSize(wUnit * 4 / 6);
        c.drawText(strSub0, wUnit * 8, (float)(hUnit * 21.2), paint);
        c.drawText(strSub1, wUnit * 8, (float)(hUnit * 22.2), paint);

        ivOverlay.setImageBitmap(bmOverlay);
    }

    private void showAlertDialog(int titleId, int msgId) {
    	SimpleDialog.getNewInstance(getResources().getString(titleId),
    			getResources().getString(msgId))
    	.show(getSupportFragmentManager(), "dialog");
    }
}
