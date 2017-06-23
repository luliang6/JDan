package www.luliang.jdan.deprecated;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;

import www.luliang.jdan.model.Joke;
import www.luliang.jdan.net.JSONParser;


/**
 * 段子数据请求
 */
public class Request4Joke extends Request<ArrayList<Joke>> {

	private Response.Listener<ArrayList<Joke>> mListener;

	public Request4Joke(String url, Response.Listener<ArrayList<Joke>> listener, Response.ErrorListener
			errorListener) {
		super(Method.GET, url, errorListener);
		this.mListener = listener;
	}

	@Override
	protected Response<ArrayList<Joke>> parseNetworkResponse(NetworkResponse response) {

		try {
			String jsonStr = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			jsonStr = new JSONObject(jsonStr).getJSONArray("comments").toString();

			return Response.success((ArrayList<Joke>) JSONParser.toObject(jsonStr, new TypeToken<ArrayList<Joke>>() {
			}.getType()), HttpHeaderParser.parseCacheHeaders(response));

		} catch (Exception e) {
			e.printStackTrace();
			return Response.error(new ParseError(e));
		}
	}

	@Override
	protected void deliverResponse(ArrayList<Joke> response) {
		mListener.onResponse(response);
	}

}
