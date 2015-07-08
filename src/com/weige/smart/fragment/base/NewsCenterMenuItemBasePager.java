package com.weige.smart.fragment.base;

import android.content.Context;
import android.view.View;

public abstract class NewsCenterMenuItemBasePager {
	
	public Context mContext;
	private View rootView;
	
	public NewsCenterMenuItemBasePager(Context context) {
		this.mContext = context;
		
		rootView = initView();
	}

	public abstract View initView();
	
	public View getRootView() {
		return rootView;
	}
	
	public void initData() {
		
	}
}
