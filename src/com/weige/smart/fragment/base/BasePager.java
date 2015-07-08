package com.weige.smart.fragment.base;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.weige.smart.MainUI;
import com.weige.smart.R;

public class BasePager implements OnClickListener {
	public Context mContext;
	private View rootView;

	public ImageButton ibMenu;
	public TextView tvTitle;
	public FrameLayout flContent;

	public BasePager(Context context) {
		this.mContext = context;

		rootView = initView();
	}
	

	private View initView() {
		View view = View.inflate(mContext, R.layout.base_pager, null);
		ibMenu = (ImageButton) view.findViewById(R.id.ib_title_bar_menu);
		tvTitle = (TextView) view.findViewById(R.id.tv_title_bar_title);
		flContent = (FrameLayout) view.findViewById(R.id.fl_base_pager_content);
		ibMenu.setOnClickListener(this);
		return view;
	}
	
	/**
	 * 获取当前基类的View
	 * @return
	 */
	public View getRootView() {
		return rootView;
	}

	/**
	 * 初始化数据
	 */
	public void initData() {
	}


	@Override
	public void onClick(View v) {
		((MainUI) mContext).getSlidingMenu().toggle();
	}

}
