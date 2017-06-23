package www.luliang.jdan.model;

import java.util.List;

/**
 * Created by Administrator on 2016/12/8.
 */

public class VideoBean {
	public String status;
	public int            current_page;
	public int            total_comments;
	public int            page_count;
	public int            count;
	public List<Comments> comments;

	public static class Comments {
		public String       comment_ID;
		public String       comment_post_ID;
		public String       comment_author;
		public String       comment_author_email;
		public String       comment_author_url;
		public String       comment_author_IP;
		public String       comment_date;
		public String       comment_date_gmt;
		public String       comment_content;
		public String       comment_karma;
		public String       comment_approved;
		public String       comment_agent;
		public String       comment_type;
		public String       comment_parent;
		public String       user_id;
		public String       comment_subscribe;
		public String       comment_reply_ID;
		public String       vote_positive;
		public String       vote_negative;
		public String       vote_ip_pool;
		public String       text_content;
		public List<Videos> videos;

		public static class Videos {
			public String       id;
			public String       title;
			public String       description;
			public String       thumbnail;
			public String       thumbnail_v2;
			public String       category;
			public String       published;
			public String       duration;
			public int          view_count;
			public int          comment_count;
			public int          favorite_count;
			public int          up_count;
			public int          down_count;
			public int          is_panorama;
			public String       public_type;
			public String       state;
			public String       tags;
			public String       copyright_type;
			public int          paid;
			public String       link;
			public String       player;
			public User         user;
			public String       video_source;
			public List<String> streamtypes;
			public List<?>      operation_limit;
			public List<String> download_status;

			public static class User {
				public int    id;
				public String name;
				public String link;
			}
		}
	}
}
