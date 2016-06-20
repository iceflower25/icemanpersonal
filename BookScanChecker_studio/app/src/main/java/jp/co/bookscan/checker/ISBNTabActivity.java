package jp.co.bookscan.checker;

import android.app.ActionBar;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

public class ISBNTabActivity extends TabActivity {

    public TabHost tabHost;
    public TabHost.TabSpec tabSpec_13;
    public TabHost.TabSpec tabSpec_10;

    Intent isbnIntent_13;
    Intent isbnIntent_10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isbntab);

        tabHost = getTabHost();
        isbnIntent_13 = new Intent().setClass(this, IsbnActivity.class);
        isbnIntent_10 = new Intent().setClass(this, IsbnActivity10.class);


        if(android.os.Build.VERSION.SDK_INT >= 14) {
            getActionBar().setHomeButtonEnabled(true);
        }

        if(android.os.Build.VERSION.SDK_INT >= 11) {
            createCutomActionBarTitle();
        }


        tabSpec_13 = tabHost.newTabSpec(getResources().getString(R.string.tab_isbntab13))
                .setIndicator(getResources().getString(R.string.tab_isbntab13))
                .setContent(isbnIntent_13);
        tabHost.addTab(tabSpec_13);

        tabSpec_10 = tabHost.newTabSpec(getResources().getString(R.string.tab_isbntab10))
                .setIndicator(getResources().getString(R.string.tab_isbntab10))
                .setContent(isbnIntent_10);
        tabHost.addTab(tabSpec_10);

    }
    private void createCutomActionBarTitle() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.isbn_actionbar, null);
        actionBar.setDisplayShowHomeEnabled(false);

        ImageView home = (ImageView)v.findViewById(R.id.isbn_action_bar_home);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ISBNTabActivity.this.getApplicationContext(), ReaderActivity.class));
                overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
                ////overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
        //assign the view to the actionbar
        actionBar.setCustomView(v);
    }

}
