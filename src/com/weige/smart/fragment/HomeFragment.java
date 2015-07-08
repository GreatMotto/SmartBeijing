package com.weige.smart.fragment;

import java.util.ArrayList;
import java.util.List;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.weige.smart.MainUI;
import com.weige.smart.R;
import com.weige.smart.fragment.base.BasePager;
import com.weige.smart.fragment.base.impl.GovaffairsPager;
import com.weige.smart.fragment.base.impl.HomePager;
import com.weige.smart.fragment.base.impl.NewsCenterPager;
import com.weige.smart.fragment.base.impl.SettingsPager;
import com.weige.smart.fragment.base.impl.SmartServicePager;
import com.weige.smart.view.NoScrollViewPager;

import android.R.anim;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts.Data;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * @author weige
 * 主界面的Fragment
 */
public class HomeFragment extends Fragment implements OnCheckedChangeListener {
	
	@ViewInject(R.id.nsvp_main_home)
	private NoScrollViewPager mViewPager;

	@ViewInject(R.id.rg_main_home)
	private RadioGroup mRadioGroup;

	private List<BasePager> pagerList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_home, null);
		ViewUtils.inject(this, view);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		init();
	}

	private void init() {
		mRadioGroup.setOnCheckedChangeListener(this);
		mRadioGroup.check(R.id.rb_main_home_tab_home);
		
		pagerList = new ArrayList<BasePager>();
		pagerList.add(new HomePager(getActivity()));
		pagerList.add(new NewsCenterPager(getActivity()));
		pagerList.add(new SmartServicePager(getActivity()));
		pagerList.add(new GovaffairsPager(getActivity()));
		pagerList.add(new SettingsPager(getActivity()));
		
		HomeTabAdapter mTabAdapter = new HomeTabAdapter();
		mViewPager.setAdapter(mTabAdapter);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.rb_main_home_tab_home:
			//设置不可拉出菜单
			((MainUI) getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
			mViewPager.setCurrentItem(0);
			break;
		case R.id.rb_main_home_tab_newscenter:
			//设置可拉出菜单
			((MainUI) getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			mViewPager.setCurrentItem(1);
			break;
		case R.id.rb_main_home_tab_smartservice:
			//设置可拉出菜单
			((MainUI) getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			mViewPager.setCurrentItem(2);
			break;
		case R.id.rb_main_home_tab_govaffairs:
			//设置可拉出菜单
			((MainUI) getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			mViewPager.setCurrentItem(3);
			break;
		case R.id.rb_main_home_tab_settings:
			//设置不可拉出菜单
			((MainUI) getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
			mViewPager.setCurrentItem(4);
			break;

		default:
			break;
		}
	}
	
	class HomeTabAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return pagerList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(pagerList.get(position).getRootView());
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			System.out.println("加载：" + position);
			BasePager pager = pagerList.get(position);
			container.addView(pager.getRootView());
			pager.initData();
			return pager.getRootView();
		}
		
	}
	
	public NewsCenterPager getNewsCenterPager() {
		return (NewsCenterPager) pagerList.get(1);
	}
}
