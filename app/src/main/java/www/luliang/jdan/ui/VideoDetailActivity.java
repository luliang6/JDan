package www.luliang.jdan.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import www.luliang.jdan.R;
import www.luliang.jdan.base.BaseActivity;
import www.luliang.jdan.utils.ShareUtil;
import www.luliang.jdan.utils.ShowToast;

/**
 * 小电影详情页
 */
public class VideoDetailActivity extends BaseActivity implements View.OnClickListener {
	@BindView(R.id.webView)
	WebView     mWebView;
	@BindView(R.id.progress)
	ProgressBar progress;
	@BindView(R.id.toolbar)
	Toolbar     mToolbar;
	@BindView(R.id.imgBtn_back)
	ImageButton imgBtn_back;
	@BindView(R.id.imgBtn_forward)
	ImageButton imgBtn_forward;
	@BindView(R.id.imgBtn_control)
	ImageButton imgBtn_control;

	private boolean mIsLoadFinish = false;
	private String mUrl;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_detail);
		initView();
		initData();
	}

	@Override
	protected void initView() {
		ButterKnife.bind(this);

		mToolbar.setTitleTextColor(Color.WHITE);
		setSupportActionBar(mToolbar);
		mToolbar.setTitle(R.string.loading);
		mToolbar.setNavigationIcon(R.drawable.ic_action_back);

		imgBtn_back.setOnClickListener(this);
		imgBtn_forward.setOnClickListener(this);
		imgBtn_control.setOnClickListener(this);

		mWebView.getSettings().setJavaScriptEnabled(true);
		// Sets the chrome handler. This is an implementation of WebChromeClient for use in handling JavaScript
		// dialogs, favicons, titles, and the progress. This will replace the current handler.

		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					progress.setVisibility(View.GONE);
				} else {
					progress.setProgress(newProgress);
					progress.setVisibility(View.VISIBLE);
				}
				super.onProgressChanged(view, newProgress);
			}
		});

		mWebView.setWebViewClient(new WebViewClient() {
			// return true means the host application handles the url
			// return false means the current WebView handles the url
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				// 加载完成更新控制的图标
				imgBtn_control.setImageResource(R.drawable.ic_action_refresh);
				mIsLoadFinish = true;
				mToolbar.setTitle(view.getTitle());
			}
		});

	}

	@Override
	protected void initData() {
		mUrl = getIntent().getStringExtra("url");
		mWebView.loadUrl(mUrl);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.imgBtn_back:
				if (mWebView.canGoBack()) {
					mWebView.goBack();
				}

				break;

			case R.id.imgBtn_forward:
				if (mWebView.canGoForward()) {
					mWebView.goForward();
				}
				break;
			case R.id.imgBtn_control:
				if (mIsLoadFinish) {
					mWebView.reload();
					mIsLoadFinish = false;
				} else {
					mWebView.stopLoading();
				}

				break;
			default:
				break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_video_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			case R.id.action_more:  // 更多选项
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("请选择需要的操作").setPositiveButton("分享", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 分享
						ShareUtil.shareText(VideoDetailActivity.this, mToolbar.getTitle() + "" + mUrl);
						ShowToast.Short("分享成功!");
					}
				}).setNegativeButton("浏览器打开", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 浏览器打开
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl)));
					}
				}).show();


				break;
			default:
				break;
		}

		return super.onOptionsItemSelected(item);

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mWebView != null) {
			mWebView.onResume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mWebView != null) {
			mWebView.onPause();
		}
	}
}
