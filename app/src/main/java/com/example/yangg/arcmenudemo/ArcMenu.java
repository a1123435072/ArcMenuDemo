package com.example.yangg.arcmenudemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;

/**
 * Created by yangg on 2017/7/2.
 * 因为是弧形排列的,所以使用viewgroup
 */

public class ArcMenu extends ViewGroup  implements View.OnClickListener{

    private View child0;
    private int childLeft;
    private int childTOP;
    private int childRight;
    private int childbottom;

    public ArcMenu(Context context) {
        this(context, null);
    }

    public ArcMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

        //status = new  CurrentStatus();
    }

    /**初始化
     *
     * */

    private void init() {


    }

    //测量子空间的狂傲
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //遍历所有控件,
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            //0,0空间有多宽多高,就显示有多宽多高
            getChildAt(i).measure(0,0);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
    在viewGroup默认是不册来那个孩子的
     从第一个子视图,其余子视图,进行弧形排列
     */
    private float radius= 300f;
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        //用代码排列
        child0 = getChildAt(0);
        //这个不能写getHeight() 因为这个得到的是onlayout方法之后得到的
        //getMeasuredWidth拿测量的宽高
        child0.layout(0,0,child0.getMeasuredWidth(),child0.getMeasuredHeight());

        child0.setOnClickListener(this);


        int childCount = getChildCount();
        for (int i = 0; i < childCount-1; i++) {
            View child = getChildAt(i + 1);
            child.setVisibility(VISIBLE);
            float a  = (float) (Math.PI/2/4*i);


            int measuredWidth = child.getMeasuredWidth();
            int measuredHeight = child.getMeasuredHeight();

            childLeft = (int) (radius*Math.sin(a));
            childTOP = (int) (radius*Math.cos(a));
            childRight = childLeft +measuredWidth;
            childbottom = childTOP +measuredHeight;
            child.layout(childLeft, childTOP, childRight, childbottom);
            child.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl:
                rotateChild0();
                rorateOtherChildren();
                break;
        }
    }

    private void rorateOtherChildren(){
        for (int i = 0; i < getChildCount()-1; i++) {

            final View child = getChildAt(i+1);
            int left = child.getLeft();
            int top = child.getTop();


            AnimationSet as = new AnimationSet(true);

            /**
             * 绝对移动的类型包含绝对,相对于自身,相对于父亲
             * 1,3x方向移动的类型
             * 2,4 x发那个想从2到4
             * 5,7y方向一定的类型
             *参数6,8 y方向 从6到8
             */

            TranslateAnimation ta=null;
            if (status ==CurrentStatus.OPEN){
                ta =new TranslateAnimation(
                        TranslateAnimation.ABSOLUTE
                        ,-left
                        ,TranslateAnimation.ABSOLUTE
                        ,-top
                        ,TranslateAnimation.ABSOLUTE
                        ,-childLeft
                        ,TranslateAnimation.ABSOLUTE
                        ,0);
            }else {
                ta =new TranslateAnimation(
                        TranslateAnimation.ABSOLUTE
                        ,-left
                        ,TranslateAnimation.ABSOLUTE
                        ,-top
                        ,TranslateAnimation.ABSOLUTE
                        ,-childLeft
                        ,TranslateAnimation.ABSOLUTE
                        ,0);
            }

            ta.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                //当动画结束后的处理
                @Override
                public void onAnimationEnd(Animation animation) {
                    //改变状态

                    if (status  == CurrentStatus.CLOSE){

                        //先清除掉动画效果
                        child.clearAnimation();
                        //在隐藏动画效果
                        child.setVisibility(INVISIBLE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });


            RotateAnimation ra = new RotateAnimation(0, 720, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            ra.setDuration(2000);
            //平移动画是一个一个先后执行的
            ra.setStartOffset(i*300);

            ra.setFillAfter(true);//动画执行完成之后停在最后一帧

            //动画的茶之气,可以个i阿扁动画的执行效果,,平移超出重点一段距离之后,在会到终点
            ra.setInterpolator(new OvershootInterpolator());//只针对平移动画

            as.addAnimation(ra);
            as.addAnimation(ta);


            child.startAnimation(as);
        }

        changeCurrentStatus();

    }

    private void changeCurrentStatus() {

        status = status == CurrentStatus.OPEN?CurrentStatus.OPEN:CurrentStatus.CLOSE;
    }


    private void rotateChild0() {
        RotateAnimation ra = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(2000);
        ra.setFillAfter(true);
        child0.startAnimation(ra);
    }
    //定义打开和关闭的状态
    private CurrentStatus status =CurrentStatus.CLOSE;
    public enum CurrentStatus{
        OPEN,CLOSE;
    }
    /**
     * 自动hi图的动画效果处理
     * 1,子视图的旋转:我们用补间动画
     * 2,其余子视图 的自身旋转
     * 3,子视图的平移效果
     *
     */
}
