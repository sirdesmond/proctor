package com.team2.android.proctor.util;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Created by abhijeet on 4/19/16.
 */
public class FlyOutContainer extends LinearLayout {

    private View menu;
    private View content;

    protected static final int menuMargin =150;

    public  enum MenuState{
        CLOSED, OPEN, CLOSING, OPENING
    };

    protected Scroller scroller =  new Scroller(this.getContext(), new SmoothInterpolator());
    protected  Runnable runnable = new AnimationRunnable();
    protected Handler handler = new Handler();

    private  static final int duration = 1000;
    private  static  final int interval = 16;

    protected int currentContentOffset =0;
    protected MenuState menuCurrentState = MenuState.CLOSED;

    public FlyOutContainer(Context context) {
        super(context);
    }

    public FlyOutContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlyOutContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        this.menu = this.getChildAt(0);
        this.content = this.getChildAt(1);

        this.menu.setVisibility(View.GONE);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if(changed){
            this.calculateChildDimentions();
        }

        this.menu.layout(l,t,r-menuMargin,b);
        this.content.layout(l + this.currentContentOffset, t, r + this.currentContentOffset, b);

        //super.onLayout(changed, l, t, r, b);
    }

    public void toggleMenu(){
        switch (this.menuCurrentState){
            case CLOSED:
                /*this.menu.setVisibility(View.VISIBLE);
                this.currentContentOffset = this.getMenuWidth();
                this.content.offsetLeftAndRight(currentContentOffset);
                this.menuCurrentState = MenuState.OPEN;*/
                this.menuCurrentState = MenuState.OPENING;
                this.menu.setVisibility(View.VISIBLE);
                this.scroller.startScroll(0,0,this.getMenuWidth(),0,duration);
                break;
            case OPEN:
                /*this.content.offsetLeftAndRight(-currentContentOffset);
                this.currentContentOffset =0;
                this.menuCurrentState = MenuState.CLOSED;
                this.menu.setVisibility(View.GONE);*/
                this.menuCurrentState = MenuState.CLOSING;
                this.scroller.startScroll(this.currentContentOffset,0, -this.currentContentOffset,0,duration);
                break;
            default:
                return;
        }

        this.handler.postDelayed(this.runnable, interval);
    }

    private int getMenuWidth() {
        return this.menu.getLayoutParams().width;
    }

    private void calculateChildDimentions() {
        this.content.getLayoutParams().height = this.getHeight();
        this.content.getLayoutParams().width = this.getWidth();

        this.menu.getLayoutParams().width = this.getWidth() - menuMargin;
        this.menu.getLayoutParams().height=this.getHeight();
    }

    protected class SmoothInterpolator implements Interpolator {

        @Override
        public float getInterpolation(float input) {
            return (float) Math.pow(input-1,5)+1;
        }
    }

    private class AnimationRunnable implements Runnable {
        @Override
        public void run() {
            boolean isAnimationGoingOn = FlyOutContainer.this.scroller.computeScrollOffset();
            FlyOutContainer.this.adjustContentPosition(isAnimationGoingOn);
        }
    }

    private void adjustContentPosition(boolean isAnimationGoingOn) {

        int scorllerOffset = this.scroller.getCurrX();
        this.content.offsetLeftAndRight(scorllerOffset - this.currentContentOffset);
        this.currentContentOffset = scorllerOffset;
        this.invalidate();

        if(isAnimationGoingOn)
        {
            this.handler.postDelayed(this.runnable, interval);
        }
        else
        {
            this.onMenuTransitionComplete();
        }
    }

    private void onMenuTransitionComplete() {
        switch (this.menuCurrentState)
        {
            case OPENING:
                this.menuCurrentState = MenuState.OPEN;
                break;
            case CLOSING:
                this.menuCurrentState = MenuState.CLOSED;
                //this.menu.setVisibility(View.GONE);
                break;
            default:
                return;
        }
    }

}
