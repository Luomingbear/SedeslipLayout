package cn.bearever.sedesliplayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import cn.bearever.sedesliplayout.widget.SideslipLayout;
import cn.bearever.sedesliplayout.widget.SideslipViewItem;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private SideslipLayout mSideslipLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {

        mSideslipLayout = (SideslipLayout) findViewById(R.id.sideslip_layout);

        //主界面
        View homeView = LayoutInflater.from(this).inflate(R.layout.home_layout, null, false);
        mSideslipLayout.setHomeView(homeView);

        //左边的侧滑菜单
        View leftView = LayoutInflater.from(this).inflate(R.layout.left_layout, null, false);
        mSideslipLayout.setLeftViewItem(new SideslipViewItem(leftView, 0.7f));

        //底部的界面
        View bottomView = LayoutInflater.from(this).inflate(R.layout.bottom_layout, null, false);
        mSideslipLayout.setBottomViewItem(new SideslipViewItem(bottomView, 1f));

        mSideslipLayout.setOnSideslipListener(new SideslipLayout.OnSideslipListener() {
            @Override
            public void onShow(int gravity) {
                Log.i(TAG, "onShow: " + gravity);
            }

            @Override
            public void onHide(int gravity) {
                Log.i(TAG, "onHide: " + gravity);
            }
        });
    }
}
