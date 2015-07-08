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
		requestWindowFeature(Window.FEATURE_NO_TITLE);//�Ƴ�����
		setContentView(R.layout.main);//����������
		setBehindContentView(R.layout.main_left_menu);//������˵�
		//����SlidingMenu��ز���
		SlidingMenu mSlidingMenu = getSlidingMenu();
		//������໬��
		mSlidingMenu.setMode(SlidingMenu.LEFT);
		//����ȫ�����Ի���
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		//���������滬��ʱ����������Ļ�����ؿ��
		mSlidingMenu.setBehindOffset(450);
		
		initFragment();
	}

	private void initFragment() {
		FragmentManager fm = getSupportFragmentManager();//��ȡFragment����������
		FragmentTransaction ft = fm.beginTransaction();//�������񣬻�ȡ�������������
		ft.replace(R.id.fl_main_left_menu, new LeftMenuFragment(), "left_menu");//�����˵��Ĳ�����ӵ�fl_main_left_menu
		ft.commit();//�ύ����
		
		fm.beginTransaction().replace(R.id.fl_main, new HomeFragment(), "home").commit();
		
	}
	
	public LeftMenuFragment getLeftMenuFragment() {
		return (LeftMenuFragment) getSupportFragmentManager().findFragmentByTag("left_menu");
	}

	public HomeFragment getHomeFragment() {
		return (HomeFragment) getSupportFragmentManager().findFragmentByTag("home");
	}

}
