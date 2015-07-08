package com.weige.smart.fragment.base.impl;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.weige.smart.MainUI;
import com.weige.smart.domain.NewsCenterBean;
import com.weige.smart.fragment.base.BasePager;

/**
 * @author weige 2015-4-16 下午1:11:55 
 * 主页面的导航条：智慧服务选项
 */
public class SmartServicePager extends BasePager {

	public SmartServicePager(Context context) {
		super(context);
	}
	
	@Override
	public void initData() {
		ibMenu.setVisibility(View.VISIBLE);
		tvTitle.setText("生活");
		
		TextView tv = new TextView(mContext);
		tv.setText("智慧服务");
		tv.setTextSize(25);
		tv.setTextColor(Color.RED);
		tv.setGravity(Gravity.CENTER);
		
		flContent.addView(tv);
		
		((MainUI) mContext).getLeftMenuFragment().setMenuListData(new ArrayList<NewsCenterBean.MenuItem>());
	}

}
