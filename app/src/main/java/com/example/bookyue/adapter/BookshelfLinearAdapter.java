package com.example.bookyue.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bookyue.R;
import com.example.bookyue.database.bean.Book;
import com.example.bookyue.util.DateUtil;
import com.example.bookyue.view.LabelImageView;

import java.util.List;

import static android.support.v7.widget.RecyclerView.*;

public class BookshelfLinearAdapter extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = "BookshelfLinearAdapter";
    private static final int LINEAR_VIEW_TYPE =0;
    private static final int LAST_VIEW_TYPE = 1;

    private List<Book> mBooks;
    private Context mContext;
    private OnClickListener mOnClickListener;

    public BookshelfLinearAdapter(List<Book> books, Context context){
        mBooks = books;
        mContext = context;
    }

    public void setOnClickListener(OnClickListener  onClickListener){
        mOnClickListener = onClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mBooks.size())
            return LAST_VIEW_TYPE;
        return LINEAR_VIEW_TYPE;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        //Log.i(TAG, "onCreateViewHolder: "+viewType);
        if (viewType == LAST_VIEW_TYPE){
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.recycler_book_linear_last,viewGroup,false);
            return new LastLinearViewHolder(view);
        }
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_book_linear,viewGroup,false);
        return new LinearViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        //Log.i(TAG, "onBindViewHolder: "+position);
        if (position == mBooks.size()){           //最后一个直接返回
            return;
        }
        LinearViewHolder linearViewHolder = (LinearViewHolder) viewHolder;
        linearViewHolder.bookTitle.setText(mBooks.get(position).getTitle());
        String str = DateUtil.getDate(mBooks.get(position).getUpdated())+":"+mBooks.get(position).getLastChapter();
        linearViewHolder.lastChapter.setText(str);
        linearViewHolder.bookCover.setUpdate(mBooks.get(position).getIsUpdate());
        Glide.with(mContext).load("https://statics.zhuishushenqi.com"+mBooks.get(position).getCover())
                .into(linearViewHolder.bookCover);
    }

    @Override
    public int getItemCount() {
        return mBooks.size()+1;
    }

    class LinearViewHolder extends ViewHolder{

        private RelativeLayout linear;
        private LabelImageView bookCover;
        private TextView bookTitle;
        private TextView lastChapter;

        LinearViewHolder(@NonNull View itemView) {
            super(itemView);

            linear = itemView.findViewById(R.id.recycler_book_linear);
            bookCover = itemView.findViewById(R.id.linear_book_cover);
            bookTitle = itemView.findViewById(R.id.linear_book_title);
            lastChapter = itemView.findViewById(R.id.linear_book_lastChapter);

            linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickListener != null){
                        mOnClickListener.onClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    class LastLinearViewHolder extends ViewHolder{

        private RelativeLayout linearLast;
        LastLinearViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLast = itemView.findViewById(R.id.recycler_book_linear_last);

            linearLast.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickListener != null)
                        mOnClickListener.onClick(getAdapterPosition());
                }
            });
        }
    }
}
