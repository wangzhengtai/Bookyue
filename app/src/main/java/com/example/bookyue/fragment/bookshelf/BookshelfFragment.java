package com.example.bookyue.fragment.bookshelf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bookyue.R;
import com.example.bookyue.activity.read.ReadActivity;
import com.example.bookyue.adapter.BookshelfGridAdapter;
import com.example.bookyue.adapter.BookshelfLinearAdapter;
import com.example.bookyue.adapter.OnClickListener;
import com.example.bookyue.database.bean.Book;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookshelfFragment extends Fragment implements IbookshelfView, OnClickListener {

    private static final String TAG = "BookshelfFragment";
    private Activity mActivity;
    private BookshelfPresenter mPresenter;
    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter<? extends RecyclerView.ViewHolder> mAdapter;               //超类引用
    private BookshelfGridAdapter mGridAdapter;
    private BookshelfLinearAdapter mLinearAdapter;
    private RecyclerView.LayoutManager mLayoutManager;                   //超类引用
    private RecyclerView.LayoutManager mGridLayoutManager;
    private RecyclerView.LayoutManager mLinearLayoutManager;

    private static final int GRID_MODE = 0;
    private static final int LINEAR_MODE = 1;

    private static final String LAYOUT_MODE = "layout_mode";
    private int layout_mode;

//    public static final String BOOK_ID = "book_id";
//    public static final String BOOK_TITLE = "book_title";
//    public static final String BOOK_INDEX_OF_CHAPTERS = "index_of_chapters";
//    public static final String BOOK_INDEX_OF_PAGES = "index_of_pages";

    public BookshelfFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach: ");
        mPresenter = new BookshelfPresenter(this);
        mActivity = (Activity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bookshelf, container, false);
        mSwipeRefresh = view.findViewById(R.id.swipe_refresh);
        mRecyclerView = view.findViewById(R.id.book_recycler);

        mPresenter.initView();

        mSwipeRefresh.setColorSchemeColors(ContextCompat.getColor(mActivity,R.color.red));
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.refreshView();
            }
        });
        return view;
    }


    public void switchLayout(){
        Log.i(TAG, "switchLayout: ");
        if (layout_mode == GRID_MODE){
            if (mLinearAdapter == null){
                mLinearAdapter = new BookshelfLinearAdapter(mPresenter.getBooks(),getContext());
                mLinearLayoutManager = new LinearLayoutManager(getContext());     //默认竖直方向
                mLinearAdapter.setOnClickListener(this);
            }
            mLayoutManager = mLinearLayoutManager;
            mAdapter = mLinearAdapter;
            layout_mode = LINEAR_MODE;
        }else if(layout_mode == LINEAR_MODE){
            if (mGridAdapter == null){
                mGridAdapter = new BookshelfGridAdapter(mPresenter.getBooks(),getContext());
                mGridLayoutManager = new GridLayoutManager(getContext(),3);
                mGridAdapter.setOnClickListener(this);
            }
            mLayoutManager = mGridLayoutManager;
            mAdapter = mGridAdapter;
            layout_mode = GRID_MODE;
        }
        //保存到配置
        saveLayoutMode(layout_mode);
        //配置RecyclerView
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Nullable
    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Override
    public void initRecyclerView(List<Book> books) {
        Log.i(TAG, "initRecyclerView: "+books.size());
        //第一次从文件中读取
        layout_mode = getLayoutMode();
        //首次实例化
        if (layout_mode == GRID_MODE){
            mGridAdapter = new BookshelfGridAdapter(books,getContext());
            mGridLayoutManager = new GridLayoutManager(getContext(),3);
            mGridAdapter.setOnClickListener(this);
            //赋值
            mLayoutManager = mGridLayoutManager;
            mAdapter = mGridAdapter;
        }else if (layout_mode == LINEAR_MODE){
            mLinearAdapter = new BookshelfLinearAdapter(books,getContext());
            mLinearLayoutManager = new LinearLayoutManager(getContext());     //默认竖直方向
            mLinearAdapter.setOnClickListener(this);
            //赋值
            mLayoutManager = mLinearLayoutManager;
            mAdapter = mLinearAdapter;
        }
        //配置RecyclerView
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void refreshRecyclerView() {
        mAdapter.notifyDataSetChanged();
        mSwipeRefresh.setRefreshing(false);
    }

    @Override
    public void showToast(int string) {
        Toast.makeText(getContext(),string,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
        mPresenter.detachView();         //消除引用
    }

    private void saveLayoutMode(final int layoutMode) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(BookshelfFragment.LAYOUT_MODE,layoutMode);
        editor.apply();
    }

    private int getLayoutMode() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        return sharedPreferences.getInt(BookshelfFragment.LAYOUT_MODE,0);
    }

    //adapter的接口回调
    @Override
    public void onClick(int position) {
        if (position != mPresenter.getBooks().size()){
            //如果书籍是已更新状态，则取消更新状态
            if (mPresenter.getBooks().get(position).getIsUpdate() == 1){
                mPresenter.updateBookState(position,0);
                mAdapter.notifyItemChanged(position);
            }
            Intent intent = new Intent(mActivity, ReadActivity.class);
            //传入下一个activity将要用到的数据
            intent.putExtra(ReadActivity.BOOK_ID,mPresenter.getBooks().get(position).get_id());
            intent.putExtra(ReadActivity.BOOK_TITLE,mPresenter.getBooks().get(position).getTitle());
//            intent.putExtra(BOOK_INDEX_OF_CHAPTERS,mPresenter.getBooks().get(position).getIndexOfChapters());
//            intent.putExtra(BOOK_INDEX_OF_PAGES,mPresenter.getBooks().get(position).getIndexOfPages());
            startActivity(intent);
        }else{
            Toast.makeText(mActivity,String.valueOf(position),Toast.LENGTH_SHORT).show();
        }
    }
}
