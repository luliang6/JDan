package www.luliang.jdan.model;

import java.util.List;

/**
 * 段子的Bean
 */

public class JokerBean {
	public String         status;
	public int            current_page;
	public int            total_comments;
	public int            page_count;
	public int            count;
	public List<Comments> comments;

	public static class Comments {
		public String  comment_ID;
		public String  comment_post_ID;
		public String  comment_author;
		public String  comment_author_email;
		public String  comment_author_url;
		public String  comment_author_IP;
		public String  comment_date;
		public String  comment_date_gmt;
		public String  comment_content;
		public String  comment_karma;
		public String  comment_approved;
		public String  comment_agent;
		public String  comment_type;
		public String  comment_parent;
		public String  user_id;
		public String  comment_subscribe;
		public String  comment_reply_ID;
		public String  vote_positive;
		public String  vote_negative;
		public String  vote_ip_pool;
		public String  text_content;
		public List<?> videos;
	}
}
