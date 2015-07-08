package com.weige.smart.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class HorizontalScrollViewPager extends ViewPager {

	private int downX;
	private int downY;

	public HorizontalScrollViewPager(Context context) {
		super(context);
	}

	public HorizontalScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			getParent().requestDisallowInterceptTouchEvent(true); // true 不让父控件拦截当前事件
			downX = (int) ev.getX();
			downY = (int) ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			int moveX = (int) ev.getX();
			int moveY = (int) ev.getY();
			
			int diffX = downX - moveX;
			int diffY = downY - moveY;
			
			if(Math.abs(diffX) > Math.abs(diffY)) {
				
				if(getCurrentItem() == 0 && diffX < 0) {
					// 当前ViewPager是第一个图片，并且是从左向右拉动，可以允许父类拦截事件
					getParent().requestDisallowInterceptTouchEvent(false); // false 让父控件拦截当前事件
				} else if((getCurrentItem() == (getAdapter().getCount() - 1)) && diffX > 0) {
					// 当前ViewPager是最后一个图片，并且是从右向左拉动，可以允许父类拦截事件
					getParent().requestDisallowInterceptTouchEvent(false); // false 让父控件拦截当前事件
				} else {
					getParent().requestDisallowInterceptTouchEvent(true); // true 不让父控件拦截当前事件
				}
			} else {
				getParent().requestDisallowInterceptTouchEvent(false); // false 让父控件拦截当前事件
			}
			break;
		case MotionEvent.ACTION_CANCEL: // 手指按住当前控件，移动到别的位置时，这时是取消事件
			getParent().requestDisallowInterceptTouchEvent(true); // true 不让父控件拦截当前事件
			break;
		default:
			break;
		}
		
		return super.dispatchTouchEvent(ev);
	}

}
