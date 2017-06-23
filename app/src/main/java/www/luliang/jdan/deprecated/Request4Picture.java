package www.luliang.jdan.deprecated;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;

import www.luliang.jdan.model.Picture;
import www.luliang.jdan.net.JSONParser;

/**
 * 无聊图的网络请求
 */

public class Request4Picture extends Request<ArrayList<Picture>> {

	private Response.Listener<ArrayList<Picture>> mListener;

	// Constructor
	public Request4Picture(String url, Response.Listener<ArrayList<Picture>> listener, Response.ErrorListener
			errorListener) {
		super(Method.GET, url, errorListener);
		this.mListener = listener;
	}


	@Override
	protected Response<ArrayList<Picture>> parseNetworkResponse(NetworkResponse response) {

		try {
			String jsonStr = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

			jsonStr = new JSONObject(jsonStr).getJSONArray("comments").toString();

			// 将jsonArray转成arrayList
			ArrayList<Picture> pictures = (ArrayList<Picture>) JSONParser.toObject(jsonStr, new
					TypeToken<ArrayList<Picture>>() {
			}.getType());


			return Response.success(pictures, HttpHeaderParser.parseCacheHeaders(response));
		} catch (Exception e) {
			e.printStackTrace();
			return Response.error(new ParseError(e));
		}


	}

	@Override
	protected void deliverResponse(ArrayList<Picture> response) {
		mListener.onResponse(response);
	}


}
