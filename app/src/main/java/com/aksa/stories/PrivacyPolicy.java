package com.aksa.stories;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class PrivacyPolicy extends AppCompatActivity {
    private AdView mAdView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chapter_detail);
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
        detail.setText(chapterDetail);
    }

    @Override
    public void onBackPressed() {
        MainActivity.setCounter(MainActivity.getCounter() + 1);
        super.onBackPressed();
        finish();
    }
}
