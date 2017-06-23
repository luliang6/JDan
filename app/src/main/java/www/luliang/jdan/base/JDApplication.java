package www.luliang.jdan.base;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.SPCookieStore;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.yanzhenjie.nohttp.OkHttpNetworkExecutor;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.cache.DBCacheStore;
import com.yolanda.nohttp.cookie.DBCookieStore;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;
import www.luliang.jdan.R;
import www.luliang.jdan.utils.StrictModeUtil;
import www.luliang.jdan.view.imageloader.ImageLoaderProxy;

/**
 *
 */

public class JDApplication extends Application {
	// 对话框的颜色
	public static int COLOR_OF_DIALOG         = R.color.primary;
	// 对话框内容的颜色
	public static int COLOR_OF_DIALOG_CONTENT = Color.WHITE;

	private static Context mContext;


	@Override
	public void onCreate() {
		// 开启严格模式
		StrictModeUtil.init();
		super.onCreate();


		mContext = this;


		ImageLoaderProxy.initImageLoader(this);

		Logger.setDebug(true);// 开启NoHttp的调试模式, 配置后可看到请求过程、日志和错误信息。
		Logger.setTag("NoHttp");// 设置NoHttp打印Log的tag。

		// 一般情况下你只需要这样初始化：
		NoHttp.initialize(this);

		// 如果你需要自定义配置:
		NoHttp.initialize(this, new NoHttp.Config()
				// 设置全局连接超时时间，单位毫秒，默认10s。
				.setConnectTimeout(30 * 1000)
				// 设置全局服务器响应超时时间，单位毫秒，默认10s。
				.setReadTimeout(30 * 1000)
				// 配置缓存，默认保存数据库DBCacheStore，保存到SD卡使用DiskCacheStore。
				.setCacheStore(new DBCacheStore(this).setEnable(true) // 如果不使用缓存，设置false禁用。
				)
				// 配置Cookie，默认保存数据库DBCookieStore，开发者可以自己实现。
				.setCookieStore(new DBCookieStore(this).setEnable(false) // 如果不维护cookie，设置false禁用。
				)
				// 配置网络层，默认使用URLConnection，如果想用OkHttp：OkHttpNetworkExecutor。
				.setNetworkExecutor(new OkHttpNetworkExecutor()));

		// 如果你需要用OkHttp，请依赖下面的项目，version表示版本号：
		// compile 'com.yanzhenjie.nohttp:okhttp:version'

		// 初始化OkGo
		initOkGo();
	}

	public static Context getContext() {
		return mContext;
	}

	private void initOkGo() {
		// 1. 构建OkHttpClient.Builder
		OkHttpClient.Builder builder = new OkHttpClient.Builder();

		// 2. log相关
		HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
		loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);        //log打印级别，决定了log显示的详细程度
		loggingInterceptor.setColorLevel(Level.INFO);                               //log颜色级别，决定了log在控制台显示的颜色
		builder.addInterceptor(loggingInterceptor);                                 //添加OkGo默认debug日志

		// 第三方的开源库，使用通知显示当前请求的log，不过在做文件下载的时候，这个库好像有问题，对文件判断不准确
		// builder.addInterceptor(new ChuckInterceptor(this));

		// 3. 配置超时时间
		// 全局的读取超时时间
		builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
		// 全局的写入超时时间
		builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
		// 全局的连接超时时间
		builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);

		// 4. 配置Cookie, 以下几种任选其一就行
		// 使用sp保持cookie，如果cookie不过期，则一直有效
		builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));
		// 使用数据库保持cookie，如果cookie不过期，则一直有效
		// builder.cookieJar(new CookieJarImpl(new DBCookieStore(this)));
		// 使用内存保持cookie，app退出后，cookie消失
		// builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));


		// 5. Https配置, 以下几种方案根据需要自己设置

		//方法一：信任所有证书,不安全有风险
		HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory();

		//方法二：自定义信任规则，校验服务端证书
		// HttpsUtils.SSLParams sslParams2 = HttpsUtils.getSslSocketFactory(new SafeTrustManager());

		//方法三：使用预埋证书，校验服务端证书（自签名证书）
		//HttpsUtils.SSLParams sslParams3 = HttpsUtils.getSslSocketFactory(getAssets().open("srca.cer"));
		//方法四：使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
		//HttpsUtils.SSLParams sslParams4 = HttpsUtils.getSslSocketFactory(getAssets().open("xxx.bks"), "123456", getAssets().open("yyy.cer"));

		builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);

		//配置https的域名匹配规则，详细看demo的初始化介绍，不需要就不要加入，使用不当会导致https握手失败
		// builder.hostnameVerifier(new SafeHostnameVerifier());

		// 6. 配置OkGo

		/*
		//---------这里给出的是示例代码,告诉你可以这么传,实际使用的时候,根据需要传,不需要就不传-------------//
		HttpHeaders headers = new HttpHeaders();
		headers.put("commonHeaderKey1", "commonHeaderValue1");    //header不支持中文，不允许有特殊字符
		headers.put("commonHeaderKey2", "commonHeaderValue2");
		HttpParams params = new HttpParams();
		params.put("commonParamsKey1", "commonParamsValue1");     //param支持中文,直接传,不要自己编码
		params.put("commonParamsKey2", "这里支持中文参数");
		//-------------------------------------------------------------------------------------//
		*/


		OkGo.getInstance().init(this)                           //必须调用初始化
				.setOkHttpClient(builder.build())               //必须设置OkHttpClient
				.setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
				.setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
				.setRetryCount(3);                              //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
				//.addCommonHeaders(headers)                      //全局公共头
				//.addCommonParams(params);                       //全局公共参数

	}
}
