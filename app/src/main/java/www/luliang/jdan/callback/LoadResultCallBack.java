package www.luliang.jdan.callback;

/**
 * 加载结果回调接口
 */
public interface LoadResultCallBack {
	int SUCCESS_OK   = 1001;
	int SUCCESS_NONE = 1002;
	int ERROR_NET    = 1003;

	void onSuccess(int result, Object object);

	void onError(int code, String msg);
}
