package com.aksa.stories;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ChapterActivity extends AppCompatActivity {
    private static final String TAG = "ChapterActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chapter_detail);
        Log.d(TAG, "onCreated started");
        getIncomingIntent();
    }

    private void getIncomingIntent() {
        if(getIntent().hasExtra("chapter_nbr") && getIntent().hasExtra("chapter_detail")){
            String chapterNbr = getIntent().getStringExtra("chapter_nbr");
            String chapterDetail = getIntent().getStringExtra("chapter_detail");

            setData(chapterNbr, chapterDetail);
        }
    }

    private void setData(String chapterNbr, String chapterDetail) {
        TextView title = findViewById(R.id.chapter);
        title.setText(chapterNbr);

        TextView detail =findViewById(R.id.chapterDetail);
        detail.setText(chapterDetail);
    }
}
