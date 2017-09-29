# SideslipLayout 侧滑菜单

更加全面的侧滑菜单控件，支持上下左右四个方向的侧滑菜单，还可以自由设定显示的大小比例。

## 效果图
<img src="https://github.com/Luomingbear/SedeslipLayout/blob/master/show.gif"></img>

## 使用方法

- **引用**

  在布局文件里面引用<code>SideslipLayout</code>

- **相关方法**

  <code>get/setHomeView()</code>获取/设置主界面View；
  
  <code>get/setLeftViewItem()</code>获取/设置左边侧滑菜单；
  
  <code>get/setTopViewItem()</code>获取/设置顶部的侧滑菜单；
  
  <code>get/setRightViewItem()</code>获取/设置右边的侧滑菜单；
  
  <code>get/setBottomViewItem()</code>获取/设置底部的侧滑菜单；
  
- **SideslipViewItem类**
``` java
public class SideslipViewItem {
    private View layout; //View视图
    private float scale = 0.6f; //显示比例,宽度占控件宽度的比例
}
```
