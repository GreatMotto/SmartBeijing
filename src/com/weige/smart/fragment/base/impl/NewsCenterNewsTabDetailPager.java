package com.weige.smart.fragment.base.impl;

import java.util.List;

import org.w3c.dom.Text;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.weige.smart.MainUI;
import com.weige.smart.NewsDetailUI;
import com.weige.smart.R;
import com.weige.smart.domain.NewsCenterBean.NewsItem;
import com.weige.smart.domain.NewsTabDetailBean;
import com.weige.smart.domain.NewsTabDetailBean.New;
import com.weige.smart.domain.NewsTabDetailBean.TopNew;
import com.weige.smart.fragment.base.NewsCenterMenuItemBasePager;
import com.weige.smart.utils.CacheUtils;
import com.weige.smart.utils.Constants;
import com.weige.smart.view.HorizontalScrollViewPager;
import com.weige.smart.view.OnRefreshListener;
import com.weige.smart.view.RefreshListView;

public class NewsCenterNewsTabDetailPager extends NewsCenterMenuItemBasePager
		implements OnPageChangeListener, OnRefreshListener, OnItemClickListener {

	private NewsItem mNewsItem;

	@ViewInject(R.id.hsvp_news_tab_detail_pager_topnews)
	private HorizontalScrollViewPager mTopNewsViewPager; // 顶部新闻轮播图

	@ViewInject(R.id.tv_news_tab_detail_pager_description)
	private TextView tvDescription; // 轮播图描述信息

	@ViewInject(R.id.ll_news_tab_detail_pager_point_group)
	private LinearLayout llPointGroup; // 轮播图切换的点的父布局

	@ViewInject(R.id.rlv_news_tab_detail_pager_news_list)
	private RefreshListView mListView; // 新闻信息列表

	private String url; // 当前新闻的请求地址

	private List<TopNew> topNewsList; // 轮播图的数据集合
	private List<New> newsList; // 新闻信息数据

	private BitmapUtils bitmapUtils;
	private int previousEnabledPosition = 0; // 前一个被选中点的索引：默认为0
	private String moreUrl; // 加载更多数据的链接地址
	
	private boolean isLoadMore = false; // 当前是否是加载更多出来的数据

	private NewsAdapter mNewsAdapter;

	private TopNewsAdapter mTopNewsAdapter;

	private InternalHandler mHandler;

	public NewsCenterNewsTabDetailPager(Context context) {
		super(context);
	}

	public NewsCenterNewsTabDetailPager(Context context, NewsItem newsItem) {
		this(context);
		this.mNewsItem = newsItem;

		bitmapUtils = new BitmapUtils(mContext);
		bitmapUtils.configDefaultBitmapConfig(Config.ARGB_4444);
	}

	@Override
	public View initView() {
		View view = View
				.inflate(mContext, R.layout.news_tab_detail_pager, null);
		ViewUtils.inject(this, view);

		View topNewView = View.inflate(mContext,
				R.layout.newscenter_tad_detail_topnew, null);
		ViewUtils.inject(this, topNewView);

		mListView.addCustomHeaderView(topNewView);
		mListView.setEnablePullDownRefresh(true);
		mListView.setEnableLoadingMore(true);
		mListView.setOnRefreshListener(this);
		mListView.setOnItemClickListener(this);
		return view;
	}

	@Override
	public void initData() {
		url = Constants.BASE_URL + mNewsItem.url;
		// 取本地缓存的新闻数据
		String result = CacheUtils.getStringData(mContext, url, null);
		if (!TextUtils.isEmpty(result)) {
			// 当前新闻数据不为空，需要先展示到界面上
			processData(result);
		}
		
		// 继续请求网络上最新的新闻信息
		HttpUtils utils = new HttpUtils();
		utils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// 把数据缓存到本地
				CacheUtils.cacheStringData(mContext, url, responseInfo.result);
				
				processData(responseInfo.result);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(mContext, mNewsItem.title + "新闻请求失败", 0).show();
				System.out.println("error " + msg);
			}
		});
	}

	protected void processData(String result) {
		Gson gson = new Gson();
		NewsTabDetailBean mNewsTabDetailBean = gson.fromJson(result, NewsTabDetailBean.class);
		
		if (TextUtils.isEmpty(mNewsTabDetailBean.data.more)) {
			// 当前没有更多数据了，不需要加载，把moreUrl置空
			moreUrl = null;
		} else {
			// 当前有更多数据，拼接url
			moreUrl = Constants.BASE_URL + mNewsTabDetailBean.data.more;
		}

		if (isLoadMore) {
			// 当前是加载更多的数据
			isLoadMore = false;
			// 需要在原有数据的基础上，添加加载出来的数据
			newsList.addAll(mNewsTabDetailBean.data.news);
		} else {
			topNewsList = mNewsTabDetailBean.data.topnews;
			newsList = mNewsTabDetailBean.data.news;
			
			if (mTopNewsAdapter == null) {
				mTopNewsAdapter = new TopNewsAdapter();
				mTopNewsViewPager.setAdapter(mTopNewsAdapter);
			} else {
				mTopNewsAdapter.notifyDataSetChanged();
			}
			
			// 初始化图片的描述信息和切换的点
			llPointGroup.removeAllViews(); // 初始化点之前把线性布局清空
			View view;
			LayoutParams lp;
			for (int i = 0; i < topNewsList.size(); i++) {
				view = new View(mContext);
				view.setBackgroundResource(R.drawable.point_bg);
				view.setEnabled(false);
				lp = new LayoutParams(8, 8);
				if (i != 0) {
					lp.leftMargin = 15;
				}
				view.setLayoutParams(lp);
				llPointGroup.addView(view);
			}
			// 设置第一个点选中，和图片描述信息
			previousEnabledPosition = 0;
			llPointGroup.getChildAt(previousEnabledPosition).setEnabled(true);
			tvDescription.setText(topNewsList.get(previousEnabledPosition).title);
			mTopNewsViewPager.setOnPageChangeListener(this);
			
			if (mHandler == null) {
				mHandler = new InternalHandler();
			}
			
			// 每一次开启任务时，把已经开启的任务和消息全部移除
			mHandler.removeCallbacksAndMessages(null);
			// 5秒钟之后会执行ViewPagerRollRunnable中的run方法
			mHandler.postDelayed(new ViewPagerRollRunnable(), 5000);
		}
		

		// 初始化新闻数据
		if (mNewsAdapter == null) {
			mNewsAdapter = new NewsAdapter();
			mListView.setAdapter(mNewsAdapter);
		} else {
			mNewsAdapter.notifyDataSetChanged(); // 通知ListView刷新数据
		}
	}

	class NewsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return newsList.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			NewsHolder mHolder = null;
			if (convertView == null) {
				convertView = View.inflate(mContext,
						R.layout.newscenter_tab_detail_news_item, null);
				mHolder = new NewsHolder();
				mHolder.ivImage = (ImageView) convertView
						.findViewById(R.id.iv_newscenter_tab_detail_news_item_icon);
				mHolder.tvTitle = (TextView) convertView
						.findViewById(R.id.tv_newscenter_tab_detail_news_item_title);
				mHolder.tvDate = (TextView) convertView
						.findViewById(R.id.tv_newscenter_tab_detail_news_item_date);
				
				convertView.setTag(mHolder);

			} else {
				mHolder = (NewsHolder) convertView.getTag();
			}
			New mNew = newsList.get(position);
			bitmapUtils.display(mHolder.ivImage, mNew.listimage);
			mHolder.tvTitle.setText(mNew.title);
			mHolder.tvDate.setText(mNew.pubdate);
			
			
			

			return convertView;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

	}

	class NewsHolder {
		public ImageView ivImage;
		public TextView tvTitle;
		public TextView tvDate;
	}

	class TopNewsAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return topNewsList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView iv = (ImageView) View.inflate(mContext,
					R.layout.news_tab_detail_topnews_item, null);
			TopNew topNew = topNewsList.get(position);

			// 根据图片的链接地址去请求图片并设置给iv
			bitmapUtils.display(iv, topNew.topimage);
			container.addView(iv); // 把ImageView添加到ViewPager中
			
			iv.setOnTouchListener(new TopNewsItemOnTouchListener());
			
			return iv;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}
	
	class TopNewsItemOnTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				System.out.println("按下停止滚动");
				mHandler.removeCallbacksAndMessages(null);
				break;
				
			case MotionEvent.ACTION_UP:
				System.out.println("抬起开始滚动");
				mHandler.postDelayed(new ViewPagerRollRunnable(), 5000);
				break;

			default:
				break;
			}
			return true;
		}
		
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int position) {
		llPointGroup.getChildAt(position).setEnabled(true);
		llPointGroup.getChildAt(previousEnabledPosition).setEnabled(false);
		tvDescription.setText(topNewsList.get(position).title);

		previousEnabledPosition = position;
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnPullDownRefresh() {
		getNewsDataFromNet(url);
	}

	@Override
	public void onLoadingMore() {
		if (TextUtils.isEmpty(moreUrl)) {
			mListView.onRefreshFinish();
			Toast.makeText(mContext, "当前没有更多新闻数据了", 0).show();
			return;
		}
		isLoadMore = true;
		getNewsDataFromNet(moreUrl);
	}
	
	private void getNewsDataFromNet(final String url) {
		HttpUtils utils = new HttpUtils();
		utils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				mListView.onRefreshFinish();
				// 把数据缓存到本地
				CacheUtils.cacheStringData(mContext, url, responseInfo.result);
				
				processData(responseInfo.result);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				isLoadMore = false;
				mListView.onRefreshFinish();
				Toast.makeText(mContext, mNewsItem.title + "新闻请求失败", 0).show();
				System.out.println("error " + msg);
			}
		});
	}
	
	/**
	 * @author Administrator
	 * 内部Handler，用于切换轮播图
	 */
	class InternalHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			mTopNewsViewPager.setCurrentItem((mTopNewsViewPager.getCurrentItem() + 1) % topNewsList.size());
			postDelayed(new ViewPagerRollRunnable(), 5000);
		}
		
	}
	
	/**
	 * @author Administrator
	 * 轮播图刷新的任务类
	 */
	class ViewPagerRollRunnable implements Runnable {

		@Override
		public void run() {
			// 发送一个消息到handleMessage方法，让它切换图片去
			mHandler.obtainMessage().sendToTarget();
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		New mNew = newsList.get(position - 1);
		Intent intent = new Intent(mContext, NewsDetailUI.class);
		intent.putExtra("url", mNew.url);
		mContext.startActivity(intent);
	}

}
