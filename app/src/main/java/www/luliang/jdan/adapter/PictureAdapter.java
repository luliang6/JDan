package www.luliang.jdan.adapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yolanda.nohttp.rest.OnResponseListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import www.luliang.jdan.R;
import www.luliang.jdan.base.BaseActivity;
import www.luliang.jdan.base.ConstantString;
import www.luliang.jdan.callback.LoadFinishCallBack;
import www.luliang.jdan.callback.LoadResultCallBack;
import www.luliang.jdan.model.BoringPictureBean;
import www.luliang.jdan.model.Picture;
import www.luliang.jdan.net.CallServer;
import www.luliang.jdan.net.JavaBeanRequest;
import www.luliang.jdan.net.URLs;
import www.luliang.jdan.ui.ImageDetailActivity;
import www.luliang.jdan.utils.NetWorkUtil;
import www.luliang.jdan.utils.ShareUtil;
import www.luliang.jdan.utils.ShowToast;
import www.luliang.jdan.utils.String2TimeUtil;
import www.luliang.jdan.utils.TextUtil;
import www.luliang.jdan.view.ShowMaxImageView;
import www.luliang.jdan.view.imageloader.ImageLoaderProxy;

/**
 * 无聊图的Adapter
 */
public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.PictureViewHolder> {
	private Activity                         mActivity;
	private Picture.PictureType              mType;
	private LoadFinishCallBack               mLoadFinishCallBack;
	private LoadResultCallBack               mLoadResultCallBack;
	private List<BoringPictureBean.Comments> mPictures;  // 无聊图的信息, 在loadData()中获取
	private boolean                          isWifiConnected;

	private LoadFinishCallBack mSaveFileCallBack;

	private int mLastPosition = -1;
	private int mPage;

	public PictureAdapter(Activity activity, LoadResultCallBack loadResultCallBack, LoadFinishCallBack
			loadFinishCallBack, Picture.PictureType type) {
		mActivity = activity;
		mType = type;
		mLoadFinishCallBack = loadFinishCallBack;
		mLoadResultCallBack = loadResultCallBack;
		mPictures = new ArrayList<>();
		isWifiConnected = NetWorkUtil.isWifiConnected(mActivity);


	}

	@Override
	public PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pic, parent, false);
		return new PictureViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final PictureViewHolder holder, int position) {
		final BoringPictureBean.Comments picture = mPictures.get(position);
		String                           picUrl  = picture.pics.get(0);

		// picUrl = http://ww4.sinaimg.cn/mw600/69187c2fgw1f799a49xb3j20qo0zk13i.jpg
		if (picUrl.endsWith(".gif")) {
			holder.img_gif.setVisibility(View.VISIBLE);
			// 非WIFI网络情况下, GIF图只加载缩略图, 详情页才加载真实图片
			if (!isWifiConnected)
				picUrl = picUrl.replace("mw600", "small").replace("mw1200", "small").replace("large", "small");

		} else {
			holder.img_gif.setVisibility(View.GONE);
		}

		holder.progress.setProgress(0);
		holder.progress.setVisibility(View.VISIBLE);

		ImageLoaderProxy.displayImageList(picUrl, holder.img, R.drawable.ic_loading_large, new
				SimpleImageLoadingListener() {
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				super.onLoadingComplete(imageUri, view, loadedImage);
				// 隐藏进度条
				holder.progress.setVisibility(View.GONE);


			}
		}, new ImageLoadingProgressListener() {

			@Override
			public void onProgressUpdate(String imageUrl, View view, int current, int total) {
				// 设置进度条的值
				holder.progress.setProgress((int) (current * 100f / total));
			}
		});

		// 设置图片的内容描述信息
		if (TextUtil.isNull(picture.comment_author.trim())) {
			holder.tv_content.setVisibility(View.GONE);
		} else {
			holder.tv_content.setVisibility(View.VISIBLE);
			holder.tv_content.setText(picture.comment_author.trim());
		}

		// 图片的点击事件
		holder.img.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 跳到无聊图的详情页面
				Intent intent = new Intent(mActivity, ImageDetailActivity.class);

				intent.putExtra(BaseActivity.DATA_IMAGE_AUTHOR, picture.comment_author);
				intent.putExtra(BaseActivity.DATA_IMAGE_URL, picture.pics.get(0));
				intent.putExtra(BaseActivity.DATA_IMAGE_ID, picture.comment_post_ID);
				intent.putExtra(BaseActivity.DATA_THREAD_KEY, "comment-" + picture.comment_ID); // 评论的ID

				if (picture.pics.get(0).endsWith(".gif")) {// 如果是gif图片, 则需要webView来加载
					intent.putExtra(BaseActivity.DATA_IS_NEED_WEBVIEW, true);
				}

				mActivity.startActivity(intent);
			}
		});

		holder.tv_author.setText(picture.comment_author);
		holder.tv_time.setText(String2TimeUtil.dateString2GoodExperienceFormat(picture.comment_date));
		holder.tv_like.setText(picture.vote_positive);
		holder.tv_comment_count.setText(picture.vote_negative);

		// 设置一些默认字体和颜色
		holder.tv_like.setTypeface(Typeface.DEFAULT);
		holder.tv_like.setTextColor(mActivity.getResources().getColor(R.color.secondary_text_default_material_light));

		holder.tv_support_des.setTextColor(mActivity.getResources().getColor(R.color
				.secondary_text_default_material_light));

		holder.tv_unlike.setText(picture.vote_negative);
		holder.tv_unlike.setTypeface(Typeface.DEFAULT);
		holder.tv_unlike.setTextColor(mActivity.getResources().getColor(R.color
				.secondary_text_default_material_light));
		holder.tv_un_support_des.setTextColor(mActivity.getResources().getColor(R.color
				.secondary_text_default_material_light));


		// 分享按钮的点击事件
		holder.img_share.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
				builder.setTitle("请选择需要的操作").setPositiveButton("分享", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 分享
						 ShareUtil.shareText(mActivity, picture.pics.get(0));

					}
				}).setNegativeButton("复制图片链接", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 复制
						ClipboardManager manager = (ClipboardManager) mActivity.getSystemService(Context
								.CLIPBOARD_SERVICE);
						 manager.setPrimaryClip(ClipData.newPlainText(null, picture.pics.get(0)));
						ShowToast.Short(ConstantString.COPY_SUCCESS);
					}
				}).show();


			}
		});


		// TODO 吐槽的点击事件
		/*holder.ll_comment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, CommentListActivity.class);
				intent.putExtra(BaseActivity.DATA_THREAD_KEY, "comment-" + picture.getComment_ID());
				mActivity.startActivity(intent);
			}
		});*/

		setAnimation(holder.card, position);


	}

	/**
	 * 设置底部向上滑出时的动画
	 *
	 * @param target
	 * @param position
	 */
	private void setAnimation(View target, int position) {
		if (position > mLastPosition) {
			Animation animation = AnimationUtils.loadAnimation(target.getContext(), R.anim.item_bottom_in);
			target.startAnimation(animation);
			mLastPosition = position;

		}

	}

	@Override
	public int getItemCount() {
		return mPictures.size();
	}


	// 显示的视图离开了窗体时开始回调
	@Override
	public void onViewDetachedFromWindow(PictureViewHolder holder) {
		super.onViewDetachedFromWindow(holder);
		holder.card.clearAnimation();
	}

	public void setSaveFileCallBack(LoadFinishCallBack mSaveFileCallBack) {
		this.mSaveFileCallBack = mSaveFileCallBack;
	}


	private void loadDataByNetworkType() {
		if (NetWorkUtil.isNetWorkConnected(mActivity)) {
			loadData();

		} else {
			loadCache();

		}
	}

	private void loadData() {

		// mPage = 1
		//	Logger.d(Picture.getRequestUrl(mType, mPage)) =
		// http://jandan.net/?oxwlxojflwblxbsapi=jandan.get_pic_comments&page=1

		JavaBeanRequest<BoringPictureBean> request = new JavaBeanRequest<BoringPictureBean>(URLs
				.getBoringPictureRequestUrl(mPage), BoringPictureBean.class);

		CallServer.getInstance().add(0, request, new OnResponseListener<BoringPictureBean>() {
			@Override
			public void onStart(int what) {

			}

			@Override
			public void onSucceed(int what, com.yolanda.nohttp.rest.Response<BoringPictureBean> response) {
				mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
				mLoadFinishCallBack.loadFinish(null);


				if (mPage == 1) {
					mPictures.clear();
					// TODO Cache

				}
				mPictures.addAll(response.get().comments);// 重新添加数据
				notifyDataSetChanged();
			}

			@Override
			public void onFailed(int what, com.yolanda.nohttp.rest.Response<BoringPictureBean> response) {
				// 错误的监听
				ShowToast.Short(ConstantString.LOAD_FAILED);
				mLoadFinishCallBack.loadFinish(null);
				mLoadResultCallBack.onError(LoadResultCallBack.ERROR_NET, response.getException().getMessage());
			}

			@Override
			public void onFinish(int what) {

			}
		});


		/*RequestManager.addRequest(new Request4Picture(Picture.getRequestUrl(mType, mPage), new Response
				.Listener<ArrayList<Picture>>() {


			@Override
			public void onResponse(ArrayList<Picture> response) {
				// response是装有25个无聊图(Picture)的集合
				getCommentCounts(response);

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// 错误的监听
			}
		}), mActivity);*/

	}

	/**
	 * private void getCommentCounts(final ArrayList<Picture> pictures) {
	 StringBuilder builder = new StringBuilder();
	 for (Picture pic : pictures) {
	 builder.append("comment-" + pic.getCommentID() + ",");
	 }

	 RequestManager.addRequest(new Request4CommentCounts(CommentNumber.getCommentCountsURL(builder.toString()), new
	 Response.Listener<ArrayList<CommentNumber>>() {


	@Override public void onResponse(ArrayList<CommentNumber> response) {

	mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
	mLoadFinishCallBack.loadFinish(null);

	// 添加评论信息
	for (int i = 0; i < pictures.size(); i++) {
	pictures.get(i).setComment_counts(response.get(i).getComments() + "");
	}

	if (mPage == 1) {// 如果是第1页
	PictureAdapter.this.mPictures.clear();
	//					PictureCache.getInstance(mActivity).clearAllCache();
	}
	PictureAdapter.this.mPictures.addAll(pictures);
	notifyDataSetChanged();
	// TODO
	//加载完毕后缓存
	//				PictureCache.getInstance(mActivity).addResultCache(JSONParser.toString(pictures),
	// mPage);


	}
	}, new Response.ErrorListener() {
	@Override public void onErrorResponse(VolleyError error) {
	ShowToast.Short(ConstantString.LOAD_FAILED);
	mLoadFinishCallBack.loadFinish(null);
	mLoadResultCallBack.onError(LoadResultCallBack.ERROR_NET, error.getMessage());
	}
	}), mActivity);


	 }*/

	/**
	 * 通过缓存加载
	 */
	private void loadCache() {
		mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
		mLoadFinishCallBack.loadFinish(null);
		// TODO


	}

	public void loadFirst() {
		mPage = 1;
		loadDataByNetworkType();
	}

	public void loadNextPage() {
		mPage++;
		loadDataByNetworkType();
	}


	public class PictureViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.tv_author)
		TextView tv_author;
		@BindView(R.id.tv_time)
		TextView tv_time;
		@BindView(R.id.tv_content)
		TextView tv_content;
		@BindView(R.id.tv_like)
		TextView tv_like;
		@BindView(R.id.tv_unlike)
		TextView tv_unlike;
		@BindView(R.id.tv_comment_count)
		TextView tv_comment_count;
		@BindView(R.id.tv_unsupport_des)
		TextView tv_un_support_des;
		@BindView(R.id.tv_support_des)
		TextView tv_support_des;

		@BindView(R.id.img_share)
		ImageView        img_share;
		@BindView(R.id.img_gif)
		ImageView        img_gif;
		@BindView(R.id.img)
		ShowMaxImageView img;

		@BindView(R.id.ll_comment)
		LinearLayout ll_comment;
		@BindView(R.id.progress)
		ProgressBar  progress;
		@BindView(R.id.card)
		CardView     card;


		public PictureViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
