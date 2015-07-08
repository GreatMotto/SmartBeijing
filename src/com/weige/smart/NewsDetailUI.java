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
 * @author Administrator ��������ҳ��
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

		settings = mWebView.getSettings(); // ���webview�Ĳ���������
		settings.setJavaScriptEnabled(true);
		mWebView.loadUrl(url); // ��webview����url

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
	 * ����ѡ������Ի���
	 */
	private void showSelectTextSizeDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("��ѡ�������С");
		
		String[] item = {"���������","�������","��������","С������","��С������"};
		builder.setSingleChoiceItems(item, currentTextSizePosition, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				currentTextSizePosition = which;
				// �ѵ�ǰ�Ի���ر�
				dialog.dismiss();
				
				// �޸�����
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
		 //�ر�sso��Ȩ
		 oks.disableSSOWhenAuthorize(); 

		// ����ʱNotification��ͼ�������  2.5.9�Ժ�İ汾�����ô˷���
		 //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
//		 // title���⣬ӡ��ʼǡ����䡢��Ϣ��΢�š���������QQ�ռ�ʹ��
//		 oks.setTitle(getString(R.string.share));
//		 // titleUrl�Ǳ�����������ӣ�������������QQ�ռ�ʹ��
//		 oks.setTitleUrl("http://sharesdk.cn");
		 // text�Ƿ����ı�������ƽ̨����Ҫ����ֶ�
		 oks.setText("�ޣ�" + url);
		 // imagePath��ͼƬ�ı���·����Linked-In�����ƽ̨��֧�ִ˲���
		 oks.setImagePath("/mnt/sdcard/sina/weibo/weibo/img-212e5b576a35ba2b34fc92d64718583a.jpg");//ȷ��SDcard������ڴ���ͼƬ
//		 // url����΢�ţ��������Ѻ�����Ȧ����ʹ��
//		 oks.setUrl("http://sharesdk.cn");
//		 // comment���Ҷ�������������ۣ�������������QQ�ռ�ʹ��
//		 oks.setComment("���ǲ��������ı�");
//		 // site�Ƿ�������ݵ���վ���ƣ�����QQ�ռ�ʹ��
//		 oks.setSite(getString(R.string.app_name));
//		 // siteUrl�Ƿ�������ݵ���վ��ַ������QQ�ռ�ʹ��
//		 oks.setSiteUrl("http://sharesdk.cn");

		// ��������GUI
		 oks.show(this);
		 }

}
