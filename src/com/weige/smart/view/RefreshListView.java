package com.weige.smart.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.AvoidXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.weige.smart.R;

public class RefreshListView extends ListView implements OnScrollListener {

	private LinearLayout mHeadViewRoot;
	private int downY = -1;
	private int mPullDownHeaderViewHeight; // 下拉头布局的高度
	private int mFirstVisiblePosition = -1; // 当前ListView第一个显示的item索引 
	private View mPullDownHeaderView; // 下拉刷新头布局
	
	private final int PULL_DOWN = 0; // 下拉刷新状态
	private final int RELEASE_REFRESH = 1; // 释放刷新
	private final int REFRESHING = 2; // 正在刷新中状态
	
	private int currentState = PULL_DOWN; // 当前下拉头的状态，默认为：下拉刷新状态
	private RotateAnimation upAnim; // 向上旋转动画
	private RotateAnimation downAnim; // 向下旋转动画
	private ImageView ivArrow; // 头布局的箭头
	private ProgressBar mProgressBar; // 头布局的进度条
	private TextView tvState; // 头布局的状态
	private TextView tvDate; // 头布局的最后刷新时间
	private View mCustomHeaderView; // 用户添加进来的头布局文件（轮播图）
	private OnRefreshListener mOnRefreshListener; // 当前ListView刷新数据的监听事件
	private View mFooterView; // 脚布局对象
	private int mFooterViewHeight; // 脚布局的高度
	private boolean isLoadingMore = false; // 是否正在加载更多，默认是没有加载
	
	private boolean isEnablePullDownRefresh = false; // 是否启用下拉刷新的功能，默认为：不启用
	private boolean isEnableLoadingMore = false; // 是否启用加载更多的功能，默认为：不启用

