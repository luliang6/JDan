package www.luliang.jdan.net;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import www.luliang.jdan.BuildConfig;
import www.luliang.jdan.base.JDApplication;
import www.luliang.jdan.utils.logger.Logger;

/**
 * 网络请求管理, volley相关
 */
public class RequestManager {
	public static final int OUT_TIME       = 10000;
	public static final int TIMES_OF_RETRY = 1;

	public static RequestQueue mRequestQueue = Volley.newRequestQueue(JDApplication.getContext());

	// 构造函数
	public RequestManager() {
	}

	/**
	 * 添加一个网络请求
	 *
	 * @param request 请求对象
	 * @param tag     请求标识
	 */
	public static void addRequest(Request<?> request, Object tag) {
		if (tag != null) {
			request.setTag(tag);
		}
		//给每个请求重设超时、重试次数
		request.setRetryPolicy(new DefaultRetryPolicy(OUT_TIME, TIMES_OF_RETRY, DefaultRetryPolicy
				.DEFAULT_BACKOFF_MULT));
		mRequestQueue.add(request);

		if (BuildConfig.DEBUG) {
			 Logger.d(request.getUrl());
		}

	}

	public static void cancelAll(Object tag) {
		mRequestQueue.cancelAll(tag);
	}

}
