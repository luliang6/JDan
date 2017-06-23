package www.luliang.jdan.net;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import www.luliang.jdan.callback.LoadFinishCallBack;
import www.luliang.jdan.model.Comment4FreshNews;
import www.luliang.jdan.utils.logger.Logger;

/**
 * 新鲜事评论网络请求
 */
public class Request4FreshNewsCommentList extends Request<ArrayList<Comment4FreshNews>> {

	private Response.Listener<ArrayList<Comment4FreshNews>> mListener;
	private LoadFinishCallBack                              mLoadFinishCallBack;

	public Request4FreshNewsCommentList(String url, Response.Listener<ArrayList<Comment4FreshNews>> listener, Response
			.ErrorListener errorListener, LoadFinishCallBack callBack) {
		super(Method.GET, url, errorListener);
		mListener = listener;
		mLoadFinishCallBack = callBack;
	}

	@Override
	protected Response<ArrayList<Comment4FreshNews>> parseNetworkResponse(NetworkResponse response) {

		try {

			String resultStr = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

			JSONObject resultObj = new JSONObject(resultStr);

			String status = resultObj.optString("status");

			if (status.equals("ok")) {
				String commentsStr = resultObj.optJSONObject("post").optJSONArray("comments").toString();
				int    id          = resultObj.optJSONObject("post").optInt("id");

				mLoadFinishCallBack.loadFinish(Integer.toString(id));

				ArrayList<Comment4FreshNews> comment4FreshNewses = (ArrayList<Comment4FreshNews>) JSONParser.toObject
						(commentsStr, new TypeToken<ArrayList<Comment4FreshNews>>() {
				}.getType());

				// \d表示匹配数字, {7}表示数字的个数为7个, 回复形式如下:
				// <p>@<a href="#comment-3297368" rel="nofollow">sarti</a>: 谢谢，已改。</p>
				Pattern pattern = Pattern.compile("\\d{7}");


				for (Comment4FreshNews comment4FreshNews : comment4FreshNewses) {
					Matcher matcher         = pattern.matcher(comment4FreshNews.getContent());
					boolean isHas7Num       = matcher.find();
					boolean isHasCommentStr = comment4FreshNews.getContent().contains("#comment-");
					//有回复
					if (isHas7Num && isHasCommentStr || comment4FreshNews.getParentId() != 0) {
						// <p>@<a href="#comment-3297368" rel="nofollow">sarti</a>: 谢谢，已改。</p>
						ArrayList<Comment4FreshNews> tempComments = new ArrayList<>();
						int                          parentId     = getParentId(comment4FreshNews.getContent());
						comment4FreshNews.setParentId(parentId);
						getParenFreshNews(tempComments, comment4FreshNewses, parentId);
						Collections.reverse(tempComments);// 因为是递归查找父评论的, 所有这里好逆序
						comment4FreshNews.setParentComments(tempComments);// 设置父评论
						comment4FreshNews.setFloorNum(tempComments.size() + 1);// 楼层+1
						// 设置自己的评论内容, 去掉些特殊符号
						comment4FreshNews.setContent(getContentWithParent(comment4FreshNews.getContent()));
					} else {
						comment4FreshNews.setContent(getContentOnlySelf(comment4FreshNews.getContent()));
					}
				}

				Logger.d("" + comment4FreshNewses);

				return Response.success(comment4FreshNewses, HttpHeaderParser.parseCacheHeaders(response));
			} else {
				return Response.error(new ParseError(new Exception("request failed")));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.error(new ParseError(e));
		}
	}

	private void getParenFreshNews(ArrayList<Comment4FreshNews> tempComments, ArrayList<Comment4FreshNews>
			comment4FreshNewses, int parentId) {

		for (Comment4FreshNews comment4FreshNews : comment4FreshNewses) {
			if (comment4FreshNews.getId() != parentId)
				continue;
			//找到了父评论
			tempComments.add(comment4FreshNews);
			//父评论又有父评论
			if (comment4FreshNews.getParentId() != 0 && comment4FreshNews.getParentComments() != null) {
				comment4FreshNews.setContent(getContentWithParent(comment4FreshNews.getContent()));
				tempComments.addAll(comment4FreshNews.getParentComments());
				return;
			}
			//父评论没有父评论了
			comment4FreshNews.setContent(getContentOnlySelf(comment4FreshNews.getContent()));
		}
	}


	private int getParentId(String content) {
		// <p>@<a href="#comment-3297368" rel="nofollow">sarti</a>: 谢谢，已改。</p>
		try {
			int index = content.indexOf("comment-") + 8; // 找到父ID开始的索引

			int parentId = Integer.parseInt(content.substring(index, index + 7));   // 截取父ID
			return parentId;
		} catch (Exception ex) {
			return 0;
		}
	}


	private String getContentWithParent(String content) {
		if (content.contains("</a>:"))
			return getContentOnlySelf(content).split("</a>:")[1];
		return content;
	}

	private String getContentOnlySelf(String content) {
		content = content.replace("</p>", "");
		content = content.replace("<p>", "");
		content = content.replace("<br />", "");
		return content;
	}

	@Override
	protected void deliverResponse(ArrayList<Comment4FreshNews> response) {
		mListener.onResponse(response);
	}
}
