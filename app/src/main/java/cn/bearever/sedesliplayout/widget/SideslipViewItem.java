package cn.bearever.sedesliplayout.widget;

import android.view.View;

/**
 * 侧滑菜单的itemview
 * Created by Bear on 2017/9/28.
 */

public class SideslipViewItem {
    private View layout; //View视图
    private float scale = 0.6f; //显示比例,宽度占控件宽度的比例

    public SideslipViewItem(View layout, float scale) {
        this.layout = layout;
        this.scale = scale;
    }

    public View getLayout() {
        return layout;
    }

    public void setLayout(View layout) {
        this.layout = layout;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

}
