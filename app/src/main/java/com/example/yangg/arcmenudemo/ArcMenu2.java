package com.example.yangg.arcmenudemo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Range;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by yangg on 2017/7/2.
 */

public class ArcMenu2 extends ViewGroup implements View.OnClickListener {


    private View child0;

    public ArcMenu2(Context context) {
        this(context,null);
    }

    public ArcMenu2(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ArcMenu2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //这里我们不许要进行初始化
    private void init() {
    }

    /**
     * 在VeiwGroup 中默认的是不测量孩子视图的
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //测量孩子视图
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).measure(0,0);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private float radius = 400f;
    //除第一个子视图外的其余子视图成弧形排列
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        child0 = getChildAt(0);
        child0.layout(0,0,child0.getMeasuredWidth(),child0.getMeasuredHeight());
        child0.setOnClickListener(this);
        int childCount = getChildCount();
        //显示剩下的四个图标
        for (int i = 0; i <childCount-1; i++) {
            View child = getChildAt(i + 1);
            int childwidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredWidth();
            float a = (float) (Math.PI/2/4*i);
            int childLeft = (int) (radius* Math.sin(a));
            int childTop  = (int) (radius* Math.cos(a));
            int childRight  =  childLeft  + childwidth;
            int childBttom = childTop + childHeight;


            child.layout(childLeft,childTop ,childRight,childBttom);
            child.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl:
                rotateChild0();
                animateOherChildren();
                break;
        }
    }

    /**
     * 自试图的动画效果处理
     * 1,第0个子视图 的旋转
     * 2,其余子视图的自身旋转
     * 3,其余字是自试图的平移
     *
     */
    private void animateOherChildren() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount-1; i++) {
            final View child = getChildAt(i + 1);
            child.setVisibility(View.VISIBLE);
            int childLeft = child.getLeft();
            int childTop = child.getTop();
            AnimationSet as = new AnimationSet(true);
            //移动的类型包含,ABSOULTE,SELF
            //参数:1,3:x方向的移动的类型
            //参数:2,4 x方向从2,4
            //参数5,7 y方向的移动的类型
            //参数6,8:y方向从6,8
            //这点移动的代码不是太懂
            TranslateAnimation ta = null;
            if (status == CurrentStatus.CLOSE){
                ta = new TranslateAnimation(
                        TranslateAnimation.ABSOLUTE,-childLeft
                        ,TranslateAnimation.ABSOLUTE,0
                        ,TranslateAnimation.ABSOLUTE,-childTop
                        ,TranslateAnimation.ABSOLUTE,0
                );
            }else {
                ta = new TranslateAnimation(
                        TranslateAnimation.ABSOLUTE,0
                        ,TranslateAnimation.ABSOLUTE,-childLeft
                        ,TranslateAnimation.ABSOLUTE,0
                        ,TranslateAnimation.ABSOLUTE,-childTop
                );
            }
            ta.setDuration(1500);
            //当平移完成后,再次切换状态
            ta.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }
                //当动画结束后的处理

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (status ==CurrentStatus.CLOSE){
                        child.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            //平移动画是一个一个显瘦执行的
            ta.setStartOffset(i*300);
            ta.setFillAfter(true);
            //平移动画差值器,可以改变动画的执行效果\
            ta.setInterpolator(new OvershootInterpolator());

            RotateAnimation ra = new RotateAnimation(0, 720, RotateAnimation.RELATIVE_TO_SELF, 0.5f
                    , RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            ra.setDuration(3000);
            ra.setFillAfter(true);
            as.addAnimation(ra);
            as.addAnimation(ta);

            child.startAnimation(as);
        }
        changeCurrentStatus();
    }

    private void rotateChild0() {
        RotateAnimation ra = new RotateAnimation(
                0, 720,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF,0.5f);
        ra.setDuration(2000);
        ra.setFillAfter(true);
        child0.startAnimation(ra);

    }
    private CurrentStatus status  = CurrentStatus.CLOSE;

    private void changeCurrentStatus(){
        status = status == CurrentStatus.CLOSE?CurrentStatus.OPEN:CurrentStatus.CLOSE;
    }

    private enum  CurrentStatus{
        CLOSE,OPEN;
    }
}
