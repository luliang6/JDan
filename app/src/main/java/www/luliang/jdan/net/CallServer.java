/*
 * Copyright 2015 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package www.luliang.jdan.net;

import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.rest.RequestQueue;

/**
 * Created in Oct 23, 2015 1:00:56 PM.
 *
 * @author Yan Zhenjie.
 */
public class CallServer {

	// 构造函数
	private CallServer() {

	}


	/**
	 * 获取请求队列的单例
	 * @return
	 */
	public static RequestQueue getInstance() {
		return CallServerHolder.mInstance;

	}

	private static class CallServerHolder {
		private static final RequestQueue mInstance = NoHttp.newRequestQueue();
	}


	/**
	 * 取消这个sign标记的所有请求
	 */
	public void cancelBySign(Object sign) {
		getInstance().cancelBySign(sign);
	}

	/**
	 * 取消队列中所有请求.
	 */
	public void cancelAll() {
		getInstance().cancelAll();
	}

	/**
	 * 退出app时停止所有请求.
	 */
	public void stopAll() {
		getInstance().stop();
	}


}
