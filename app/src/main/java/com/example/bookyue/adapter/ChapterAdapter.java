package com.example.bookyue.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bookyue.R;
import com.example.bookyue.database.bean.Chapter;


import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ViewHolder> {

    private List<Chapter> chapters;
    private Context mContext;
    private int mIndexOfChapters;
    private OnClickListener mOnClickListener;

    public ChapterAdapter(List<Chapter> chapters, Context context) {
        this.chapters = chapters;
        mContext = context;
    }

    public void setIndexOfChapters(int indexOfChapters) {
        mIndexOfChapters = indexOfChapters;
    }

    public void setOnClickListener(OnClickListener onClickListener){
        mOnClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_chapter_item,
                viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.chapter.setText(chapters.get(position).getTitle());
        if (chapters.get(position).getCache() == 1){
            viewHolder.cacheImageView.setVisibility(View.VISIBLE);
        }else{
            viewHolder.cacheImageView.setVisibility(View.GONE);
        }
        if (position == mIndexOfChapters){
            viewHolder.chapter.setTextColor(Color.RED);
        }else{
            viewHolder.chapter.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private RelativeLayout mRelativeLayout;
        private TextView chapter;
        private ImageView cacheImageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mRelativeLayout = itemView.findViewById(R.id.item_chapter);
            chapter = itemView.findViewById(R.id.chapter_item);
            cacheImageView = itemView.findViewById(R.id.chapter_cache);
            mRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickListener != null)
                        mOnClickListener.onClick(getAdapterPosition());
                }
            });
        }
    }
}
