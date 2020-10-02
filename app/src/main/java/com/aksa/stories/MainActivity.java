package com.aksa.stories;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AudienceNetworkAds;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.ads.mediationtestsuite.MediationTestSuite;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.onesignal.OneSignal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ChapterModel> chapterList;
    private DatabaseHelper mDBHelper;
    public static int counter = 0;
    private AdView mAdView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView title;
    private static InterstitialAd interstitialAd;
    private ConsentForm form;
    public ProgressDialog progressDialog;
    public boolean adViewStarted;
    public boolean adIsPaused;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.wtf("WTF", "Starting...");

        checkConnection();
        getContentInfo();

        // =============== OneSignal =============

        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        //================== End OneSignal ==============





        // ============ Ads ====================

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(String.valueOf(R.string.ad_banner));

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(String.valueOf(R.string.ad_inter));
        final AdRequest request = new AdRequest.Builder().build();
        interstitialAd.loadAd(request);

        interstitialAd.setAdListener(
                new AdListener() {
                    @Override
                    public void onAdLoaded() {
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        interstitialAd.loadAd(request);
                    }

                    @Override
                    public void onAdClosed() {
                        interstitialAd.loadAd(request);
                    }
                });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



        if(mAdView.isShown()){
            adViewStarted = true;
        }





        //MediationTestSuite.launch(MainActivity.this);


        // ================ End Ads =======================

        toolBar();

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mDBHelper = new DatabaseHelper(this);

        File database = this.getDatabasePath(DatabaseHelper.DB_NAME);
        if(!database.exists()){
            mDBHelper.getReadableDatabase();
            if(copyDatabase(this)){
            }else {
                Toast.makeText(this, "DataBase Error", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        chapterList = mDBHelper.getListChapters();

        mAdapter = new ListAdapter(this, chapterList);
        mRecyclerView.setAdapter(mAdapter);


    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdView.resume();
    }


    @Override
    protected void onStop() {
        mAdView.pause();
        super.onStop();
    }

    private boolean copyDatabase(Context context) {
        try {
            InputStream inputStream = context.getAssets().open(DatabaseHelper.DB_NAME);
            String outFileName = DatabaseHelper.DB_LOCATION + DatabaseHelper.DB_NAME;
            OutputStream outputStream = new FileOutputStream(outFileName);
            byte[] buff = new byte[1024];
            int length = 0;
            while((length = inputStream.read(buff)) > 0) {
                outputStream.write(buff, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void toolBar() {
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);
        title=findViewById(R.id.title);
        toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
    }

    @Override
    public void onBackPressed(){
        counter++;
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            new AlertDialog.Builder(this)
                    .setTitle("الخروج ؟")
                    .setMessage("هل تريد فعلا الخروج من التطبيق ؟")
                    .setNegativeButton("لا", null)
                    .setPositiveButton("نعم", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            MainActivity.super.onBackPressed();
                        }
                    }).create().show();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                break;
            case R.id.nav_policy:
                Intent intent = new Intent(this, PrivacyPolicy.class);
                intent.putExtra("chapter_nbr", "Privacy Policy");
                intent.putExtra("chapter_detail", "Privacy Policy for " + getResources().getString(R.string.company_name) + "\n" +
                        "At " + getResources().getString(R.string.app_name) + ", accessible at "+ getResources().getString(R.string.package_name) +", one of our main priorities is the privacy of our visitors. This Privacy Policy document contains types of information that is collected and recorded by " + getResources().getString(R.string.app_name) + " and how we use it.\n" +
                        "\n" +
                        "If you have additional questions or require more information about our Privacy Policy, do not hesitate to contact us through email at " + getResources().getString(R.string.email) + "\n" +
                        "\n" +
                        "This privacy policy applies only to our online activities and is valid for visitors to our website with regards to the information that they shared and/or collect in " + getResources().getString(R.string.app_name) + ". This policy is not applicable to any information collected offline or via channels other than this application.\n" +
                        "\n" +
                        "Consent\n" +
                        "\n" +
                        "By using our application, you hereby consent to our Privacy Policy and agree to its terms.\n" +
                        "\n" +
                        "Information we collect\n" +
                        "\n" +
                        "The personal information that you are asked to provide, and the reasons why you are asked to provide it, will be made clear to you at the point we ask you to provide your personal information.\n" +
                        "\n" +
                        "If you contact us directly, we may receive additional information about you such as your name, email address, phone number, the contents of the message and/or attachments you may send us, and any other information you may choose to provide.\n" +
                        "\n" +
                        "When you register for an Account, we may ask for your contact information, including items such as name, Website, address, email address, and telephone number.\n" +
                        "\n" +
                        "How we use your information\n" +
                        "\n" +
                        "We use the information we collect in various ways, including to:\n" +
                        "\n" +
                        "Provide, operate, and maintain our app\n" +
                        "Improve, personalize, and expand our app\n" +
                        "Understand and analyze how you use our app\n" +
                        "Develop new products, services, features, and functionality\n" +
                        "Communicate with you, either directly or through one of our partners, including for customer service, to provide you with updates and other information relating to the app, and for marketing and promotional purposes\n" +
                        "Send you emails\n" +
                        "Find and prevent fraud\n" +
                        "Log Files\n" +
                        "\n" +
                        getResources().getString(R.string.app_name) + " follows a standard procedure of using log files. These files log visitors when they visit websites. All hosting companies do this and a part of hosting services' analytics. The information collected by log files include internet protocol (IP) addresses, browser type, Internet Service Provider (ISP), date and time stamp, referring/exit pages, and possibly the number of clicks. These are not linked to any information that is personally identifiable. The purpose of the information is for analyzing trends, administering the site, tracking users' movement on the website, and gathering demographic information.\n" +
                        "\n" +
                        "Cookies and Web Beacons\n" +
                        "Like any other application, " + getResources().getString(R.string.app_name) + " uses ‘cookies'. These cookies are used to store information including visitors' preferences, and the pages on the website that the visitor accessed or visited. The information is used to optimize the users' experience by customizing our web page content based on visitors' browser type and/or other information.\n" +
                        "\n" +
                        "DoubleClick DART Cookie\n" +
                        "Google is one of a third-party vendor on our site. It also uses cookies, known as DART cookies, to serve ads to our site visitors based upon their visit to https://play.google.com/store/apps/details?id="+ getResources().getString(R.string.package_name) +" and other sites on the internet. However, visitors may choose to decline the use of DART cookies by visiting the Google ad and content network Privacy Policy at the following URL – https://policies.google.com/technologies/ads.\n" +
                        "\n" +
                        "Some of advertisers on our site may use cookies and web beacons. Our advertising partners are listed below. Each of our advertising partners has their own Privacy Policy for their policies on user data. For easier access, we hyperlinked to their Privacy Policies below.\n" +
                        "\n" +
                        "Google\n" +
                        "\n" +
                        "https://policies.google.com/technologies/ads\n" +
                        "\n" +
                        "Advertising Partners Privacy Policies\n" +
                        "\n" +
                        "You may consult this list to find the Privacy Policy for each of the advertising partners of " + getResources().getString(R.string.app_name) + " .\n" +
                        "\n" +
                        "Third-party ad servers or ad networks uses technologies like cookies, JavaScript, or Web Beacons that are used in their respective advertisements and links that appear on " + getResources().getString(R.string.app_name) + ", which are sent directly to users' browser. They automatically receive your IP address when this occurs. These technologies are used to measure the effectiveness of their advertising campaigns and/or to personalize the advertising content that you see on websites that you visit.\n" +
                        "\n" +
                        "Note that " + getResources().getString(R.string.app_name) + " has no access to or control over these cookies that are used by third-party advertisers.\n" +
                        "\n" +
                        "Third-Party Privacy Policies\n" +
                        "\n" +
                        getResources().getString(R.string.app_name) + "'s Privacy Policy does not apply to other advertisers or websites. Thus, we are advising you to consult the respective Privacy Policies of these third-party ad servers for more detailed information. It may include their practices and instructions about how to opt-out of certain options. You may find a complete list of these Privacy Policies and their links here: Privacy Policy Links.\n" +
                        "\n" +
                        "You can choose to disable cookies through your individual browser options. To know more detailed information about cookie management with specific web browsers, it can be found at the browsers' respective websites. What Are Cookies?\n" +
                        "\n" +
                        "CCPA Privacy Policy (Do Not Sell My Personal Information)\n" +
                        "\n" +
                        "Under the CCPA, among other rights, California consumers have the right to:\n" +
                        "\n" +
                        "Request that a business that collects a consumer's personal data disclose the categories and specific pieces of personal data that a business has collected about consumers.\n" +
                        "\n" +
                        "Request that a business delete any personal data about the consumer that a business has collected.\n" +
                        "\n" +
                        "Request that a business that sells a consumer's personal data, not sell the consumer's personal data.\n" +
                        "\n" +
                        "If you make a request, we have one month to respond to you. If you would like to exercise any of these rights, please contact us.\n" +
                        "\n" +
                        "GDPR Privacy Policy (Data Protection Rights)\n" +
                        "\n" +
                        "We would like to make sure you are fully aware of all of your data protection rights. Every user is entitled to the following:\n" +
                        "\n" +
                        "The right to access – You have the right to request copies of your personal data. We may charge you a small fee for this service.\n" +
                        "\n" +
                        "The right to rectification – You have the right to request that we correct any information you believe is inaccurate. You also have the right to request that we complete the information you believe is incomplete.\n" +
                        "\n" +
                        "The right to erasure – You have the right to request that we erase your personal data, under certain conditions.\n" +
                        "\n" +
                        "The right to restrict processing – You have the right to request that we restrict the processing of your personal data, under certain conditions.\n" +
                        "\n" +
                        "The right to object to processing – You have the right to object to our processing of your personal data, under certain conditions.\n" +
                        "\n" +
                        "The right to data portability – You have the right to request that we transfer the data that we have collected to another organization, or directly to you, under certain conditions.\n" +
                        "\n" +
                        "If you make a request, we have one month to respond to you. If you would like to exercise any of these rights, please contact us.\n" +
                        "\n" +
                        "Children's Information\n" +
                        "\n" +
                        "Another part of our priority is adding protection for children while using the internet. We encourage parents and guardians to observe, participate in, and/or monitor and guide their online activity.\n" +
                        "\n" +
                        getResources().getString(R.string.app_name) + " does not knowingly collect any Personal Identifiable Information from children under the age of 13. If you think that your child provided this kind of information on our application, we strongly encourage you to contact us immediately and we will do our best efforts to promptly remove such information from our records.");
                startActivity(intent);
                break;
            case R.id.nav_apps:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id="+getResources().getString(R.string.developer))));
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        incrementActions(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void incrementActions(Context mcontext) {
        counter++;
        if(counter >= 5) {
            progressDialog = new ProgressDialog(mcontext);
            progressDialog.setContentView(R.layout.loader);
            progressDialog.setTitle(R.string.app_name);
            progressDialog.setMessage("المرجو الانتظار");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            ProgressBar progressbar=(ProgressBar)progressDialog.findViewById(android.R.id.progress);
            progressbar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#6200EE"), android.graphics.PorterDuff.Mode.SRC_IN);

            Runnable progressRunnable = new Runnable() {

                @Override
                public void run() {
                    progressDialog.dismiss();
                    if (interstitialAd.isLoaded()) {
                        interstitialAd.show();
                        counter = 0;
                    } else {
                        Log.wtf("wtf", "interstitial not loaded yet !!!");
                    }
                }
            };

            Handler pdCanceller = new Handler();
            pdCanceller.postDelayed(progressRunnable, 3000);


        }
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

    private void getContentInfo(){
        ConsentInformation consentInformation = ConsentInformation.getInstance(this);
        String[] publisherIds = {getResources().getString(R.string.publisher_ID)};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                // User's consent status successfully updated.
                boolean isEuropeanUser = ConsentInformation.getInstance(MainActivity.this).isRequestLocationInEeaOrUnknown();

                if(isEuropeanUser) {
                    switch (consentStatus) {
                        case PERSONALIZED:
                            personalizeAds(true);
                            break;
                        case NON_PERSONALIZED:
                            personalizeAds(false);
                            break;
                        case UNKNOWN:
                            getConsentForm();
                            break;
                    }
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
            }
        });
    }

    private void personalizeAds(boolean isPersonalized) {
        if(isPersonalized){
            AdRequest adRequest =new AdRequest.Builder().build();
        }else{
            Bundle extras = new Bundle();
            extras.putString("npa", "1");

            AdRequest request = new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                    .build();
        }
    }

    private void getConsentForm(){
        URL privacyUrl = null;
        try {
            // TODO: Replace with your app's privacy policy URL.
            privacyUrl = new URL(getResources().getString(R.string.privacy));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // Handle error.
        }
        form = new ConsentForm.Builder(this, privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        // Consent form loaded successfully.
                        form.show();
                    }

                    @Override
                    public void onConsentFormOpened() {
                        // Consent form was displayed.
                    }

                    @Override
                    public void onConsentFormClosed(
                            ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        // Consent form was closed.
                        if(consentStatus.equals(ConsentStatus.PERSONALIZED)){
                            personalizeAds(true);
                        }else {
                            personalizeAds(false);
                        }
                    }

                    @Override
                    public void onConsentFormError(String errorDescription) {
                        // Consent form error.
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .withAdFreeOption()
                .build();
        form.load();
    }

    public static int getCounter() {
        return counter;
    }

    public static void setCounter(int counter) {
        MainActivity.counter = counter;
    }

    @Override
     protected void onDestroy() {
        Log.wtf("wtf", "this is destroyed");
         mAdView.destroy();
        super.onDestroy();
     }
}