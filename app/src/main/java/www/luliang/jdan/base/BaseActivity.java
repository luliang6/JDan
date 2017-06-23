package www.luliang.jdan.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;

import www.luliang.jdan.BuildConfig;
import www.luliang.jdan.net.RequestManager;
import www.luliang.jdan.utils.logger.LogLevel;
import www.luliang.jdan.utils.logger.Logger;

/**
 * Activity的基类
 */

public abstract class BaseActivity extends AppCompatActivity implements ConstantString {
	protected Context    mContext;
	private   Request<?> mRequest;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//		mContext = this;

		// 日志开关
		if (BuildConfig.DEBUG) {
			Logger.init(getClass().getSimpleName()).setLogLevel(LogLevel.FULL).hideThreadInfo();
		} else {
			Logger.init(getClass().getSimpleName()).setLogLevel(LogLevel.NONE).hideThreadInfo();

		}


	}

	/*@Override
	public void finish() {
		super.finish();
		// 转场动画
		overridePendingTransition(R.anim.anim_none, R.anim.trans_center_2_right);
	}*/

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// 应用完全退出时取消所有网络访问请求
		RequestManager.cancelAll(this);

	}

	// 2个抽象类
	protected abstract void initView();
	protected abstract void initData();

	/**
	 * 替换碎片
	 *
	 * @param idContent
	 * @param fragment
	 */
	public void replaceFragment(int idContent, Fragment fragment) {
		// 开启一个事务
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(idContent, fragment);
		transaction.commit();
	}

	public void executeRequest(Request<?> request) {
		mRequest = request;
		RequestManager.addRequest(request, this);
	}


}
