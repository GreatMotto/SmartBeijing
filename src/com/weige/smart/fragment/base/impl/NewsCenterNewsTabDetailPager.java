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
	private HorizontalScrollViewPager mTopNewsViewPager; // ���������ֲ�ͼ

	@ViewInject(R.id.tv_news_tab_detail_pager_description)
	private TextView tvDescription; // �ֲ�ͼ������Ϣ

	@ViewInject(R.id.ll_news_tab_detail_pager_point_group)
	private LinearLayout llPointGroup; // �ֲ�ͼ�л��ĵ�ĸ�����

	@ViewInject(R.id.rlv_news_tab_detail_pager_news_list)
	private RefreshListView mListView; // ������Ϣ�б�

	private String url; // ��ǰ���ŵ������ַ

	private List<TopNew> topNewsList; // �ֲ�ͼ�����ݼ���
	private List<New> newsList; // ������Ϣ����

	private BitmapUtils bitmapUtils;
	private int previousEnabledPosition = 0; // ǰһ����ѡ�е��������Ĭ��Ϊ0
	private String moreUrl; // ���ظ������ݵ����ӵ�ַ
	
	private boolean isLoadMore = false; // ��ǰ�Ƿ��Ǽ��ظ������������

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
		// ȡ���ػ������������
		String result = CacheUtils.getStringData(mContext, url, null);
		if (!TextUtils.isEmpty(result)) {
			// ��ǰ�������ݲ�Ϊ�գ���Ҫ��չʾ��������
			processData(result);
		}
		
		// �����������������µ�������Ϣ
		HttpUtils utils = new HttpUtils();
		utils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// �����ݻ��浽����
				CacheUtils.cacheStringData(mContext, url, responseInfo.result);
				
				processData(responseInfo.result);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				Toast.makeText(mContext, mNewsItem.title + "��������ʧ��", 0).show();
				System.out.println("error " + msg);
			}
		});
	}

	protected void processData(String result) {
		Gson gson = new Gson();
		NewsTabDetailBean mNewsTabDetailBean = gson.fromJson(result, NewsTabDetailBean.class);
		
		if (TextUtils.isEmpty(mNewsTabDetailBean.data.more)) {
			// ��ǰû�и��������ˣ�����Ҫ���أ���moreUrl�ÿ�
			moreUrl = null;
		} else {
			// ��ǰ�и������ݣ�ƴ��url
			moreUrl = Constants.BASE_URL + mNewsTabDetailBean.data.more;
		}

		if (isLoadMore) {
			// ��ǰ�Ǽ��ظ��������
			isLoadMore = false;
			// ��Ҫ��ԭ�����ݵĻ����ϣ���Ӽ��س���������
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
			
			// ��ʼ��ͼƬ��������Ϣ���л��ĵ�
			llPointGroup.removeAllViews(); // ��ʼ����֮ǰ�����Բ������
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
			// ���õ�һ����ѡ�У���ͼƬ������Ϣ
			previousEnabledPosition = 0;
			llPointGroup.getChildAt(previousEnabledPosition).setEnabled(true);
			tvDescription.setText(topNewsList.get(previousEnabledPosition).title);
			mTopNewsViewPager.setOnPageChangeListener(this);
			
			if (mHandler == null) {
				mHandler = new InternalHandler();
			}
			
			// ÿһ�ο�������ʱ�����Ѿ��������������Ϣȫ���Ƴ�
			mHandler.removeCallbacksAndMessages(null);
			// 5����֮���ִ��ViewPagerRollRunnable�е�run����
			mHandler.postDelayed(new ViewPagerRollRunnable(), 5000);
		}
		

		// ��ʼ����������
		if (mNewsAdapter == null) {
			mNewsAdapter = new NewsAdapter();
			mListView.setAdapter(mNewsAdapter);
		} else {
			mNewsAdapter.notifyDataSetChanged(); // ֪ͨListViewˢ������
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

			// ����ͼƬ�����ӵ�ַȥ����ͼƬ�����ø�iv
			bitmapUtils.display(iv, topNew.topimage);
			container.addView(iv); // ��ImageView��ӵ�ViewPager��
			
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
				System.out.println("����ֹͣ����");
				mHandler.removeCallbacksAndMessages(null);
				break;
				
			case MotionEvent.ACTION_UP:
				System.out.println("̧��ʼ����");
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
			Toast.makeText(mContext, "��ǰû�и�������������", 0).show();
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
				// �����ݻ��浽����
				CacheUtils.cacheStringData(mContext, url, responseInfo.result);
				
				processData(responseInfo.result);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				isLoadMore = false;
				mListView.onRefreshFinish();
				Toast.makeText(mContext, mNewsItem.title + "��������ʧ��", 0).show();
				System.out.println("error " + msg);
			}
		});
	}
	
	/**
	 * @author Administrator
	 * �ڲ�Handler�������л��ֲ�ͼ
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
	 * �ֲ�ͼˢ�µ�������
	 */
	class ViewPagerRollRunnable implements Runnable {

		@Override
		public void run() {
			// ����һ����Ϣ��handleMessage�����������л�ͼƬȥ
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
