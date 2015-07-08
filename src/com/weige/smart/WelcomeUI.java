package com.weige.smart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;

import com.weige.smart.utils.CacheUtils;
import com.weige.smart.utils.Constants;
/**
 * 欢迎界面
 */
public class WelcomeUI extends Activity implements AnimationListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        
        init();
    }

	/**
	 * 初始化动画
	 */
	private void init() {
		View rlRootView = findViewById(R.id.rl_root);
		
		RotateAnimation ra = new RotateAnimation(
				0, 360, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		ra.setDuration(1000);
		
		ScaleAnimation sa = new ScaleAnimation(
				0, 1, 
				0, 1, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		sa.setDuration(1000);
		
		AlphaAnimation aa = new AlphaAnimation(0, 1);
		aa.setDuration(2000);
		
		//把三个动画合并到一个集合中
		AnimationSet animSet = new AnimationSet(false);
		animSet.addAnimation(ra);
		animSet.addAnimation(sa);
		animSet.addAnimation(aa);
		animSet.setAnimationListener(this);
		rlRootView.startAnimation(animSet);
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		boolean isOpenMainPage = CacheUtils.getBooleanData(this, Constants.IS_OPEN_MAIN_PAGE_KEY, false);
		if(isOpenMainPage){
			//打开主界面
			startActivity(new Intent(this, MainUI.class));
		} else {
			//打开引导界面
			startActivity(new Intent(this, GuideUI.class));
		}
		finish();
	}

	@Override
	public void onAnimationStart(Animation animation) {
		
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		
	}
}
