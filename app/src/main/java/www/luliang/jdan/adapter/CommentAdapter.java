package www.luliang.jdan.adapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import www.luliang.jdan.R;
import www.luliang.jdan.base.ConstantString;
import www.luliang.jdan.callback.LoadFinishCallBack;
import www.luliang.jdan.callback.LoadResultCallBack;
import www.luliang.jdan.model.Comment4FreshNews;
import www.luliang.jdan.model.Commentator;
import www.luliang.jdan.net.Request4FreshNewsCommentList;
import www.luliang.jdan.net.RequestManager;
import www.luliang.jdan.utils.ShowToast;
import www.luliang.jdan.utils.String2TimeUtil;
import www.luliang.jdan.view.floorview.FloorView;
import www.luliang.jdan.view.floorview.SubComments;
import www.luliang.jdan.view.floorview.SubFloorFactory;

/**
 * 评论的Adapter
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
	private ArrayList<Commentator>       mCommentators;
	private ArrayList<Comment4FreshNews> mComment4FreshNewses;

	private Activity           mActivity;
	private String             mThreadKey;
	private String             mThreadId;
	private LoadResultCallBack mLoadResultCallBack;
	private boolean            mIsFromFreshNews;

	// constructor
	public CommentAdapter(Activity activity, String thread_key, boolean isFromFreshNews, LoadResultCallBack
			loadResultCallBack) {
		mActivity = activity;
		this.mThreadKey = thread_key;
		this.mIsFromFreshNews = isFromFreshNews;
		mLoadResultCallBack = loadResultCallBack;
		if (isFromFreshNews) {  // 如果是来自新鲜事的评论
			mComment4FreshNewses = new ArrayList<>();
		} else {    // 否则就是普通评论
			mCommentators = new ArrayList<>();
		}
	}

	@Override
	public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		switch (viewType) { // 注意这里的switch没有break 0

			case Commentator.TYPE_HOT:  // 如果是热门评论
			case Commentator.TYPE_NEW:  // 如果是最新评论
				return new CommentViewHolder(mActivity.getLayoutInflater().inflate(R.layout.item_comment_flag, parent,
						false));    // 带一个标题, 热门评论或者最新评论

			case Commentator.TYPE_NORMAL:   // 正常评论
				return new CommentViewHolder(mActivity.getLayoutInflater().inflate(R.layout.item_comment, parent,
						false));    // 没有任何标题
			default:
				return null;
		}
	}

	@Override
	public void onBindViewHolder(CommentViewHolder holder, int position) {

		Commentator commentator = null;
		if (mIsFromFreshNews) {
			commentator = mComment4FreshNewses.get(position);
		} else {
			commentator = mCommentators.get(position);
		}

		switch (commentator.getType()) {
			case Commentator.TYPE_HOT:
				if (holder.tv_flag != null) {
					holder.tv_flag.setText("热门评论");
				}
				break;
			case Commentator.TYPE_NEW:
				if (holder.tv_flag != null) {
					holder.tv_flag.setText("最新评论");
				}
				break;
			case Commentator.TYPE_NORMAL:
				final Commentator comment = commentator;    // 取其中的一条评论

				if (holder.tv_name != null) {
					holder.tv_name.setText(commentator.getName());
				}
				if (holder.tv_content != null) {

					holder.tv_content.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
							builder.setTitle("请选择需要的操作").setPositiveButton("回复", new DialogInterface.OnClickListener
									() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// 回复
									/*Intent intent = new Intent(mActivity, PushCommentActivity.class);
									intent.putExtra("parent_id", comment.getPost_id());
									intent.putExtra("thread_id", mThreadId);
									intent.putExtra("parent_name", comment.getName());
									mActivity.startActivityForResult(intent, 0);// 返回CommentListActivity*/

								}
							}).setNegativeButton("复制到剪贴板", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// 复制
									ClipboardManager manager = (ClipboardManager) mActivity.getSystemService(Context
											.CLIPBOARD_SERVICE);
									manager.setPrimaryClip(ClipData.newPlainText(null, comment.getMessage()));
									ShowToast.Short(ConstantString.COPY_SUCCESS);
								}
							}).show();


						}
					});
				}

				if (mIsFromFreshNews) { // 来自新鲜事的评论
					Comment4FreshNews commentators4FreshNews = (Comment4FreshNews) commentator;
					holder.tv_content.setText(commentators4FreshNews.getCommentContent());
					// 展示评论人的头像, API已经废弃
					// ImageLoadProxy.displayHeadIcon(commentators4FreshNews.getAvatar_url(), holder.img_header);
				} else {
					String timeString = commentator.getCreated_at().replace("T", " ");
					timeString = timeString.substring(0, timeString.indexOf("+"));
					holder.tv_time.setText(String2TimeUtil.dateString2GoodExperienceFormat(timeString));
					holder.tv_content.setText(commentator.getMessage());
					// ImageLoadProxy.displayHeadIcon(commentator.getAvatar_url(), holder.img_header);
				}

				//有楼层, 盖楼, 说明此评论有人回复了
				if (commentator.getFloorNum() > 1) {
					SubComments subComments = null;
					if (mIsFromFreshNews) {
						subComments = new SubComments(addFloors4FreshNews((Comment4FreshNews) commentator));
					} else {
						// subComments = new SubComments(addFloors(commentator));
					}

					holder.floors_parent.setComments(subComments);
					holder.floors_parent.setFactory(new SubFloorFactory());
					holder.floors_parent.setBoundDrawer(mActivity.getResources().getDrawable(R.drawable.bg_comment));
					holder.floors_parent.init();
				} else {
					holder.floors_parent.setVisibility(View.GONE);
				}
				break;
		}

	}

	private List<Comment4FreshNews> addFloors4FreshNews(Comment4FreshNews commentator) {
		return commentator.getParentComments();
	}

	@Override
	public int getItemCount() {
		if (mIsFromFreshNews) {
			return mComment4FreshNewses.size();
		} else {
			return mCommentators.size();
		}
	}


	/**
	 * 新鲜事的评论数据加载
	 */
	public void loadData4FreshNews() {
		RequestManager.addRequest(new Request4FreshNewsCommentList(Comment4FreshNews.getUrlComments(mThreadKey), new
				Response.Listener<ArrayList<Comment4FreshNews>>() {


			@Override
			public void onResponse(ArrayList<Comment4FreshNews> response) {
				if (response.size() == 0) { // 没有评论
					mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_NONE, null);
				} else {
					mComment4FreshNewses.clear(); // 清空集合

					// 如果评论条数大于6, 就选择positive前6作为热门评论, positive是赞数
					if (response.size() > 6) {
						// 造一个热门评论的标签
						Comment4FreshNews comment4FreshNews = new Comment4FreshNews();
						comment4FreshNews.setType(Comment4FreshNews.TYPE_HOT);
						mComment4FreshNewses.add(comment4FreshNews);

						// 按投票数进行排序
						Collections.sort(response, new Comparator<Comment4FreshNews>() {
							@Override
							public int compare(Comment4FreshNews lhs, Comment4FreshNews rhs) {
								return lhs.getVotePositive() <= rhs.getVotePositive() ? 1 : -1;
							}
						});

						List<Comment4FreshNews> subComments = response.subList(0, 6);

						for (Comment4FreshNews subComment : subComments) {
							subComment.setTag(Comment4FreshNews.TAG_HOT);
						}
						mComment4FreshNewses.addAll(subComments);
					}

					Comment4FreshNews comment4FreshNews = new Comment4FreshNews();
					comment4FreshNews.setType(Comment4FreshNews.TYPE_NEW);
					mComment4FreshNewses.add(comment4FreshNews);

					Collections.sort(response); // 排序

					for (Comment4FreshNews comment4Normal : response) {
						if (comment4Normal.getTag().equals(Comment4FreshNews.TAG_NORMAL)) {
							mComment4FreshNewses.add(comment4Normal);
						}
					}

					notifyDataSetChanged();
					mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		}, new LoadFinishCallBack() {
			@Override
			public void loadFinish(Object obj) {

			}
		}


		), mActivity);


	}

	@Override
	public int getItemViewType(int position) {
		if (mIsFromFreshNews) {
			return mComment4FreshNewses.get(position).getType();
		} else {
			return mCommentators.get(position).getType();
		}
	}

	/**
	 * 非新鲜事的评论数据加载
	 */
	public void loadData() {

	}

	public String getThreadId() {
		return mThreadId;
	}

	class CommentViewHolder extends RecyclerView.ViewHolder {
		@Nullable
		@BindView(R.id.tv_name)
		TextView tv_name;

		@Nullable
		@BindView(R.id.tv_content)
		TextView tv_content;

		@Nullable
		@BindView(R.id.tv_flag)
		TextView tv_flag;

		@Nullable
		@BindView(R.id.tv_time)
		TextView tv_time;

		@Nullable
		@BindView(R.id.img_header)
		ImageView img_header;

		@Nullable
		@BindView(R.id.floors_parent)
		FloorView floors_parent;

		CommentViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
			setIsRecyclable(false);
		}
	}
}
