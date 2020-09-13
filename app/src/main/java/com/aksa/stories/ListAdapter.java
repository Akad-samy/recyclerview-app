package com.aksa.stories;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class ListAdapter extends RecyclerView.Adapter<ListAdapter.ExampleViewHolder> {
    private ArrayList<ChapterModel> mChapterList;
    private Context mContext;

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        TextView mChapter;
        CardView parentLayout;

        public ExampleViewHolder(View itemView) {
            super(itemView);
            mChapter = itemView.findViewById(R.id.chapter);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }

    public ListAdapter(Context context, ArrayList<ChapterModel> chapterList) {
        mChapterList = chapterList;
        mContext = context;
    }

    /**public ListAdapter(ArrayList<ChapterModel> chapterList) {
        mChapterList = chapterList;
    }**/

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        final ChapterModel currentItem = mChapterList.get(position);
        holder.mChapter.setText(currentItem.getChapter_nbr());

        holder.parentLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ChapterActivity.class);
                intent.putExtra("chapter_nbr", currentItem.getChapter_nbr());
                intent.putExtra("chapter_detail", currentItem.getChapter_detail());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mChapterList.size();
    }
}