	public RefreshListView(Context context) {
		super(context);
		initHeader();
		initFooter();
		setOnScrollListener(this);
	}

	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initHeader();
		initFooter();
		setOnScrollListener(this);
	}

	/**
	 * 初始化脚布局
	 */
	private void initFooter() {
		mFooterView = View.inflate(getContext(), R.layout.refresh_listview_footerview, null);
		mFooterView.measure(0, 0); // 测量脚布局
		mFooterViewHeight = mFooterView.getMeasuredHeight();
		mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);
		this.addFooterView(mFooterView);
	}

	private void initHeader() {

		View mHeaderView = View.inflate(getContext(),
				R.layout.refresh_listview_header, null);
		mHeadViewRoot = (LinearLayout) mHeaderView
				.findViewById(R.id.ll_refresh_listview_header_root);
		mPullDownHeaderView = mHeaderView
				.findViewById(R.id.ll_pull_down_view);
		ivArrow = (ImageView) mHeaderView
				.findViewById(R.id.iv_refresh_listview_arrow);
		mProgressBar = (ProgressBar) mHeaderView
				.findViewById(R.id.pb_refresh_listview);
		tvState = (TextView) mHeaderView
				.findViewById(R.id.tv_refresh_listview_state);
		tvDate = (TextView) mHeaderView
				.findViewById(R.id.tv_refresh_listview_last_update_time);
		
		tvDate.setText("最后刷新时间：" + getCurrentTime());

		// 把下拉头布局隐藏
		mPullDownHeaderView.measure(0, 0); // 测量下拉头布局
		mPullDownHeaderViewHeight = mPullDownHeaderView.getMeasuredHeight();
		mPullDownHeaderView.setPadding(0, -mPullDownHeaderViewHeight, 0, 0);
		this.addHeaderView(mHeaderView);
		
		initAnimation();
	}

	private void initAnimation() {
		upAnim = new RotateAnimation(
				0, -180, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		upAnim.setDuration(500);
		upAnim.setFillAfter(true); // 把控件停止在动画结束的状态下
		
		
		downAnim = new RotateAnimation(
				-180, -360, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		downAnim.setDuration(500);
		downAnim.setFillAfter(true); // 把控件停止在动画结束的状态下
		
	}

	public void addCustomHeaderView(View v) {
		mCustomHeaderView = v;
		mHeadViewRoot.addView(v);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downY  = (int) ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			// 判断是否启用下拉刷新的操作
			if (!isEnablePullDownRefresh) {
				// 当前没有启用
				break;
			}
			// 如果当前是正在刷新的操作，直接跳出，不执行下拉操作
			if (currentState == REFRESHING) {
				break;
			}
			
			if (mCustomHeaderView != null) {
				
				// 如果轮播图，没有完全显示，不应该进行下拉操作，直接跳出
				int[] location = new int[2];
				this.getLocationOnScreen(location); // 取出当前ListView在屏幕中Y轴的值
				int mListViewLocationOnScreenY = location[1];
				
				// 取出mCustomHeaderView在屏幕中Y轴的值
				mCustomHeaderView.getLocationOnScreen(location);
				int mCustomHeaderViewLocationOnScreenY = location[1];
				
				if(mCustomHeaderViewLocationOnScreenY < mListViewLocationOnScreenY) {
					break;
				}
			}
			
			if (downY == -1) {
				downY  = (int) ev.getY();
			}
			int moveY = (int) ev.getY();
			
			// 计算下拉后下拉头的paddingTop值
			int paddingTop = -mPullDownHeaderViewHeight + (moveY - downY);
			
			if (paddingTop > -mPullDownHeaderViewHeight && mFirstVisiblePosition == 0) {
				if (paddingTop > 0 && currentState == PULL_DOWN) {
					currentState = RELEASE_REFRESH;
					refreshPullDownHeaderView();
				} else if(paddingTop < 0 && currentState == RELEASE_REFRESH) {
					currentState = PULL_DOWN;
					refreshPullDownHeaderView();
				}
				
				
				mPullDownHeaderView.setPadding(0, paddingTop, 0, 0);
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			downY = -1;
			
			if(currentState == RELEASE_REFRESH) {
				// 当前是释放刷新，进入到正在刷新中操作
				currentState = REFRESHING;
				refreshPullDownHeaderView();
				
				mPullDownHeaderView.setPadding(0, 0, 0, 0);
				
				if (mOnRefreshListener != null) {
					mOnRefreshListener.OnPullDownRefresh(); // 回调用户的事件
				}
			} else if(currentState == PULL_DOWN) {
				// 当前是下拉刷新，下拉头隐藏
				mPullDownHeaderView.setPadding(0, -mPullDownHeaderViewHeight, 0, 0);
			}
			break;

		default:
			break;
		}

		return super.onTouchEvent(ev);
	}
	
	private void refreshPullDownHeaderView() {
		switch (currentState) {
		case PULL_DOWN: // 当前是下拉刷新状态
			ivArrow.startAnimation(downAnim);
			tvState.setText("下拉刷新");
			break;
		case RELEASE_REFRESH: // 当前是释放刷新状态
			ivArrow.startAnimation(upAnim);
			tvState.setText("释放刷新");
			break;
		case REFRESHING: // 当前是正在刷新中状态
			ivArrow.setVisibility(View.INVISIBLE);
			ivArrow.clearAnimation();
			mProgressBar.setVisibility(View.VISIBLE);
			tvState.setText("正在刷新中...");
			break;

		default:
			break;
		}
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mFirstVisiblePosition = firstVisibleItem;
	}

	/**
	 * SCROLL_STATE_IDLE 空闲停滞
	 * SCROLL_STATE_TOUCH_SCROLL 手指按住时滑动
	 * SCROLL_STATE_FLING 快速的滑
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (!isEnableLoadingMore) {
			// 当前不启用加载更多功能，直接返回
			return;
		}
		
		// 如果滚动停止，或者快速滑动到底部，处理加载的操作
		if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_FLING) {
			if (this.getLastVisiblePosition() == (getCount() - 1) && !isLoadingMore) {
				mFooterView.setPadding(0, 0, 0, 0);
				// 让ListView滚动到底部
				this.setSelection(getCount());
				isLoadingMore = true;
				
				// 调用用户的回调事件，去加载更多的数据
				if(mOnRefreshListener != null) {
					mOnRefreshListener.onLoadingMore();
				}
			}
		}
	}
	
	public void setOnRefreshListener(OnRefreshListener listener) {
		this.mOnRefreshListener = listener;
	}
	
	/**
	 * 当用户刷新数据完成后，调用此方法，把头布局隐藏掉
	 */
	public void onRefreshFinish() {
		if (currentState == REFRESHING) {
			currentState = PULL_DOWN;
			mProgressBar.setVisibility(View.INVISIBLE);
			ivArrow.setVisibility(View.VISIBLE);
			tvState.setText("下拉刷新");
			tvDate.setText("最后刷新时间：" + getCurrentTime());
			
			mPullDownHeaderView.setPadding(0, -mPullDownHeaderViewHeight, 0, 0);
		} else if (isLoadingMore) {
			// 当前是加载更多完毕，回来把脚布局隐藏掉
			isLoadingMore = false;
			mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);
		}
	}
	
	private String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}
	
	/**
	 * 设置是否启用下拉刷新功能
	 * @param isEnablePullDownRefresh true是启用
	 */
	public void setEnablePullDownRefresh(boolean isEnablePullDownRefresh) {
		this.isEnablePullDownRefresh = isEnablePullDownRefresh;
	}
	
	/**
	 * 设置是否启用加载更多功能
	 * @param isEnablePullDownRefresh true是启用
	 */
	public void setEnableLoadingMore(boolean isEnableLoadingMore) {
		this.isEnableLoadingMore = isEnableLoadingMore;
	}

}
