package cn.bearever.sedesliplayout.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;

import cn.bearever.sedesliplayout.R;

/**
 * 侧滑布局
 * Created by Bear on 2017/9/28.
 */

public class SideslipLayout extends FrameLayout {
    private static final String TAG = "SideslipLayout";

    private SideslipViewItem mLeftViewItem; //左边需要显示的viewItem
    private SideslipViewItem mTopViewItem; //上面需要显示的viewItem
    private SideslipViewItem mRightViewItem; //右边需要显示的viewItem
    private SideslipViewItem mBottomViewItem; //下面需要显示的viewItem
    private View mHomeView; //主界面
    private int mWidth; //宽度
    private int mHeight; //高度
    private boolean isShowingSide = false; //是否正在显示侧滑菜单
    private boolean isAnimateShowingSide = false; //是否执行显示侧滑菜单的动画 true :显示策划菜单,false ：隐藏侧滑菜单
    private int mAnimateTime = 350; //动画时间 ms

    private int mMoveSide = Gravity.LEFT; //移动的侧边菜单是哪一面
    private boolean canHideSideView = true; //是否可以通过手势滑动关闭侧滑菜单

    public SideslipLayout(@NonNull Context context) {
        this(context, null);
    }

    public SideslipLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SideslipLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attributeSet, int def) {
        TypedArray array = context.obtainStyledAttributes(attributeSet, R.styleable.SideslipLayout);
        if (array != null) {
            array.recycle();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //得到布局的宽高
        this.mWidth = w;
        this.mHeight = h;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //移除所有的布局
        removeAllViewsInLayout();

        //加入主界面
        if (mHomeView != null) {
            addView(mHomeView);
            mHomeView.layout(0, 0, mWidth, mHeight);
        }

        //加入左边的界面
        addItemView(mLeftViewItem, Gravity.LEFT);

        //加入上面边的界面
        addItemView(mTopViewItem, Gravity.TOP);

        //加入右边的界面
        addItemView(mRightViewItem, Gravity.RIGHT);

        //加入下边的界面
        addItemView(mBottomViewItem, Gravity.BOTTOM);

        Log.i(TAG, "onLayout: count:" + getChildCount());
    }

    /**
     * 添加侧滑的view
     *
     * @param viewItem
     * @param position 位置
     */
    private void addItemView(SideslipViewItem viewItem, int position) {
        if (viewItem == null || viewItem.getLayout() == null)
            return;
        //获取layoutParams
        ViewGroup.LayoutParams p = viewItem.getLayout().getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        switch (position) {
            case Gravity.LEFT: {
                int x = (int) (-mWidth * viewItem.getScale());
                //设置宽度
                p.width = (int) (mWidth * viewItem.getScale());
                viewItem.getLayout().setLayoutParams(p);
                //加入布局
                addView(viewItem.getLayout());
                viewItem.getLayout().layout(x, 0, 0, mHeight);
                break;
            }
            case Gravity.TOP: {
                int t = (int) (-mHeight * viewItem.getScale());
                //
                p.height = (int) (mHeight * viewItem.getScale());
                viewItem.getLayout().setLayoutParams(p);
                //
                addView(viewItem.getLayout());
                viewItem.getLayout().layout(0, t, 0, 0);
                break;
            }
            case Gravity.RIGHT: {
                int r = (int) (mWidth * (1 + viewItem.getScale()));
                p.width = (int) (mWidth * viewItem.getScale());
                viewItem.getLayout().setLayoutParams(p);
                //
                addView(viewItem.getLayout());
                viewItem.getLayout().layout(mWidth, 0, r, mHeight);
                break;
            }
            case Gravity.BOTTOM: {
                int b = (int) (mHeight * (1 + viewItem.getScale()));
                p.height = (int) (mHeight * viewItem.getScale());
                viewItem.getLayout().setLayoutParams(p);
                //
                addView(viewItem.getLayout());
                viewItem.getLayout().layout(0, mHeight, mWidth, b);
                break;
            }
        }
    }


    private float mTouchStartX = 0;  //手指按下的x
    private float mTouchStartY = 0; //手指按下的y
    private float mTouchMoveX = 0; //移动的x
    private float mTouchMoveY = 0; //移动的y
    private float mInterceptMoveX = 0; //手指移动的x
    private float mInterceptMoveY = 0; //手指移动的y

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mTouchStartX = ev.getX();
                mTouchStartY = ev.getY();

                mInterceptMoveX = mTouchStartX;
                mInterceptMoveY = mTouchStartY;
                mTouchMoveX = mTouchStartX;
                mTouchMoveY = mTouchStartY;

                touchDownTime = System.currentTimeMillis(); //保存当前时间
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                float x = ev.getX();
                float y = ev.getY();
                mInterceptMoveX = x - mInterceptMoveX;
                mInterceptMoveY = y - mInterceptMoveY;

                /**
                 * 先判断手指按下时是否在边界
                 * 如果在边界，并且手指滑动的距离大于10； 则拦截触摸事件
                 */
                if (computeIsTouchInSide(mTouchStartX, mTouchStartY) && canHideSideView) {
                    if (Math.abs(mInterceptMoveX) > 10 || Math.abs(mInterceptMoveY) > 10) {
                        return true;
                    }
                }


                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                break;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE: {
                float x = event.getX();
                float y = event.getY();
                //变化的值
                float disX = x - mTouchMoveX;
                float disY = y - mTouchMoveY;
                mTouchMoveX = x;
                mTouchMoveY = y;

                Log.d(TAG, "onTouchEvent: x:" + disX);
                //移动view
                touchMoveViews(disX, disY);

                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                float x = event.getX();
                float y = event.getY();

                //移动距离不够则不进行动画
                if (Math.abs(x - mTouchStartX) < 50 && Math.abs(y - mTouchStartY) < 50)
                    break;

                /**
                 * 计算手指抬起的位置，判断是否应该显示或者隐藏侧滑菜单
                 */
                if (computeIsShowSide(event.getX(), event.getY()))
                    animateShowSideView(event.getX(), event.getY(), mMoveSide);
                else
                    animateHideSideView(event.getX(), event.getY(), mMoveSide);

                break;
            }
        }
        return true;
    }


    /**
     * 计算手指按下的位置是否是在侧滑响应区域
     *
     * @param x 按下x
     * @param y 按下y
     * @return
     */
    private boolean computeIsTouchInSide(float x, float y) {
        if (x < mWidth / 4 || x > mWidth / 4f * 3 || y < mHeight / 4 || y > mHeight / 4f * 3)
            return true;
        return false;
    }

    /**
     * 手指滑动的响应
     *
     * @param moveX
     * @param moveY
     */
    private void touchMoveViews(float moveX, float moveY) {
        if (mTouchStartX < mWidth / 4) {
            if (Math.abs(moveX) > Math.abs(moveY)) {
                if (mMoveSide != Gravity.LEFT && !isShowingSide) {
                    mMoveSide = Gravity.LEFT;
                    moveLeftView(moveX);

                }
            }
        } else if (mTouchStartX > mWidth / 4.0f * 3) {
            if (Math.abs(moveX) > Math.abs(moveY)) {
                if (mMoveSide != Gravity.RIGHT && !isShowingSide) {
                    mMoveSide = Gravity.RIGHT;
                    moveRightView(moveX);
                }
            }
        } else if (mTouchStartY < mHeight / 4) {
            if (Math.abs(moveY) > Math.abs(moveX)) {
                if (mMoveSide != Gravity.TOP && !isShowingSide) {
                    mMoveSide = Gravity.TOP;
                    moveTopView(moveY);

                }
            }
        } else if (mTouchStartY > mHeight / 4f * 3) {
            if (Math.abs(moveY) > Math.abs(moveX)) {
                if (mMoveSide != Gravity.BOTTOM && !isShowingSide) {
                    mMoveSide = Gravity.BOTTOM;
                    moveBottomView(moveY);

                }
            }
        }
    }

    /**
     * 左边页面侧滑
     *
     * @param mx
     */
    private void moveLeftView(float mx) {
        if (mLeftViewItem == null)
            return;

        if (mLeftViewItem.getLayout().getX() + mx <= 0)
            mLeftViewItem.getLayout().setX(mLeftViewItem.getLayout().getX() + mx);
    }

    /**
     * 顶部页面侧滑
     *
     * @param my
     */
    private void moveTopView(float my) {
        if (mTopViewItem == null)
            return;

        if (mTopViewItem.getLayout().getY() + my <= 0)
            mTopViewItem.getLayout().setY(mTopViewItem.getLayout().getY() + my);
    }

    /**
     * 右边页面侧滑
     *
     * @param mx
     */
    private void moveRightView(float mx) {
        if (mRightViewItem == null)
            return;
        if (mRightViewItem.getLayout().getX() + mx >= (1 - mRightViewItem.getScale()) * mWidth)
            mRightViewItem.getLayout().setX(mRightViewItem.getLayout().getX() + mx);
    }

    /**
     * 底部页面侧滑
     *
     * @param my
     */
    private void moveBottomView(float my) {
        if (mBottomViewItem == null)
            return;

        if (mBottomViewItem.getLayout().getY() + my >= (1 - mBottomViewItem.getScale()) * mHeight)
            mBottomViewItem.getLayout().setY(mBottomViewItem.getLayout().getY() + my);
    }

    private long touchDownTime; //手指按下时的时间

    /**
     * 计算是需要显示侧滑菜单还是隐藏
     *
     * @return true:显示侧滑菜单 false ：隐藏侧滑菜单
     */
    private boolean computeIsShowSide(float x, float y) {
        //如果手指放下的时候没有在规定区域，则不进行动画
        if (!isShowingSide && !computeIsTouchInSide(mTouchStartX, mTouchStartY))
            return false;

        long time = System.currentTimeMillis() - touchDownTime;
        float speed = 0;
        switch (mMoveSide) {
            case Gravity.LEFT: {
                speed = (x - mTouchStartX) / time;
                if (speed > 1)
                    return true;
                break;
            }
            case Gravity.RIGHT: {
                speed = (x - mTouchStartX) / time;
                if (speed < 1)
                    return true;
                break;
            }
            case Gravity.TOP: {
                speed = (y - mTouchStartY) / time;
                if (speed > 1)
                    return true;
                break;
            }
            case Gravity.BOTTOM: {
                speed = (y - mTouchStartY) / time;
                if (speed < 1)
                    return true;
                break;
            }
        }

        return false;
    }


    /**
     * 执行view动画
     * 在手指抬起的时候执行
     *
     * @param x
     * @param y
     * @param gravity //执行动画的侧边
     */
    private void animateShowSideView(float x, float y, int gravity) {
        float startVal = 0;
        float endVal = 0;
        switch (gravity) {
            case Gravity.LEFT: {
                if (mLeftViewItem == null)
                    break;
                startVal = mLeftViewItem.getLayout().getX();
                endVal = 0;
                break;
            }
            case Gravity.TOP: {
                if (mTopViewItem == null)
                    break;
                startVal = mTopViewItem.getLayout().getY();
                endVal = 0;
                break;
            }
            case Gravity.RIGHT: {
                if (mTopViewItem == null)
                    break;
                startVal = mRightViewItem.getLayout().getX();
                endVal = (1 - mRightViewItem.getScale()) * mWidth;
                break;
            }
            case Gravity.BOTTOM: {
                if (mBottomViewItem == null)
                    break;
                startVal = mBottomViewItem.getLayout().getY();
                endVal = (1 - mBottomViewItem.getScale()) * mHeight;
                break;
            }
        }

        //清除之前的动画
        clearAnimation();

        /**
         * 主界面滑动
         */
        BounceInterpolator bounceInterpolator = new BounceInterpolator();  //弹簧效果

        final ObjectAnimator animate = ObjectAnimator.ofFloat(this, "sideslip", startVal, endVal);
        animate.setInterpolator(bounceInterpolator);
        animate.setDuration(mAnimateTime);
        animate.start();
        animate.addUpdateListener(updateListener);
        animate.addListener(animationListener);
        //正在显示侧滑菜单
        isShowingSide = true;
    }

    /**
     * 执行动画隐藏侧滑菜单
     *
     * @param x
     * @param y
     * @param gravity
     */
    private void animateHideSideView(float x, float y, int gravity) {
        float startVal = 0;
        float endVal = 0;
        switch (gravity) {
            case Gravity.LEFT: {
                if (mLeftViewItem == null)
                    break;
                startVal = mLeftViewItem.getLayout().getX();
                endVal = -mLeftViewItem.getScale() * mWidth;
                break;
            }
            case Gravity.TOP: {
                if (mTopViewItem == null)
                    break;
                startVal = mTopViewItem.getLayout().getY();
                endVal = -mTopViewItem.getScale() * mHeight;
                break;
            }
            case Gravity.RIGHT: {
                if (mTopViewItem == null)
                    break;
                startVal = mRightViewItem.getLayout().getX();
                endVal = mWidth;
                break;
            }
            case Gravity.BOTTOM: {
                if (mBottomViewItem == null)
                    break;
                startVal = mBottomViewItem.getLayout().getY();
                endVal = mHeight;
                break;
            }
        }

        //清除之前的动画
        clearAnimation();

        /**
         * 主界面滑动
         */
        BounceInterpolator interpolator = new BounceInterpolator();  //弹簧效果

        final ObjectAnimator animate = ObjectAnimator.ofFloat(this, "sideslip", startVal, endVal);
        animate.setInterpolator(interpolator);
        animate.setDuration(mAnimateTime);
        animate.start();
        animate.addUpdateListener(updateListener);
        animate.addListener(animationListener);

        //隐藏侧滑菜单
        isShowingSide = false;
    }

    //动画更新监听
    private ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float cVal = (float) animation.getAnimatedValue();
            Log.i(TAG, "onAnimationUpdate: " + cVal);

            switch (mMoveSide) {
                case Gravity.LEFT: {
                    if (mLeftViewItem != null)
                        mLeftViewItem.getLayout().setX(cVal);
                    break;
                }
                case Gravity.TOP: {
                    if (mTopViewItem != null)
                        mTopViewItem.getLayout().setY(cVal);
                    break;
                }
                case Gravity.RIGHT: {
                    if (mRightViewItem != null)
                        mRightViewItem.getLayout().setX(cVal);
                    break;
                }
                case Gravity.BOTTOM: {
                    if (mBottomViewItem != null)
                        mBottomViewItem.getLayout().setY(cVal);
                    break;
                }
            }
        }
    };

    private Animator.AnimatorListener animationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (mListener != null) {
                if (isShowingSide) {
                    mListener.onShow(mMoveSide);
                } else
                    mListener.onHide(mMoveSide);
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    public View getHomeView() {
        return mHomeView;
    }

    public void setHomeView(View mHomeView) {
        this.mHomeView = mHomeView;
    }

    public SideslipViewItem getLeftViewItem() {
        return mLeftViewItem;
    }

    public void setLeftViewItem(SideslipViewItem mLeftViewItem) {
        this.mLeftViewItem = mLeftViewItem;
    }

    public SideslipViewItem getTopViewItem() {
        return mTopViewItem;
    }

    public void setTopViewItem(SideslipViewItem mTopViewItem) {
        this.mTopViewItem = mTopViewItem;
    }

    public SideslipViewItem getRightViewItem() {
        return mRightViewItem;
    }

    public void setRightViewItem(SideslipViewItem mRightViewItem) {
        this.mRightViewItem = mRightViewItem;
    }

    public SideslipViewItem getBottomViewItem() {
        return mBottomViewItem;
    }

    public void setBottomViewItem(SideslipViewItem mBottomViewItem) {
        this.mBottomViewItem = mBottomViewItem;

    }

    public boolean isCanHideSideView() {
        return canHideSideView;
    }

    public void setCanHideSideView(boolean canHideSideView) {
        this.canHideSideView = canHideSideView;
    }

    private OnSideslipListener mListener;

    public void setOnSideslipListener(OnSideslipListener mListener) {
        this.mListener = mListener;
    }

    /**
     * 侧滑状态回掉函数
     */
    public interface OnSideslipListener {
        //侧滑菜单显示
        void onShow(int gravity);

        //侧滑菜单隐藏
        void onHide(int gravity);
    }
}
