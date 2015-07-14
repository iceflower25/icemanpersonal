package jp.co.bookscan.checker;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class IsbnActivity extends FragmentActivity {
	private EditText isbnET;    /*検索入力*/
	private IsbnActivity activity;
	private static BookInfoTask biTask = null;
	private final String ISBN_HEADER = "978";  /*ISBN番号の頭の3桁*/
	private final int ISBN_HEADER_NUM = 3;     /*ISBN番号の頭の桁数*/
	private final int ISBN_MAX_NUM = 10;       /*ISBN番号は頭数字以外、最大入力できる桁数。*/	
	private int isbnNum= 0;    /*入力されたISBN桁数(頭以外)*/
	private final ForegroundColorSpan isbnHeaderColor = new ForegroundColorSpan(Color.GRAY);   /*ISBN番号の頭3桁の色*/
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);
		
        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final SpannableStringBuilder isbnStr = new SpannableStringBuilder(ISBN_HEADER);   /*ISBN番号の頭の3桁*/        
        isbnStr.setSpan(isbnHeaderColor, 0, ISBN_HEADER_NUM, Spannable.SPAN_INCLUSIVE_INCLUSIVE);   /*ISBN番号の頭の3桁の色を設定*/
        
        setContentView(R.layout.isbn);
        
        activity = this;
        
        /*ActionBarの左のアイコンがクリックできるため追加*/        
        if(android.os.Build.VERSION.SDK_INT >= 14) {
        	getActionBar().setHomeButtonEnabled(true);        	
        }
        
        if(android.os.Build.VERSION.SDK_INT >= 11) {
        	createCutomActionBarTitle();
        }
        
        isbnET = (EditText)findViewById(R.id.isbn_edittext);
        isbnET.setText(isbnStr);
        isbnET.setSelection(ISBN_HEADER_NUM);
                	
        Button searchButton = (Button)findViewById(R.id.search_button);
        TextView num0TV = (TextView)findViewById(R.id.num0_button);
        TextView num1TV = (TextView)findViewById(R.id.num1_button);
        TextView num2TV = (TextView)findViewById(R.id.num2_button);
        TextView num3TV = (TextView)findViewById(R.id.num3_button);
        TextView num4TV = (TextView)findViewById(R.id.num4_button);
        TextView num5TV = (TextView)findViewById(R.id.num5_button);
        TextView num6TV = (TextView)findViewById(R.id.num6_button);
        TextView num7TV = (TextView)findViewById(R.id.num7_button);
        TextView num8TV = (TextView)findViewById(R.id.num8_button);
        TextView num9TV = (TextView)findViewById(R.id.num9_button);
        TextView delTV = (TextView)findViewById(R.id.del_button);
        TextView enterTV = (TextView)findViewById(R.id.enter_button);        
        
        isbnET.setOnTouchListener(new OnTouchListener() {            
			@SuppressLint("ClickableViewAccessibility")
			@Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (isbnET.getRight() - isbnET.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width() - isbnET.getPaddingRight()) ) {
                     searchIsbn();
                     return true;
                    }
                }
                return true;
            }
        });
        
        searchButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	searchIsbn();
            	
            	/*デバッグのため(スクリーンのサーズ)	
            	DisplayMetrics dm1 = getResources().getDisplayMetrics();
            	String screenSizeStr = "dpi:" + dm1.densityDpi + ",height:" + dm1.heightPixels + ",width:" + dm1.widthPixels;
    	        	
                AlertDialog alertDialog = new AlertDialog.Builder(
                			IsbnActivity.this).create();
     
    	        // Setting Dialog Title
    	        alertDialog.setTitle("Alert Dialog");
    	 
    	        // Setting Dialog Message
    	        alertDialog.setMessage(screenSizeStr);
    	 
    	        // Setting Icon to Dialog
    	        ////alertDialog.setIcon(R.drawable.tick);
    	 
    	        // Showing Alert Message
    	        alertDialog.show();
    	        */
            }
        });
        
        /** "0"ボタン　 */
        num0TV.setOnClickListener(new OnClickListener() {  
            public void onClick(View v) {
            	inputNum("0");            	            	
            }
        });
        
        /** "1"ボタン　 */
        num1TV.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	inputNum("1");            	
            }
        });
        
        /** "2"ボタン　 */
        num2TV.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	inputNum("2");            	
            }
        });
        
        /** "3"ボタン　 */
        num3TV.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	inputNum("3");            	
            }
        });
        
        /** "4"ボタン　 */
        num4TV.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	inputNum("4");            	
            }
        });
        
        /** "5"ボタン　 */
        num5TV.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	inputNum("5");            	
            }
        });
        
        /** "6"ボタン　 */
        num6TV.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	inputNum("6");            	
            }
        });
        
        /** "7"ボタン　 */
        num7TV.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	inputNum("7");            	
            }
        });
        
        /** "8"ボタン　 */
        num8TV.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	inputNum("8");            	
            }
        });
        
        /** "9"ボタン　 */
        num9TV.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	inputNum("9");            	
            }
        });
        
        delTV.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	final SpannableStringBuilder isbnStr;
            	String curStr;    // 入力された数字
            	int curPos;
            	curStr = isbnET.getText().toString();
            	if( curStr.length() > 3 ) {
            		curPos = isbnET.getSelectionStart();   // cursor位置をゲット
            		curStr = curStr.substring(0, curStr.length()-1);  //最後の桁を削除
            		isbnStr = new SpannableStringBuilder(curStr);   // ISBN番号
                	isbnStr.setSpan(isbnHeaderColor, 0, ISBN_HEADER_NUM, Spannable.SPAN_INCLUSIVE_INCLUSIVE);   // ISBN番号の頭の3桁の色を設定
            		isbnET.setText(isbnStr);
            		isbnET.setSelection(curPos-1);
            	}            	
            }
        });
        
        enterTV.setOnClickListener(new OnClickListener() {            
			public void onClick(View v) {
            	searchIsbn();
            }
        });
	}
	
	/* 数字入力処理  */
	void inputNum(String num) {
    	final SpannableStringBuilder isbnStr;
    	int curPos = isbnET.getSelectionStart();       // cursor位置をゲット          	
    	String curStr = isbnET.getText().toString();   // 入力された数字
    	if ( curPos>=13 ) {
    		return;
    	}
    	isbnStr = new SpannableStringBuilder(curStr+num);   // ISBN番号
    	isbnStr.setSpan(isbnHeaderColor, 0, ISBN_HEADER_NUM, Spannable.SPAN_INCLUSIVE_INCLUSIVE);   // ISBN番号の頭の3桁の色を設定
    	isbnET.setText(isbnStr);
    	isbnET.setSelection(curPos+1);  //cursor位置を設置
    }
	
	private void searchIsbn() {
		if (biTask != null && biTask.getStatus() != AsyncTask.Status.FINISHED) {
        	biTask.cancel(true);
        }
        //old task is going to be garbage
	    biTask = new BookInfoTask(activity);
	    biTask.execute(isbnET.getText().toString());
	}
	
	@SuppressLint({ "NewApi", "InflateParams" })
	private void createCutomActionBarTitle() {
		ActionBar actionBar = getActionBar();  
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.isbn_actionbar, null);
        actionBar.setDisplayShowHomeEnabled(false);        
        
        ImageView home = (ImageView)v.findViewById(R.id.isbn_action_bar_home);
        
        home.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {            	
               	startActivity(new Intent(IsbnActivity.this.getApplicationContext(), ReaderActivity.class));
                overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
               	////overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	        	finish();                
            }
        });
        //assign the view to the actionbar
        actionBar.setCustomView(v);
    }
}
