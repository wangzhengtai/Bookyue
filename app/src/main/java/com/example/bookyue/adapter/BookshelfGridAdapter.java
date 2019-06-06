package com.example.bookyue.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bookyue.R;
import com.example.bookyue.database.bean.Book;
import com.example.bookyue.view.LabelImageView;

import java.util.List;

public class BookshelfGridAdapter extends RecyclerView.Adapter<BookshelfGridAdapter.GridViewHolder> {

    private static final String TAG = "BookshelfGridAdapter";

    private List<Book> mBooks;
    private Context mContext;
    private OnClickListener mOnClickListener;

    public BookshelfGridAdapter(List<Book> books, Context context){
        mBooks = books;
        mContext = context;
    }

    public void setOnClickListener(OnClickListener listener){
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_book_grid,viewGroup,false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder viewHolder, int i) {
        if (i == mBooks.size()){           //最后一个特殊处理
            viewHolder.bookTitle.setText("");
            viewHolder.bookCover.setUpdate(0);
            viewHolder.bookCover.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.add_book_cover));
            return;
        }
        viewHolder.bookTitle.setText(mBooks.get(i).getTitle());
        viewHolder.bookCover.setUpdate(mBooks.get(i).getIsUpdate());
        //Log.i(TAG, "onBindViewHolder: "+mBooks.get(i).getIsUpdate());
        Glide.with(mContext).load("https://statics.zhuishushenqi.com"+mBooks.get(i).getCover())
                .into(viewHolder.bookCover);
    }

    @Override
    public int getItemCount() {
        return mBooks.size()+1;
    }

    class GridViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout grid;
        private LabelImageView bookCover;
        private TextView bookTitle;

        private GridViewHolder(@NonNull View itemView) {
            super(itemView);
            grid = itemView.findViewById(R.id.recycler_book_grid);
            bookCover = itemView.findViewById(R.id.grid_book_cover);
            bookTitle = itemView.findViewById(R.id.grid_book_title);

            grid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickListener != null)
                        mOnClickListener.onClick(getAdapterPosition());
                }
            });
        }
    }
}
