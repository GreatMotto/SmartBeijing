package com.weige.smart.fragment.base.impl;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.weige.smart.fragment.base.NewsCenterMenuItemBasePager;

public class NewsCenterMenuItemTopicPager extends NewsCenterMenuItemBasePager {

	public NewsCenterMenuItemTopicPager(Context context) {
		super(context);
	}

	@Override
	public View initView() {
		TextView tv = new TextView(mContext);
		tv.setText("×¨ÌâÒ³Ãæ");
		tv.setTextSize(25);
		tv.setTextColor(Color.RED);
		tv.setGravity(Gravity.CENTER);
		return tv;
	}

}
