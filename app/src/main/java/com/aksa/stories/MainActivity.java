package com.aksa.stories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<ChapterModel> chapterList = new ArrayList<>();
        chapterList.add(new ChapterModel(1, "Line 1", "Line 2"));
        chapterList.add(new ChapterModel(2, "Line 3", "Line 4"));
        chapterList.add(new ChapterModel(3, "Line 5", "Line 6"));
        chapterList.add(new ChapterModel(4, "Line 1", "Line 2"));
        chapterList.add(new ChapterModel(5, "Line 3", "Line 4"));
        chapterList.add(new ChapterModel(6, "Line 5", "Line 6"));
        chapterList.add(new ChapterModel(7, "Line 1", "Line 2"));
        chapterList.add(new ChapterModel(8, "Line 3", "Line 4"));
        chapterList.add(new ChapterModel(9, "Line 5", "Line 6"));
        chapterList.add(new ChapterModel(10, "Line 3", "Line 4"));
        chapterList.add(new ChapterModel(11, "Line 5", "Line 6"));

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ListAdapter(this, chapterList);
        mRecyclerView.setAdapter(mAdapter);
    }
}