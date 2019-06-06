package com.example.bookyue.activity.BookDetail;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bookyue.R;
import com.example.bookyue.activity.read.ReadActivity;
import com.example.bookyue.model.BookDetail;
import com.example.bookyue.util.DateUtil;

public class BookDetailActivity extends AppCompatActivity implements IBookDetailView, View.OnClickListener {

    public static final String BOOK_ID = "bookId";

    private ImageView mBookCover;
    private TextView mBookTitle;
    private TextView mBookAuthor;
    private TextView mMinorCate;
    private TextView mWordCount;                            //总字数
    private TextView mUpdated;                              //最新更新时间
    private Button mAddOrCancel;                            //添加或者从书架中取消
    private Button mStartRead;                              //开始阅读
    private TextView mLatelyFollower;                       //追书人数
    private TextView mReaderRetentionRares;                 //读者留存率
    private TextView mSerializeWordCount;                   //日更新字数
    private TextView mIntroduction;                         //书籍简介

    private boolean haveThisBook;                           //用来标识该书是否已添加到书架中

    private IBookDetailPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle(R.string.book_detail);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        init();

        mPresenter = new BookDetailPresenter(this);
        mPresenter.initView();
    }

    private void init(){
        mBookCover = findViewById(R.id.book_detail_cover);
        mBookTitle = findViewById(R.id.book_detail_title);
        mBookAuthor = findViewById(R.id.book_detail_author);
        mMinorCate = findViewById(R.id.book_detail_minorCate);
        mWordCount = findViewById(R.id.book_detail_wordCount);
        mUpdated = findViewById(R.id.book_detail_updated);
        mAddOrCancel = findViewById(R.id.book_detail_addOrCancel);
        mStartRead = findViewById(R.id.book_detail_start_read);
        mLatelyFollower = findViewById(R.id.book_detail_latelyFollower);
        mReaderRetentionRares = findViewById(R.id.book_detail_readerRetentionRates);
        mSerializeWordCount = findViewById(R.id.book_detail_serializeWordCount);
        mIntroduction = findViewById(R.id.book_detail_introduction);

        mAddOrCancel.setOnClickListener(this);
        mStartRead.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book_detail,menu);
        return true;
    }

    @Override
    public void initView(BookDetail bookDetail) {
        Glide.with(this).load("https://statics.zhuishushenqi.com"+bookDetail.getCover())
                .into(mBookCover);
        mBookTitle.setText(bookDetail.getTitle());
        mBookAuthor.setText(bookDetail.getAuthor());
        mMinorCate.setText(bookDetail.getMinorCate());
        mWordCount.setText(String.valueOf(bookDetail.getWordCount()));
        mUpdated.setText(DateUtil.getDate(bookDetail.getUpdated()));
        mLatelyFollower.setText(String.valueOf(bookDetail.getLatelyFollower()));
        mSerializeWordCount.setText(String.valueOf(bookDetail.getSerializeWordCount()));
        mIntroduction.setText(bookDetail.getLongIntro());

        //暂时这样写
        haveThisBook = mPresenter.haveThisBook();
        if (haveThisBook){
            setCancelStyle();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.book_detail_addOrCancel:
                if (haveThisBook){      //该书已在书架的话，就从书架中移除，并且清除相关的数据库缓存
                    mPresenter.deleteBookDataFromDatabase();
                    setAddStyle();
                    haveThisBook = false;
                }else{                     //该书不存在，点击时，将书籍相关信息添加到Book表中
                    mPresenter.addBookToDatabase();
                    setCancelStyle();
                    haveThisBook = true;
                }
                break;
            case R.id.book_detail_start_read:
                //开始不将其添加到书架中
                //mPresenter.addBookToDatabase();             //将书籍信息保存到Book表中
                Intent intent = new Intent(this, ReadActivity.class);
                intent.putExtra(ReadActivity.BOOK_ID,mPresenter.getBookId());
                intent.putExtra(ReadActivity.BOOK_TITLE,mPresenter.getBookTitle());
                startActivity(intent);
                break;
            default:
        }
    }

    private void setAddStyle(){
        mAddOrCancel.setBackgroundColor(Color.RED);
        mAddOrCancel.setText(R.string.add_update);
    }

    private void setCancelStyle(){
        mAddOrCancel.setBackgroundColor(Color.GRAY);
        mAddOrCancel.setText(R.string.cancel_update);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.cache_book_menu:
                break;
            case R.id.share_menu:
                break;
            default:
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }
}
