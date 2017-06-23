package www.luliang.jdan.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/12/6.
 */
public class HomeNewsBean {


	public String      status;
	public int         count;
	public int         count_total;
	public int         pages;
	public List<Posts> posts;

	public static class Posts implements Serializable {
		public int                id;
		public String             type;
		public String             slug;
		public String             url;
		public String             status;
		public String             title;
		public String             title_plain;
		public String             content;
		public String             excerpt;
		public String             date;
		public String             modified;
		public Author             author;
		public int                comment_count;
		public String             comment_status;
		public List<Categories>   categories;
		public List<Tags>         tags;
		public List<Comments>     comments;
		public List<CommentsRank> comments_rank;
		public List<?>            attachments;

		public static class Author implements Serializable {
			public int    id;
			public String slug;
			public String name;
			public String first_name;
			public String last_name;
			public String nickname;
			public String url;
			public String description;
		}

		public static class Categories implements Serializable {
			public int    id;
			public String slug;
			public String title;
			public String description;
			public int    parent;
			public int    post_count;
		}

		public static class Tags implements Serializable {
			public int    id;
			public String slug;
			public String title;
			public String description;
			public int    post_count;
		}

		public static class Comments implements Serializable {
			public int    id;
			public String name;
			public String url;
			public String date;
			public String content;
			public int    parent;
			public int    vote_positive;
			public int    vote_negative;
			public int    index;
		}

		public static class CommentsRank implements Serializable {
			public int    id;
			public String name;
			public String url;
			public String date;
			public String content;
			public int    parent;
			public int    vote_positive;
			public int    vote_negative;
			public int    index;
		}
	}
}
