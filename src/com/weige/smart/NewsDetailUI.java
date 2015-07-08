package com.weige.smart;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;

/**
 * @author Administrator 新闻详情页面
 */
public class NewsDetailUI extends Activity {

	@ViewInject(R.id.wv_news_detail)
	private WebView mWebView;

	@ViewInject(R.id.pb_news_detail)
	private ProgressBar mProgressBar;

	private int currentTextSizePosition = 2;
	
	private WebSettings settings;

	private String url;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.news_detail);

		ViewUtils.inject(this);

		init();
	}

	private void init() {
		Intent intent = getIntent();
		url = intent.getStringExtra("url");

		settings = mWebView.getSettings(); // 获得webview的参数配置类
		settings.setJavaScriptEnabled(true);
		mWebView.loadUrl(url); // 让webview加载url

		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				mProgressBar.setVisibility(View.GONE);
			}

		});
	}

	@OnClick(R.id.ib_news_detail_back)
	public void back(View v) {
		finish();
	}

	@OnClick(R.id.ib_news_detail_textsize)
	public void changeTextSize(View v) {
		showSelectTextSizeDialog();
	}

	/**
	 * 弹出选择字体对话框
	 */
	private void showSelectTextSizeDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("请选择字体大小");
		
		String[] item = {"超大号字体","大号字体","正常字体","小号字体","超小号字体"};
		builder.setSingleChoiceItems(item, currentTextSizePosition, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				currentTextSizePosition = which;
				// 把当前对话框关闭
				dialog.dismiss();
				
				// 修改字体
				switchTextSize(currentTextSizePosition);
			}
		});
		final AlertDialog dialog = builder.show();
	}

	protected void switchTextSize(int textsize) {
		switch (textsize) {
		case 0:
			settings.setTextSize(TextSize.LARGEST);
			break;
		case 1:
			settings.setTextSize(TextSize.LARGER);
			break;
		case 2:
			settings.setTextSize(TextSize.NORMAL);
			break;
		case 3:
			settings.setTextSize(TextSize.SMALLER);
			break;
		case 4:
			settings.setTextSize(TextSize.SMALLEST);
			break;

		default:
			break;
		}
	}

	@OnClick(R.id.ib_news_detail_share)
	public void share(View v) {
		showShare();
	}
	
	private void showShare() {
		 ShareSDK.initSDK(this);
		 OnekeyShare oks = new OnekeyShare();
		 //关闭sso授权
		 oks.disableSSOWhenAuthorize(); 

		// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
		 //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
//		 // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//		 oks.setTitle(getString(R.string.share));
//		 // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//		 oks.setTitleUrl("http://sharesdk.cn");
		 // text是分享文本，所有平台都需要这个字段
		 oks.setText("赞！" + url);
		 // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		 oks.setImagePath("/mnt/sdcard/sina/weibo/weibo/img-212e5b576a35ba2b34fc92d64718583a.jpg");//确保SDcard下面存在此张图片
//		 // url仅在微信（包括好友和朋友圈）中使用
//		 oks.setUrl("http://sharesdk.cn");
//		 // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//		 oks.setComment("我是测试评论文本");
//		 // site是分享此内容的网站名称，仅在QQ空间使用
//		 oks.setSite(getString(R.string.app_name));
//		 // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//		 oks.setSiteUrl("http://sharesdk.cn");

		// 启动分享GUI
		 oks.show(this);
		 }

}
