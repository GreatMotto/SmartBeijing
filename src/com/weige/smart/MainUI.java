package com.weige.smart;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.weige.smart.fragment.HomeFragment;
import com.weige.smart.fragment.LeftMenuFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

public class MainUI extends SlidingFragmentActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//移除标题
		setContentView(R.layout.main);//设置主界面
		setBehindContentView(R.layout.main_left_menu);//设置左菜单
		//设置SlidingMenu相关参数
		SlidingMenu mSlidingMenu = getSlidingMenu();
		//设置左侧滑动
		mSlidingMenu.setMode(SlidingMenu.LEFT);
		//设置全屏可以滑动
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		//设置主界面滑动时可以留在屏幕的像素宽度
		mSlidingMenu.setBehindOffset(450);
		
		initFragment();
	}

	private void initFragment() {
		FragmentManager fm = getSupportFragmentManager();//获取Fragment管理器对象
		FragmentTransaction ft = fm.beginTransaction();//开启事务，获取事务管理器对象
		ft.replace(R.id.fl_main_left_menu, new LeftMenuFragment(), "left_menu");//把左侧菜单的布局添加到fl_main_left_menu
		ft.commit();//提交事务
		
		fm.beginTransaction().replace(R.id.fl_main, new HomeFragment(), "home").commit();
		
	}
	
	public LeftMenuFragment getLeftMenuFragment() {
		return (LeftMenuFragment) getSupportFragmentManager().findFragmentByTag("left_menu");
	}

	public HomeFragment getHomeFragment() {
		return (HomeFragment) getSupportFragmentManager().findFragmentByTag("home");
	}

}
