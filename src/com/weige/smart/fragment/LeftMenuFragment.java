package com.weige.smart.fragment;

import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.weige.smart.MainUI;
import com.weige.smart.R;
import com.weige.smart.domain.NewsCenterBean.MenuItem;

/**
 * @author weige 2015-4-15 上午11:58:17 左侧菜单的Fragment
 */
public class LeftMenuFragment extends Fragment implements OnItemClickListener {

	private ListView mListView;
	private int currentSelectedPosition = 0;
	private MenuAdapter mAdapter;
	private List<MenuItem> menuItemList;

	/**
	 * 此方法返回的View对象，作为当前Fragment的布局来展示
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mListView = new ListView(getActivity());
		mListView.setPadding(0, 150, 0, 0);
		mListView.setCacheColorHint(Color.TRANSPARENT);
		mListView.setDividerHeight(0);
		mListView.setSelector(android.R.color.transparent);
		mListView.setOnItemClickListener(this);
		return mListView;
		
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	class MenuAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return menuItemList.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if (convertView == null) {
				view = View.inflate(getActivity(), R.layout.mian_left_menu_item, null);
			} else {
				view = convertView;
			}
			
			TextView tvText = (TextView) view.findViewById(R.id.tv_main_left_menu_item_text);
			ImageView ivImage = (ImageView) view.findViewById(R.id.iv_main_left_menu_item_image);
			tvText.setText(menuItemList.get(position).title);
			//根据当前position的位置刷新条目颜色
			tvText.setEnabled(currentSelectedPosition == position);
			ivImage.setEnabled(currentSelectedPosition == position);
			return view;
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		currentSelectedPosition = position;
		mAdapter.notifyDataSetChanged();//刷新ListView数据
		
		((MainUI) getActivity()).getSlidingMenu().toggle();
		
		((MainUI) getActivity()).getHomeFragment().getNewsCenterPager().switchCurrentPager(position);
	}

	public void setMenuListData(List<MenuItem> menuItemList) {
		this.menuItemList = menuItemList;
		
		if(mAdapter == null) {
			mAdapter = new MenuAdapter();
			mListView.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}
	}

}
