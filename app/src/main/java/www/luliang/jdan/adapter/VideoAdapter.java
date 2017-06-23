package www.luliang.jdan.adapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yolanda.nohttp.rest.OnResponseListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import www.luliang.jdan.R;
import www.luliang.jdan.base.ConstantString;
import www.luliang.jdan.callback.LoadFinishCallBack;
import www.luliang.jdan.callback.LoadResultCallBack;
import www.luliang.jdan.model.CommentNumber;
import www.luliang.jdan.model.Video;
import www.luliang.jdan.model.VideoBean;
import www.luliang.jdan.net.CallServer;
import www.luliang.jdan.net.JavaBeanRequest;
import www.luliang.jdan.net.Request4CommentCounts;
import www.luliang.jdan.net.Request4Video;
import www.luliang.jdan.net.RequestManager;
import www.luliang.jdan.net.URLs;
import www.luliang.jdan.ui.VideoDetailActivity;
import www.luliang.jdan.utils.NetWorkUtil;
import www.luliang.jdan.utils.ShareUtil;
import www.luliang.jdan.utils.ShowToast;
import www.luliang.jdan.view.imageloader.ImageLoaderProxy;


public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

	private int                           mPage;
	private ArrayList<VideoBean.Comments> mVideos;
	private int lastPosition = -1;
	private Activity           mActivity;
	private LoadResultCallBack mLoadResultCallBack;
	private LoadFinishCallBack mLoadFinishCallBack;

	public VideoAdapter(Activity activity, LoadResultCallBack loadResultCallBack, LoadFinishCallBack
			loadFinishCallBack) {

		mActivity = activity;
		mLoadFinishCallBack = loadFinishCallBack;
		mLoadResultCallBack = loadResultCallBack;
		mVideos = new ArrayList<>();
	}

	private void setAnimation(View viewToAnimate, int position) {
		if (position > lastPosition) {
			Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.item_bottom_in);
			viewToAnimate.startAnimation(animation);
			lastPosition = position;
		}
	}

	@Override
	public void onViewDetachedFromWindow(VideoViewHolder holder) {
		super.onViewDetachedFromWindow(holder);
		holder.card.clearAnimation();
	}

	@Override
	public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
		return new VideoViewHolder(v);
	}

	@Override
	public void onBindViewHolder(final VideoViewHolder holder, final int position) {

		final VideoBean.Comments video = mVideos.get(position);

		holder.tv_title.setText(video.);
		holder.tv_comment_count.setText(video.getComment_count());

		ImageLoaderProxy.displayImageWithLoadingPicture(video.getImgUrl(), holder.img, R.drawable.ic_loading_small);

		//用于恢复默认的文字
		holder.tv_like.setText(video.getVote_positive());
		holder.tv_like.setTypeface(Typeface.DEFAULT);
		holder.tv_like.setTextColor(mActivity.getResources().getColor(R.color.secondary_text_default_material_light));
		holder.tv_support_des.setTextColor(mActivity.getResources().getColor(R.color
				.secondary_text_default_material_light));

		holder.tv_unlike.setText(video.getVote_negative());
		holder.tv_unlike.setTypeface(Typeface.DEFAULT);
		holder.tv_unlike.setTextColor(mActivity.getResources().getColor(R.color
				.secondary_text_default_material_light));
		holder.tv_un_support_des.setTextColor(mActivity.getResources().getColor(R.color
				.secondary_text_default_material_light));

		/*holder.ll_comment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, CommentListActivity.class);
				intent.putExtra(BaseActivity.DATA_THREAD_KEY, "comment-" + video.getComment_ID());
				mActivity.startActivity(intent);
			}
		});*/

		// 分享按钮的点击事件
		holder.img_share.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
				builder.setTitle("请选择需要的操作").setPositiveButton("分享", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 分享
						ShareUtil.shareText(mActivity, video.getTitle().trim() + " " + video.getUrl());

					}
				}).setNegativeButton("复制链接", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 复制
						ClipboardManager manager = (ClipboardManager) mActivity.getSystemService(Context
								.CLIPBOARD_SERVICE);
						manager.setPrimaryClip(ClipData.newPlainText(null, video.getUrl()));
						ShowToast.Short(ConstantString.COPY_SUCCESS);
					}
				}).show();


			}
		});

		holder.card.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, VideoDetailActivity.class);
				intent.putExtra("url", video.getUrl());
				mActivity.startActivity(intent);
			}
		});

		setAnimation(holder.card, position);

	}

	@Override
	public int getItemCount() {
		return mVideos.size();
	}

	public void loadFirst() {
		mPage = 1;
		loadDataByNetworkType();
	}

	public void loadNextPage() {
		mPage++;
		loadDataByNetworkType();
	}

	private void loadDataByNetworkType() {
		if (NetWorkUtil.isNetWorkConnected(mActivity)) {
			loadData();
		} else {
			loadCache();
		}
	}

	private void loadData() {
		JavaBeanRequest<VideoBean> request = new JavaBeanRequest<>(URLs.getVideoRequestUrl(mPage), VideoBean.class);
		CallServer.getInstance().add(0, request, new OnResponseListener<VideoBean>() {
			@Override
			public void onStart(int what) {

			}

			@Override
			public void onSucceed(int what, com.yolanda.nohttp.rest.Response<VideoBean> response) {
				mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
				mLoadFinishCallBack.loadFinish(null);

				if (mPage == 1) {
					mVideos.clear();
				}
				mVideos.addAll(response.get().comments);// 重新添加数据
				notifyDataSetChanged();
				// 添加缓存到greenDao
			}

			@Override
			public void onFailed(int what, com.yolanda.nohttp.rest.Response<VideoBean> response) {
				mLoadFinishCallBack.loadFinish(null);
				mLoadResultCallBack.onError(LoadResultCallBack.ERROR_NET, null);
			}

			@Override
			public void onFinish(int what) {

			}
		});


		RequestManager.addRequest(new Request4Video(Video.getUrlVideos(mPage), new Response.Listener<ArrayList<Video>>
				() {
			@Override
			public void onResponse(ArrayList<Video> response) {
				getCommentCounts(response);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				mLoadFinishCallBack.loadFinish(null);
			}
		}), mActivity);
	}

	private void loadCache() {


		/*mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
		mLoadFinishCallBack.loadFinish(null);
		VideoCache videoCacheUtil = VideoCache.getInstance(mActivity);
		if (mPage == 1) {
			mVideos.clear();
			ShowToast.Short(ConstantString.LOAD_NO_NETWORK);
		}
		mVideos.addAll(videoCacheUtil.getCacheByPage(mPage));
		notifyDataSetChanged();*/

	}

	//获取评论数量, API已经失效!!!
	private void getCommentCounts(final ArrayList<Video> videos) {

		StringBuilder builder = new StringBuilder();
		for (Video video : videos) {
			builder.append("comment-" + video.getComment_ID() + ",");
		}

		RequestManager.addRequest(new Request4CommentCounts(CommentNumber.getCommentCountsURL(builder.toString()), new
				Response.Listener<ArrayList<CommentNumber>>() {

			@Override
			public void onResponse(ArrayList<CommentNumber> response) {

				mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
				mLoadFinishCallBack.loadFinish(null);

				for (int i = 0; i < videos.size(); i++) {
					videos.get(i).setComment_count(response.get(i).getComments() + "");
				}

				if (mPage == 1) {
					mVideos.clear();
					//					VideoCache.getInstance(mActivity).clearAllCache();
				}

				mVideos.addAll(videos);
				notifyDataSetChanged();
				//				VideoCache.getInstance(mActivity).addResultCache(JSONParser.toString(videos), mPage);
				//防止加载不到一页的情况
				if (mVideos.size() < 10) {
					loadNextPage();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		}), mActivity);

	}


	static class VideoViewHolder extends RecyclerView.ViewHolder {

		@BindView(R.id.img)
		ImageView      img;
		@BindView(R.id.tv_title)
		TextView       tv_title;
		@BindView(R.id.tv_support_des)
		TextView       tv_support_des;
		@BindView(R.id.tv_like)
		TextView       tv_like;
		@BindView(R.id.ll_support)
		LinearLayout   ll_support;
		@BindView(R.id.tv_unsupport_des)
		TextView       tv_un_support_des;
		@BindView(R.id.tv_unlike)
		TextView       tv_unlike;
		@BindView(R.id.ll_unsupport)
		LinearLayout   ll_unsupport;
		@BindView(R.id.tv_comment_count)
		TextView       tv_comment_count;
		@BindView(R.id.ll_comment)
		LinearLayout   ll_comment;
		@BindView(R.id.img_share)
		ImageButton    img_share;
		@BindView(R.id.bottom)
		RelativeLayout bottom;
		@BindView(R.id.card)
		CardView       card;

		VideoViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}
	}
}

