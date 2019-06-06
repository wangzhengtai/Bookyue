package com.example.bookyue.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.bookyue.R;
import com.example.bookyue.ApplicationActivity;
import com.example.bookyue.fragment.bookshelf.BookshelfFragment;
import com.example.bookyue.fragment.community.CommunityFragment;
import com.example.bookyue.fragment.discovery.DiscoveryFragment;
import com.example.bookyue.fragment.mime.MineFragment;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.content.ContextCompat.getSystemService;


public class MainActivity extends ApplicationActivity {

    private static final String TAG = "MainActivity";

    private List<Fragment> mFragments = new ArrayList<>(4);
    private int[] titles = {R.string.bookshelf,R.string.community,R.string.discovery,R.string.mine};
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.view_pager);
        mActionBar = getSupportActionBar();
        //初始化actionBar title
        if (mActionBar != null) {
            mActionBar.setTitle(R.string.bookshelf);
        }

        init();

        FragmentManager fragmentManager = getSupportFragmentManager();
        //FragmentPagerAdapter并不会销毁fragment的实例
        viewPager.setAdapter(new FragmentPagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int i) {
                return mFragments.get(i);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                if (mActionBar != null) {
                    mActionBar.setTitle(titles[position]);
                }
            }
        });
    }

    private void init(){
        mFragments.add(new BookshelfFragment());
        mFragments.add(new CommunityFragment());
        mFragments.add(new DiscoveryFragment());
        mFragments.add(new MineFragment());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.layout_switch:
                //布局切换
                ((BookshelfFragment) mFragments.get(0)).switchLayout();
                break;
            case R.id.night_mode:
                //夜间模式
                break;
            default:
        }
        return true;
    }

    // 让菜单同时显示图标和文字
//    @Override
//    public boolean onMenuOpened(int featureId, Menu menu) {
//        if (menu != null) {
//            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
//                try {
//                    @SuppressLint("PrivateApi")
//                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible",Boolean.TYPE);
//                    method.setAccessible(true);
//                    method.invoke(menu, true);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return super.onMenuOpened(featureId, menu);
//    }

}
