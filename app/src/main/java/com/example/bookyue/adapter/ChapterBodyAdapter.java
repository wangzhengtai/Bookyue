package com.example.bookyue.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bookyue.R;

public class ChapterBodyAdapter extends RecyclerView.Adapter<ChapterBodyAdapter.ViewHolder> {

    private String mChapterBody;
    private Context mContext;
    private OnClickListener mOnClickListener;            //监听回调接口
    private int mStart = 0;              //字符的开始读取位置

    public ChapterBodyAdapter(String chapterBody, Context context) {
        mChapterBody = chapterBody;
        mContext = context;
    }

    public void setOnClickListener(OnClickListener onClickListener){
        mOnClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_read_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        //viewHolder.mReadView.setText(mChapterBody.substring(mStart));
        //viewHolder.mReadView.resize();
        //mStart += viewHolder.mReadView.getCharNum();
        mStart += 500;
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        //ReadView mReadView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
//            mReadView = itemView.findViewById(R.id.read_text);
//
//            mReadView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mOnClickListener != null)
//                        mOnClickListener.onClick(getAdapterPosition());
//                }
//            });
        }
    }
}
