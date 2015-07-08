package com.weige.smart.fragment.base.impl;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Camera.Size;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.weige.smart.fragment.base.BasePager;

/**
 * @author weige 2015-4-16 下午1:11:55 
 * 主页面的导航条：设置选项
 */
public class SettingsPager extends BasePager {

	public SettingsPager(Context context) {
		super(context);
	}
	
	@Override
	public void initData() {
		ibMenu.setVisibility(View.GONE);
		tvTitle.setText("设置");
		
		TextView tv = new TextView(mContext);
		tv.setText("设置");
		tv.setTextSize(25);
		tv.setTextColor(Color.RED);
		tv.setGravity(Gravity.CENTER);
		
		flContent.addView(tv);
	}

}
