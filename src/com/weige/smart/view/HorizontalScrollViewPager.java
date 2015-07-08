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
			getParent().requestDisallowInterceptTouchEvent(true); // true ���ø��ؼ����ص�ǰ�¼�
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
					// ��ǰViewPager�ǵ�һ��ͼƬ�������Ǵ����������������������������¼�
					getParent().requestDisallowInterceptTouchEvent(false); // false �ø��ؼ����ص�ǰ�¼�
				} else if((getCurrentItem() == (getAdapter().getCount() - 1)) && diffX > 0) {
					// ��ǰViewPager�����һ��ͼƬ�������Ǵ����������������������������¼�
					getParent().requestDisallowInterceptTouchEvent(false); // false �ø��ؼ����ص�ǰ�¼�
				} else {
					getParent().requestDisallowInterceptTouchEvent(true); // true ���ø��ؼ����ص�ǰ�¼�
				}
			} else {
				getParent().requestDisallowInterceptTouchEvent(false); // false �ø��ؼ����ص�ǰ�¼�
			}
			break;
		case MotionEvent.ACTION_CANCEL: // ��ָ��ס��ǰ�ؼ����ƶ������λ��ʱ����ʱ��ȡ���¼�
			getParent().requestDisallowInterceptTouchEvent(true); // true ���ø��ؼ����ص�ǰ�¼�
			break;
		default:
			break;
		}
		
		return super.dispatchTouchEvent(ev);
	}

}
