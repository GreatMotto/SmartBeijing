package com.weige.smart.view;

public interface OnRefreshListener {

	/**
	 * ��������ˢ��ʱ���ص��˷���������ˢ�����ݵĲ���
	 */
	public void OnPullDownRefresh();

	/**
	 * ��ǰ�Ǽ��ظ���Ĳ���
	 */
	public void onLoadingMore();
}
