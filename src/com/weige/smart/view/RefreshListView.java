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
	private int mPullDownHeaderViewHeight; // ����ͷ���ֵĸ߶�
	private int mFirstVisiblePosition = -1; // ��ǰListView��һ����ʾ��item���� 
	private View mPullDownHeaderView; // ����ˢ��ͷ����
	
	private final int PULL_DOWN = 0; // ����ˢ��״̬
	private final int RELEASE_REFRESH = 1; // �ͷ�ˢ��
	private final int REFRESHING = 2; // ����ˢ����״̬
	
	private int currentState = PULL_DOWN; // ��ǰ����ͷ��״̬��Ĭ��Ϊ������ˢ��״̬
	private RotateAnimation upAnim; // ������ת����
	private RotateAnimation downAnim; // ������ת����
	private ImageView ivArrow; // ͷ���ֵļ�ͷ
	private ProgressBar mProgressBar; // ͷ���ֵĽ�����
	private TextView tvState; // ͷ���ֵ�״̬
	private TextView tvDate; // ͷ���ֵ����ˢ��ʱ��
	private View mCustomHeaderView; // �û���ӽ�����ͷ�����ļ����ֲ�ͼ��
	private OnRefreshListener mOnRefreshListener; // ��ǰListViewˢ�����ݵļ����¼�
	private View mFooterView; // �Ų��ֶ���
	private int mFooterViewHeight; // �Ų��ֵĸ߶�
	private boolean isLoadingMore = false; // �Ƿ����ڼ��ظ��࣬Ĭ����û�м���
	
	private boolean isEnablePullDownRefresh = false; // �Ƿ���������ˢ�µĹ��ܣ�Ĭ��Ϊ��������
	private boolean isEnableLoadingMore = false; // �Ƿ����ü��ظ���Ĺ��ܣ�Ĭ��Ϊ��������

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
	 * ��ʼ���Ų���
	 */
	private void initFooter() {
		mFooterView = View.inflate(getContext(), R.layout.refresh_listview_footerview, null);
		mFooterView.measure(0, 0); // �����Ų���
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
		
		tvDate.setText("���ˢ��ʱ�䣺" + getCurrentTime());

		// ������ͷ��������
		mPullDownHeaderView.measure(0, 0); // ��������ͷ����
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
		upAnim.setFillAfter(true); // �ѿؼ�ֹͣ�ڶ���������״̬��
		
		
		downAnim = new RotateAnimation(
				-180, -360, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		downAnim.setDuration(500);
		downAnim.setFillAfter(true); // �ѿؼ�ֹͣ�ڶ���������״̬��
		
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
			// �ж��Ƿ���������ˢ�µĲ���
			if (!isEnablePullDownRefresh) {
				// ��ǰû������
				break;
			}
			// �����ǰ������ˢ�µĲ�����ֱ����������ִ����������
			if (currentState == REFRESHING) {
				break;
			}
			
			if (mCustomHeaderView != null) {
				
				// ����ֲ�ͼ��û����ȫ��ʾ����Ӧ�ý�������������ֱ������
				int[] location = new int[2];
				this.getLocationOnScreen(location); // ȡ����ǰListView����Ļ��Y���ֵ
				int mListViewLocationOnScreenY = location[1];
				
				// ȡ��mCustomHeaderView����Ļ��Y���ֵ
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
			
			// ��������������ͷ��paddingTopֵ
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
				// ��ǰ���ͷ�ˢ�£����뵽����ˢ���в���
				currentState = REFRESHING;
				refreshPullDownHeaderView();
				
				mPullDownHeaderView.setPadding(0, 0, 0, 0);
				
				if (mOnRefreshListener != null) {
					mOnRefreshListener.OnPullDownRefresh(); // �ص��û����¼�
				}
			} else if(currentState == PULL_DOWN) {
				// ��ǰ������ˢ�£�����ͷ����
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
		case PULL_DOWN: // ��ǰ������ˢ��״̬
			ivArrow.startAnimation(downAnim);
			tvState.setText("����ˢ��");
			break;
		case RELEASE_REFRESH: // ��ǰ���ͷ�ˢ��״̬
			ivArrow.startAnimation(upAnim);
			tvState.setText("�ͷ�ˢ��");
			break;
		case REFRESHING: // ��ǰ������ˢ����״̬
			ivArrow.setVisibility(View.INVISIBLE);
			ivArrow.clearAnimation();
			mProgressBar.setVisibility(View.VISIBLE);
			tvState.setText("����ˢ����...");
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
	 * SCROLL_STATE_IDLE ����ͣ��
	 * SCROLL_STATE_TOUCH_SCROLL ��ָ��סʱ����
	 * SCROLL_STATE_FLING ���ٵĻ�
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (!isEnableLoadingMore) {
			// ��ǰ�����ü��ظ��๦�ܣ�ֱ�ӷ���
			return;
		}
		
		// �������ֹͣ�����߿��ٻ������ײ���������صĲ���
		if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_FLING) {
			if (this.getLastVisiblePosition() == (getCount() - 1) && !isLoadingMore) {
				mFooterView.setPadding(0, 0, 0, 0);
				// ��ListView�������ײ�
				this.setSelection(getCount());
				isLoadingMore = true;
				
				// �����û��Ļص��¼���ȥ���ظ��������
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
	 * ���û�ˢ��������ɺ󣬵��ô˷�������ͷ�������ص�
	 */
	public void onRefreshFinish() {
		if (currentState == REFRESHING) {
			currentState = PULL_DOWN;
			mProgressBar.setVisibility(View.INVISIBLE);
			ivArrow.setVisibility(View.VISIBLE);
			tvState.setText("����ˢ��");
			tvDate.setText("���ˢ��ʱ�䣺" + getCurrentTime());
			
			mPullDownHeaderView.setPadding(0, -mPullDownHeaderViewHeight, 0, 0);
		} else if (isLoadingMore) {
			// ��ǰ�Ǽ��ظ�����ϣ������ѽŲ������ص�
			isLoadingMore = false;
			mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);
		}
	}
	
	private String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}
	
	/**
	 * �����Ƿ���������ˢ�¹���
	 * @param isEnablePullDownRefresh true������
	 */
	public void setEnablePullDownRefresh(boolean isEnablePullDownRefresh) {
		this.isEnablePullDownRefresh = isEnablePullDownRefresh;
	}
	
	/**
	 * �����Ƿ����ü��ظ��๦��
	 * @param isEnablePullDownRefresh true������
	 */
	public void setEnableLoadingMore(boolean isEnableLoadingMore) {
		this.isEnableLoadingMore = isEnableLoadingMore;
	}

}
