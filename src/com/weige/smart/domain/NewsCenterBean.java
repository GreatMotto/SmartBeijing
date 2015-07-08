package com.weige.smart.domain;

import java.util.List;

public class NewsCenterBean {

	public List<MenuItem> data;
	public List<String> extend;
	public int retcode;
	
	@Override
	public String toString() {
		return "NewsCenterBean [data=" + data + ", extend=" + extend
				+ ", retcode=" + retcode + "]";
	}

	public class MenuItem {
		public List<NewsItem> children;
		public int id;
		public String title;
		public int type;
		public String url;
		public String url1;
		public String dayurl;
		public String excurl;
		public String weekurl;
		@Override
		public String toString() {
			return "MemuItem [children=" + children + ", id=" + id + ", title="
					+ title + ", type=" + type + ", url=" + url + ", url1="
					+ url1 + ", dayurl=" + dayurl + ", excurl=" + excurl
					+ ", weekurl=" + weekurl + "]";
		}
	}
	
	public class NewsItem {
		public int id;
		public String title;
		public int type;
		public String url;
		@Override
		public String toString() {
			return "NewsItem [id=" + id + ", title=" + title + ", type=" + type
					+ ", url=" + url + "]";
		}
	}
}
