package com.weige.smart.fragment.base.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.weige.smart.MainUI;
import com.weige.smart.domain.NewsCenterBean;
import com.weige.smart.fragment.LeftMenuFragment;
import com.weige.smart.fragment.base.BasePager;
import com.weige.smart.fragment.base.NewsCenterMenuItemBasePager;
import com.weige.smart.utils.CacheUtils;
import com.weige.smart.utils.Constants;

/**
 * @author weige 2015-4-16 下午1:11:55 
 * 主页面的导航条：新闻中心选项
 */
public class NewsCenterPager extends BasePager {

	private NewsCenterBean mNewsCenterBean;
	private List<NewsCenterMenuItemBasePager> menuItemPagerList;

	public NewsCenterPager(Context context) {
		super(context);
	}
	
	@Override
	public void initData() {
		ibMenu.setVisibility(View.VISIBLE);
		tvTitle.setText("新闻");
		loadData();
	}

	private void loadData() {
		String result = CacheUtils.getStringData(mContext, Constants.NEWSCENTER_URL, null);
		if(!TextUtils.isEmpty(result)) {
			System.out.println("本地取到新闻中心数据！");
			processData(result);
		}
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.GET, Constants.NEWSCENTER_URL, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				Toast.makeText(mContext, "新闻中心数据访问成功", 0).show();
				CacheUtils.cacheStringData(mContext, Constants.NEWSCENTER_URL, responseInfo.result);
				processData(responseInfo.result);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(mContext, "新闻中心数据访问失败", 0).show();
				System.out.println("error:" + msg);
			}
		});
	}

	protected void processData(String result) {
		Gson gson = new Gson();
		mNewsCenterBean = gson.fromJson(result, NewsCenterBean.class);
		List<com.weige.smart.domain.NewsCenterBean.MenuItem> menuItemList = mNewsCenterBean.data;
		LeftMenuFragment leftMenuFragment = ((MainUI) mContext).getLeftMenuFragment();
		leftMenuFragment.setMenuListData(menuItemList);
		
		menuItemPagerList = new ArrayList<NewsCenterMenuItemBasePager>();
		menuItemPagerList.add(new NewsCenterMenuItemNewsPager(mContext, mNewsCenterBean.data.get(0)));
		menuItemPagerList.add(new NewsCenterMenuItemTopicPager(mContext));
		menuItemPagerList.add(new NewsCenterMenuItemPhotosPager(mContext));
		menuItemPagerList.add(new NewsCenterMenuItemInteractionPager(mContext));
		
		switchCurrentPager(0);
	}
	
	public void switchCurrentPager(int position) {
		NewsCenterMenuItemBasePager menuItemPager = menuItemPagerList.get(position);
		flContent.removeAllViews();
		flContent.addView(menuItemPager.getRootView());
		
		tvTitle.setText(mNewsCenterBean.data.get(position).title);
		
		menuItemPager.initData();
	}

}
