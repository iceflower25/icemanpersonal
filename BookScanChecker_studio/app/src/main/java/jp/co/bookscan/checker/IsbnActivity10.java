package jp.co.bookscan.checker;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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

public class IsbnActivity10 extends FragmentActivity {
    private EditText isbnET;
    private IsbnActivity10 activity;
    private static BookInfoTask biTask = null;
    private final int ISBN_HEADER_NUM = 0;     /*ISBN�ԍ��̓��̌���*/
    private final ForegroundColorSpan isbnHeaderColor = new ForegroundColorSpan(Color.GRAY);   /*ISBN�ԍ��̓�3���̐F*/

    private TextView textLabel;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        setContentView(R.layout.activity_isbn10);

        activity = this;
        
        /*ActionBar�̍��̃A�C�R�����N���b�N�ł��邽�ߒǉ�*/
//        if(android.os.Build.VERSION.SDK_INT >= 14) {
//        	getActionBar().setHomeButtonEnabled(true);
//        }
//
//        if(android.os.Build.VERSION.SDK_INT >= 11) {
//        	createCutomActionBarTitle();
//        }

        isbnET = (EditText)findViewById(R.id.isbn_edittext);

        Button searchButton = (Button)findViewById(R.id.search_button);

        textLabel = (TextView) findViewById(R.id.textLabel);

        String str1 = getResources().getString(R.string.isbn_str1_1);
        String str2 = getResources().getString(R.string.isbn_str1_2);
        String str3 = getResources().getString(R.string.isbn_str1_3);


        String str = "<html>" + str1 + "<font color='red'>" + str2 + "</font>"  + str3 + "</html>";
        textLabel.setText(Html.fromHtml(str));

        isbnET.setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (isbnET.getRight() - isbnET.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width() - isbnET.getPaddingRight())) {
                        searchIsbn();
                        return false;
                    }
                }
                return false;
            }
        });

        searchButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                searchIsbn();
            }
        });

    }


    private void searchIsbn() {

        String searchText = isbnET.getText().toString();
        Log.e("cleverman", "searchText: " + searchText) ;

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
                startActivity(new Intent(IsbnActivity10.this.getApplicationContext(), ReaderActivity.class));
                overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
                ////overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
        //assign the view to the actionbar
        actionBar.setCustomView(v);
    }
}
