package com.weige.smart;

import java.util.ArrayList;
import java.util.List;
import com.weige.smart.utils.CacheUtils;
import com.weige.smart.utils.Constants;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
/**
 * ��������
 */
public class GuideUI extends Activity implements OnPageChangeListener, OnClickListener {
	private Button btnStartExperience;
	private LinearLayout llPointGroup;
	private List<ImageView> imageViewList;
	private View mSelectedPoint;
	private int basicWidth = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ȥ����, ��Ҫ��SetContentView֮ǰ
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.guide);

		initView();
	}

	/**
	 * ��ʼ���ؼ�
	 */
	private void initView() {
		ViewPager mViewpager = (ViewPager) findViewById(R.id.viewpager_guide);
		btnStartExperience = (Button) findViewById(R.id.btn_guide_start_experience);
		llPointGroup = (LinearLayout) findViewById(R.id.ll_guide_point_group);
		mSelectedPoint = findViewById(R.id.v_guide_selected_point);

		imageViewList = new ArrayList<ImageView>();
		int[] imageIDs = { R.drawable.guide_1, R.drawable.guide_2,
				R.drawable.guide_3 };
		ImageView iv;
		View view;
		for (int i = 0; i < imageIDs.length; i++) {
			iv = new ImageView(this);
			iv.setBackgroundResource(imageIDs[i]);
			imageViewList.add(iv);
			
			// ��LinearLayout�����һ����Ŀؼ�
			view = new View(this);
			LayoutParams params = new LayoutParams(20, 20);
			if (i != 0) {
				params.leftMargin = 20;
			}
			view.setLayoutParams(params);
			view.setBackgroundResource(R.drawable.guide_point_gray_bg);
			llPointGroup.addView(view);
		}
		// ��Adapter��������ViewPager��������
		GuideAdapter mAdapter = new GuideAdapter();
		mViewpager.setAdapter(mAdapter);
		mViewpager.setOnPageChangeListener(this);
		btnStartExperience.setOnClickListener(this);

	}

	class GuideAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imageViewList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// ��ViewPager�����һ��ImageView����, �����ڴ˷����а��Ǹ�����ӵ�imageView���󷵻�
			ImageView imageView = imageViewList.get(position);
			container.addView(imageView);
			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// �Ѷ�Ӧpositionλ�õ�Imageview��ViewPager���Ƴ���
			container.removeView(imageViewList.get(position));
		}

	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		if(basicWidth == -1){
			basicWidth = llPointGroup.getChildAt(1).getLeft() - llPointGroup.getChildAt(0).getLeft();
		}
		int leftMargin = (int) ((position + positionOffset) * basicWidth);
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(20, 20);
		params.leftMargin = leftMargin;
		mSelectedPoint.setLayoutParams(params);
	}

	@Override
	public void onPageSelected(int position) {
		if(position == imageViewList.size() - 1){
			btnStartExperience.setVisibility(View.VISIBLE);
		} else {
			btnStartExperience.setVisibility(View.GONE);
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		
	}

	@Override
	public void onClick(View v) {
		CacheUtils.cacheBooleanData(this, Constants.IS_OPEN_MAIN_PAGE_KEY, true);
		startActivity(new Intent(this, MainUI.class));
		finish();
	}

}
