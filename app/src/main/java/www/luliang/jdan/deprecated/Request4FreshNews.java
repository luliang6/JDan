package www.luliang.jdan.deprecated;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import www.luliang.jdan.utils.logger.Logger;

/**
 * 新鲜事的网络请求
 */

public class Request4FreshNews extends Request<ArrayList<FreshNews>> {
	private Response.Listener<ArrayList<FreshNews>> mListener;

	public Request4FreshNews(String url, Response.Listener<ArrayList<FreshNews>> listener, Response.ErrorListener
			errorListener) {
		super(Method.GET, url, errorListener);
		this.mListener = listener;
	}


	@Override
	protected Response<ArrayList<FreshNews>> parseNetworkResponse(NetworkResponse response) {
		try {
			String resultStr = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			// HttpHeaderParser.parseCharset(response.headers) = UTF-8
			JSONObject resultObj  = new JSONObject(resultStr);
			JSONArray  postsArray = resultObj.optJSONArray("posts");
			Logger.d(postsArray.toString());


			return Response.success(FreshNews.parse(postsArray), HttpHeaderParser.parseCacheHeaders(response));
		} catch (Exception e) {
			e.printStackTrace();

			return Response.error(new ParseError(e));
		}
	}

	@Override
	protected void deliverResponse(ArrayList<FreshNews> response) {
		mListener.onResponse(response);
	}


}
