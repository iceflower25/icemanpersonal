package jp.co.bookscan.checker;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class IsbnActivity extends FragmentActivity {
	private EditText isbnET;
	private IsbnActivity activity;
	private static BookInfoTask biTask = null;
	private InputMethodManager imm;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);
		
        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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
        
        imm = (InputMethodManager)getSystemService(
        	      Context.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(isbnET.getWindowToken(), 0);
        	
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
            @Override 
            
             
            
            public boolean onTouch(View v, MotionEvent event) {
                //final int DRAWABLE_LEFT = 0;
                //final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                //final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                	int rg = isbnET.getRight();
                	int wd = isbnET.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();
                	int df = rg - wd;
                	float x = event.getRawX();
                    if(event.getRawX() >= (isbnET.getRight() - isbnET.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width() - isbnET.getPaddingRight()) ) {
                        // your action here
                     //Log.d("test", "x" + x);
                     searchIsbn();
                     return true;
                    }
                }
                //imm.hideSoftInputFromWindow(isbnET.getWindowToken(), 0);
                return true;
            }
        });
        
        searchButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	searchIsbn();
            }
        });
        
        num0TV.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	int curPos = isbnET.getSelectionStart();
            	isbnET.setText(isbnET.getText() + "0");
            	isbnET.setSelection(curPos+1);
            }
        });        
        
        num1TV.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	int curPos = isbnET.getSelectionStart();
            	isbnET.setText(isbnET.getText() + "1");
            	isbnET.setSelection(curPos+1);
            }
        });
        
        num2TV.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	int curPos = isbnET.getSelectionStart();
            	isbnET.setText(isbnET.getText() + "2");
            	isbnET.setSelection(curPos+1);
            }
        });
        
        num3TV.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	int curPos = isbnET.getSelectionStart();
            	isbnET.setText(isbnET.getText() + "3");
            	isbnET.setSelection(curPos+1);
            }
        });
        
        num4TV.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	int curPos = isbnET.getSelectionStart();
            	isbnET.setText(isbnET.getText() + "4");
            	isbnET.setSelection(curPos+1);
            }
        });
        
        num5TV.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	int curPos = isbnET.getSelectionStart();
            	isbnET.setText(isbnET.getText() + "5");
            	isbnET.setSelection(curPos+1);
            }
        });
        
        num6TV.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	int curPos = isbnET.getSelectionStart();
            	isbnET.setText(isbnET.getText() + "6");
            	isbnET.setSelection(curPos+1);
            }
        });
        
        num7TV.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	int curPos = isbnET.getSelectionStart();
            	isbnET.setText(isbnET.getText() + "7");
            	isbnET.setSelection(curPos+1);
            }
        });
        
        num8TV.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	int curPos = isbnET.getSelectionStart();
            	isbnET.setText(isbnET.getText() + "8");
            	isbnET.setSelection(curPos+1);
            }
        });
        
        num9TV.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	int curPos = isbnET.getSelectionStart();
            	isbnET.setText(isbnET.getText() + "9");
            	isbnET.setSelection(curPos+1);
            }
        });
        
        delTV.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	String isbnStr;
            	int curPos;
            	isbnStr = isbnET.getText().toString();
            	if( isbnStr.length() > 0 ) {
            		curPos = isbnET.getSelectionStart();
            		isbnStr = isbnStr.substring(0, isbnStr.length()-1);            		
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

	/*
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu items for use in the action bar
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.isbnmenu, menu);
	        return super.onCreateOptionsMenu(menu);
	    }
	    
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	int id = item.getItemId();
	    	boolean ret;
	        switch (id) {
	        case android.R.id.home:
	            //Do stuff
	        	startActivity(new Intent(IsbnActivity.this.getApplicationContext(), ReaderActivity.class));
	        	finish();
	        	ret = true;
	        	break;
			case R.id.action_info:
	            //Do stuff
	            //return true;
				//actionopen();
				
				ret = true;
				break;
			default:
				ret = super.onOptionsItemSelected(item);
				break;  
	        }
	        return ret;
	    }
	*/
	
	private void searchIsbn() {
		if (biTask != null && biTask.getStatus() != AsyncTask.Status.FINISHED) {
        	biTask.cancel(true);
        }
        //old task is going to be garbage
	    biTask = new BookInfoTask(activity);
	    biTask.execute(isbnET.getText().toString());
	}
	
	@SuppressLint("NewApi")
	private void createCutomActionBarTitle() {
		ActionBar actionBar = getActionBar();  
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.isbn_actionbar, null);
        actionBar.setDisplayShowHomeEnabled(false);
        
        /*
        Typeface tf = Typeface.createFromAsset(getAssets(),"font/fat_tats.ttf");
        TextView frag1 = (TextView)v.findViewById(R.id.titleFragment1);
        frag1.setTypeface(tf);
        TextView frag2 = (TextView)v.findViewById(R.id.titleFragment2);
        frag2.setTypeface(tf);
        
        frag1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(YourCurrentActivity.this, YourTargetActivity.class));
                finish();
            }
        });
        frag2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(YourCurrentActivity.this, YourTargetActivity.class));
                finish();
            }
        });
        */
        
        ImageView home = (ImageView)v.findViewById(R.id.isbn_action_bar_home);
        
        home.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	//int height = getActionBar().getHeight();
                //Log.d("isbn", "height=" + height);
            	
                //startActivity(new Intent(YourCurrentActivity.this, YourTargetActivity.class));
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
