package com.weige.smart.fragment.base.impl;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.weige.smart.fragment.base.NewsCenterMenuItemBasePager;

public class NewsCenterMenuItemPhotosPager extends NewsCenterMenuItemBasePager {

	public NewsCenterMenuItemPhotosPager(Context context) {
		super(context);
	}

	@Override
	public View initView() {
		TextView tv = new TextView(mContext);
		tv.setText("×éÍ¼Ò³Ãæ");
		tv.setTextSize(25);
		tv.setTextColor(Color.RED);
		tv.setGravity(Gravity.CENTER);
		return tv;
	}

}
