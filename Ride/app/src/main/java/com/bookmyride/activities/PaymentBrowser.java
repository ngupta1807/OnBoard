package com.bookmyride.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.views.AlertDialog;

/**
 * Created by vinod on 24-02-2017.
 */

public class PaymentBrowser extends AppCompatActivity {
    WebView mWebView;
    private ProgressBar progressBar;
    String bookingID;
    TextView title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_browser);
        init();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private void init() {
        bookingID = getIntent().getStringExtra("bookingID");
        title = (TextView) findViewById(R.id.title);
        if (bookingID.equals(""))
            title.setText("RECHARGE WALLET");
        mWebView = (WebView) findViewById(R.id.webview);
        progressBar = (ProgressBar) findViewById(R.id.webloadProgressBar);
        mWebView.setWebViewClient(new AppWebViewClients(progressBar));
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(getIntent().getStringExtra("endPoint"));
        /*mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void performClick() {
                Log.d("LOGIN::", "Clicked");
                Toast.makeText(PaymentBrowser.this, "Login clicked", Toast.LENGTH_LONG).show();
            }
        }, "login");*/
    }

    int type = 0;

    public class AppWebViewClients extends WebViewClient {
        private ProgressBar progressBar;

        public AppWebViewClients(ProgressBar progressBar) {
            this.progressBar = progressBar;
            progressBar.setVisibility(View.VISIBLE);
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("site/paypal-success")) {
                type = 1;
                view.loadUrl(url);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String msg;
                        if (bookingID.equals(""))
                            msg = "Amount has been added to your wallet.";
                        else
                            msg = "Payment Received. Thanks for using Ride247V2.";
                        Alert("Success!", msg);
                    }
                }, 3000);
            } else if (url.contains("site/paypal-cancel")) {
                //view.loadUrl(url);
                type = 2;
                String msg;
                if (bookingID.equals(""))
                    msg = "There are some error while add money to your wallet. Please try again.";
                else
                    msg = "Payment cancelled. Please try again..";
                Alert("Oops !!!", msg);
            } else
                view.loadUrl(url);
            return true;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            if (url.contains("site/paypal-success")) {
                type = 1;
                view.loadUrl(url);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String msg;
                        if (bookingID.equals(""))
                            msg = "Amount has been added to your wallet.";
                        else
                            msg = "Payment Received. Thanks for using Ride247V2.";
                        Alert("Success!", msg);
                    }
                }, 3000);
            } else if (url.contains("site/paypal-cancel")) {
                //view.loadUrl(url);
                type = 2;
                String msg;
                if (bookingID.equals(""))
                    msg = "There are some error while add money to your wallet. Please try again.";
                else
                    msg = "Payment cancelled.";
                Alert("Oops !!!", msg);
            } else
                view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void Alert(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(PaymentBrowser.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (type == 1) {
                    if (bookingID.equals("")) {
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        PaymentBrowser.this.finish();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), RatingToDriver.class);
                        intent.putExtra("bookingID", getIntent().getStringExtra("bookingID"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        PaymentBrowser.this.finish();
                    }
                } else if (type == 2) {
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    PaymentBrowser.this.finish();
                }
            }
        });
        mDialog.show();
    }
}