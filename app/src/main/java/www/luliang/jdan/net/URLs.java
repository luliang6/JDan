package www.luliang.jdan.net;

/**
 * 网络请求的URLs
 */

public class URLs {

	// 首页新闻的访问接口
	public static final String URL_HOME_NEWS = "http://jandan.net/?oxwlxojflwblxbsapi=get_recent_posts&include=url," +
			"modified,tags,author,title,comment_count,custom_fields&custom_fields=thumb_c,views&dev=1&page=";

	// 首页新闻详情页的访问接口
	public static final String URL_HOME_NEWS_DETAIL = "http://i.jandan" + "" +
			".net/?oxwlxojflwblxbsapi=get_post&include=content&id=";

	// 无聊图的访问接口
	public static final String URL_BORING_PICTURE = "http://jandan.net/?oxwlxojflwblxbsapi=jandan" + "" +
			".get_pic_comments&page=";

	// 段子的访问接口
	public static final String URL_JOKER = "http://jandan.net/?oxwlxojflwblxbsapi=jandan.get_duan_comments&page=";

	// 小视频的访问接口
	public static final String URL_VIDEO = "http://jandan.net/?oxwlxojflwblxbsapi=jandan.get_video_comments&page=";


	public static String getUrlHomeNews(int page) {
		return URL_HOME_NEWS + page;
	}

	public static String getUrlHomeNewsDetail(String id) {
		return URL_HOME_NEWS_DETAIL + id;
	}

	public static String getBoringPictureRequestUrl(int page) {
		return URL_BORING_PICTURE + page;
	}
	public static String getJokerRequestUrl(int page) {
		return URL_JOKER + page;
	}
	public static String getVideoRequestUrl(int page) {
		return URL_VIDEO + page;
	}




}
