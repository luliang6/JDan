package www.luliang.jdan.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.android.volley.Request;

import www.luliang.jdan.BuildConfig;
import www.luliang.jdan.net.CallServer;
import www.luliang.jdan.net.RequestManager;
import www.luliang.jdan.utils.logger.LogLevel;
import www.luliang.jdan.utils.logger.Logger;
import www.luliang.jdan.view.imageloader.ImageLoaderProxy;

/**
 * Fragment的基类
 */

public class BaseFragment extends Fragment implements ConstantString {
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 日志开关
		if (BuildConfig.DEBUG) {
			Logger.init(getClass().getSimpleName()).setLogLevel(LogLevel.FULL).hideThreadInfo();
		} else {
			Logger.init(getClass().getSimpleName()).setLogLevel(LogLevel.NONE).hideThreadInfo();

		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// 应用完全退出时取消所有网络访问请求
		RequestManager.cancelAll(this);

		ImageLoaderProxy.getImageLoader().clearMemoryCache();// fragment销毁时清除内存缓存

		// NoHttp取消所有请求
		CallServer.getInstance().cancelAll();


	}

	protected void executeRequest(Request request) {
		RequestManager.addRequest(request, this);

	}


}
