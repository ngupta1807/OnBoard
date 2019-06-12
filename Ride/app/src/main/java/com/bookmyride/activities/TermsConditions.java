package com.bookmyride.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.bookmyride.R;

/**
 * Created by vinod on 1/17/2017.
 */
public class TermsConditions extends AppCompatActivity {
    private TextView done;
    private WebView mWebview;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        init();
    }

    private void init() {
        mWebview = (WebView) findViewById(R.id.text_term);
        mWebview.loadUrl("file:///android_asset/privacypolicy.html");
        done = (TextView) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TermsConditions.this.finish();
            }
        });
    }

    public void onBack(View view){
        onBackPressed();
    }
}
