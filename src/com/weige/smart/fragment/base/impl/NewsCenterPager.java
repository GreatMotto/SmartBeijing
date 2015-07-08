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
 * @author weige 2015-4-16 ����1:11:55 
 * ��ҳ��ĵ���������������ѡ��
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
		tvTitle.setText("����");
		loadData();
	}

	private void loadData() {
		String result = CacheUtils.getStringData(mContext, Constants.NEWSCENTER_URL, null);
		if(!TextUtils.isEmpty(result)) {
			System.out.println("����ȡ�������������ݣ�");
			processData(result);
		}
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.GET, Constants.NEWSCENTER_URL, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				Toast.makeText(mContext, "�����������ݷ��ʳɹ�", 0).show();
				CacheUtils.cacheStringData(mContext, Constants.NEWSCENTER_URL, responseInfo.result);
				processData(responseInfo.result);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(mContext, "�����������ݷ���ʧ��", 0).show();
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
