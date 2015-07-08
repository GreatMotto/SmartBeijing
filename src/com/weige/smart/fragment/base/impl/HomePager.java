package com.weige.smart.fragment.base.impl;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Camera.Size;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.weige.smart.fragment.base.BasePager;

/**
 * @author weige 2015-4-16 ����1:11:55 
 * ��ҳ��ĵ���������ҳѡ��
 */
public class HomePager extends BasePager {

	public HomePager(Context context) {
		super(context);
	}
	
	@Override
	public void initData() {
		ibMenu.setVisibility(View.GONE);
		tvTitle.setText("�ǻ۱���");
		
		TextView tv = new TextView(mContext);
		tv.setText("��ҳ");
		tv.setTextSize(25);
		tv.setTextColor(Color.RED);
		tv.setGravity(Gravity.CENTER);
		
		flContent.addView(tv);
	}

}
