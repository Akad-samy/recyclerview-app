package com.aksa.stories;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class ChapterActivity extends AppCompatActivity {
    private AdView mAdView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chapter_detail);
        checkConnection();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getIncomingIntent();

        // ============ Ads ====================

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(String.valueOf(R.string.ad_banner));

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // ================ End Ads =======================

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    private void getIncomingIntent() {
        MainActivity mActivity = new MainActivity();
        mActivity.incrementActions(this);

        if(getIntent().hasExtra("chapter_nbr") && getIntent().hasExtra("chapter_detail")){
            String chapterNbr = getIntent().getStringExtra("chapter_nbr");
            String chapterDetail = getIntent().getStringExtra("chapter_detail");

            setData(chapterNbr, chapterDetail);
        }
    }

    private void setData(String chapterNbr, String chapterDetail) {
        TextView title = findViewById(R.id.chapter);
        title.setText(chapterNbr);

        TextView detail = findViewById(R.id.chapterDetail);
        detail.setMovementMethod(LinkMovementMethod.getInstance());
        detail.setText(Html.fromHtml(chapterDetail));



    }

    @Override
    public void onBackPressed() {
        MainActivity.setCounter(MainActivity.getCounter() + 1);
        super.onBackPressed();
        finish();
    }


    public void checkConnection() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if(activeNetwork == null || !activeNetwork.isConnected() || !activeNetwork.isAvailable()){
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.alert_dialog);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
            Button btTryAgain = dialog.findViewById(R.id.bt_try_again);
            btTryAgain.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    recreate();
                }
            });
            dialog.show();
        }
    }
}
