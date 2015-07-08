package com.weige.smart.fragment.base.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.viewpagerindicator.TabPageIndicator;
import com.weige.smart.MainUI;
import com.weige.smart.R;
import com.weige.smart.domain.NewsCenterBean.MenuItem;
import com.weige.smart.domain.NewsCenterBean.NewsItem;
import com.weige.smart.fragment.base.NewsCenterMenuItemBasePager;

public class NewsCenterMenuItemNewsPager extends NewsCenterMenuItemBasePager implements OnPageChangeListener {

	private MenuItem mMenuItemData;
	private List<NewsItem> newsItemList;
	
	@ViewInject(R.id.tpi_newscenter_menu_item_news_tab)
	private TabPageIndicator mTabPageIndicator;
	
	@ViewInject(R.id.vp_newscenter_menu_item_news_content)
	private ViewPager mViewPager;
	
	private List<NewsCenterNewsTabDetailPager> tabDetailPagerList;

	
	
	public NewsCenterMenuItemNewsPager(Context context, MenuItem menuItem) {
		super(context);
		
		this.mMenuItemData = menuItem;
	}
	
	public NewsCenterMenuItemNewsPager(Context context) {
		super(context);
	}

	@Override
	public View initView() {
		View view = View.inflate(mContext, R.layout.newscenter_menu_item_news, null);
		ViewUtils.inject(this, view);
		return view;
	}
	
	@Override
	public void initData() {
		newsItemList = mMenuItemData.children;
		tabDetailPagerList = new ArrayList<NewsCenterNewsTabDetailPager>();
		for (int i = 0; i < newsItemList.size(); i++) {
			tabDetailPagerList.add(new NewsCenterNewsTabDetailPager(mContext, newsItemList.get(i)));
		}
		
		NewsCenterMenuItemNewsAdapter mAdapter = new NewsCenterMenuItemNewsAdapter();
		mViewPager.setAdapter(mAdapter);
		
		mTabPageIndicator.setViewPager(mViewPager);
		
		mTabPageIndicator.setOnPageChangeListener(this);
	}
	
	class NewsCenterMenuItemNewsAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return newsItemList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			NewsCenterNewsTabDetailPager pager = tabDetailPagerList.get(position);
			container.addView(pager.getRootView());
			pager.initData();
			return pager.getRootView();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(tabDetailPagerList.get(position).getRootView());
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return newsItemList.get(position).title;
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}
	
	@Override
	public void onPageSelected(int position) {
		if (position == 0) {
			// 让菜单可用
			((MainUI) mContext).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} else {
			// 让菜单不可用
			((MainUI) mContext).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}
	
	@OnClick(R.id.ib_newscenter_menu_item_news_next_tab)
	public void nextTab(View v) {
		int newCurrentItemPosition = mViewPager.getCurrentItem() + 1;
		mViewPager.setCurrentItem(newCurrentItemPosition);
	}

}
