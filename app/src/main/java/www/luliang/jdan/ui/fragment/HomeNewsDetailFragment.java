package www.luliang.jdan.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.victor.loading.rotate.RotateLoading;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import www.luliang.jdan.R;
import www.luliang.jdan.base.BaseFragment;
import www.luliang.jdan.model.HomeNewsBean;
import www.luliang.jdan.net.CallServer;
import www.luliang.jdan.net.URLs;
import www.luliang.jdan.ui.CommentListActivity;
import www.luliang.jdan.utils.ShareUtil;
import www.luliang.jdan.utils.String2TimeUtil;

import static www.luliang.jdan.R.id.webView;


/**
 * 新鲜事详情页, 布局是一个ViewPager, adapter采用的是FragmentViewPager
 */
public class HomeNewsDetailFragment extends BaseFragment {
	@BindView(webView)
	WebView       mWebView;
	@BindView(R.id.loading)
	RotateLoading mLoading;

	private HomeNewsBean.Posts mNews;


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
			savedInstanceState) {
		// 实际上就是一个WebView
		View view = inflater.inflate(R.layout.fragment_home_news_detail, container, false);
		ButterKnife.bind(this, view);
		return view;

	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);

		// 从Bundle中取出一条新闻
		mNews = (HomeNewsBean.Posts) getArguments().getSerializable(DATA_HOME_NEWS);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebChromeClient(new WebChromeClient() {

			// webView加载进度的回调
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				if (newProgress > 80) { // 加载完成80%则进度条消失
					mLoading.stop();
				}
			}
		});

		// 直接请求String
		final Request<String> request = NoHttp.createStringRequest(URLs.getUrlHomeNewsDetail(mNews.id + ""),
				RequestMethod.GET);
		CallServer.getInstance().add(0, request, new OnResponseListener<String>() {
			@Override
			public void onStart(int what) {

			}


			@Override
			public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
				// 这里自己把需要的HTML代码组装好, 然后通过本地的webView组件加载
				// 因为response中就是需要的内容信息, 没有提供一个URL去给webView加载
				// 所以采用手动的方法

				try {
					JSONObject jsonObject    = new JSONObject(response.get());
					JSONObject contentObject = jsonObject.optJSONObject("post");

					mWebView.loadDataWithBaseURL("", getHtml(mNews, contentObject.optString("content")), "text/html",
							"utf-8", "");

				} catch (JSONException e) {
					e.printStackTrace();
				}


			}

			@Override
			public void onFailed(int what, com.yolanda.nohttp.rest.Response<String> response) {

			}

			@Override
			public void onFinish(int what) {

			}
		});

		mLoading.start();

		// 10s后如果进度条还在显示, 则取消
		mLoading.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (mLoading.isShown()) {
					mLoading.stop();
				}
			}
		}, 10 * 1000);
	}

	/**
	 * 获取json中的Html代码
	 *
	 * @param news
	 * @param content
	 * @return
	 */
	private static String getHtml(HomeNewsBean.Posts news, String content) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html>");
		sb.append("<html dir=\"ltr\" lang=\"zh\">");
		sb.append("<head>");
		sb.append("<meta name=\"viewport\" content=\"width=100%; initial-scale=1.0; maximum-scale=1.0; " +
				"user-scalable=0;\" />");
		sb.append("<link rel=\"stylesheet\" href='file:///android_asset/style.css' type=\"text/css\" " +
				"media=\"screen\"" +
				" " +
				"" + "/>");
		sb.append("</head>");
		sb.append("<body style=\"padding:0px 8px 8px 8px;\">");
		sb.append("<div id=\"pagewrapper\">");
		sb.append("<div id=\"mainwrapper\" class=\"clearfix\">");
		sb.append("<div id=\"maincontent\">");
		sb.append("<div class=\"post\">");
		sb.append("<div class=\"posthit\">");
		sb.append("<div class=\"postinfo\">");
		sb.append("<h2 class=\"thetitle\">");
		sb.append("<a>");
		sb.append(news.title);
		sb.append("</a>");
		sb.append("</h2>");
		sb.append(news.author.name + " @ " + String2TimeUtil.dateString2GoodExperienceFormat(news.modified));
		sb.append("</div>");
		sb.append("<div class=\"entry\">");
		sb.append(content);
		sb.append("</div>");
		sb.append("</div>");
		sb.append("</div>");
		sb.append("</div>");
		sb.append("</div>");
		sb.append("</div>");
		sb.append("</body>");
		sb.append("</html>");

		return sb.toString();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mWebView != null) {
			mWebView.onResume();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mWebView != null) {
			mWebView.onPause();
		}

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_fresh_news_detail, menu);
		super.onCreateOptionsMenu(menu, inflater);// 注释掉有什么用???

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_comment:   // 去评论
				Intent intent = new Intent(getActivity(), CommentListActivity.class);
				intent.putExtra(DATA_THREAD_ID, mNews.id);
				intent.putExtra(DATA_IS_FROM_FRESH_NEWS, true);
				startActivity(intent);
				return true;
			case R.id.action_share:     // 去分享
				ShareUtil.shareText(getActivity(), mNews.title + " " + mNews.url);
				return true;
		}
		return super.onOptionsItemSelected(item);

	}

	public static HomeNewsDetailFragment getInstance(HomeNewsBean.Posts homeNews) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(DATA_HOME_NEWS, homeNews); // 在onActivityCreated()中取出
		HomeNewsDetailFragment homeNewsDetailFragment = new HomeNewsDetailFragment();
		// new了一个HomeNewsDetailFragment的对象, 势必会走onActivityCreated()方法, 从而取出我们需要的DATA_HOME_NEWS信息

		homeNewsDetailFragment.setArguments(bundle);
		return homeNewsDetailFragment;
	}
}
