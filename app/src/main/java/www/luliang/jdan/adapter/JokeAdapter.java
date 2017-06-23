package www.luliang.jdan.adapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;

import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import www.luliang.jdan.R;
import www.luliang.jdan.base.ConstantString;
import www.luliang.jdan.callback.LoadFinishCallBack;
import www.luliang.jdan.callback.LoadResultCallBack;
import www.luliang.jdan.model.JokerBean;
import www.luliang.jdan.net.CallServer;
import www.luliang.jdan.net.JavaBeanRequest;
import www.luliang.jdan.net.URLs;
import www.luliang.jdan.utils.NetWorkUtil;
import www.luliang.jdan.utils.ShareUtil;
import www.luliang.jdan.utils.ShowToast;
import www.luliang.jdan.utils.String2TimeUtil;


/**
 * 段子的Adapter
 */
public class JokeAdapter extends RecyclerView.Adapter<JokeAdapter.JokeViewHolder> {
	private int mPage;
	private int mLastPosition = -1;
	private List<JokerBean.Comments> mJokes;
	private Activity                 mActivity;
	private LoadResultCallBack       mLoadResultCallBack;
	private LoadFinishCallBack       mLoadFinishCallBack;

	// Constructor
	public JokeAdapter(Activity activity, LoadFinishCallBack loadFinishCallBack, LoadResultCallBack
			loadResultCallBack) {
		mActivity = activity;
		mLoadFinishCallBack = loadFinishCallBack;
		mLoadResultCallBack = loadResultCallBack;
		mJokes = new ArrayList<>();
	}


	@Override
	public JokeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_joke, parent, false);
		return new JokeViewHolder(v);
	}

	@Override
	public void onBindViewHolder(JokeViewHolder holder, int position) {

		final JokerBean.Comments joke = mJokes.get(position);
		holder.tv_content.setText(joke.text_content);
		holder.tv_author.setText(joke.comment_author);
		holder.tv_time.setText(String2TimeUtil.dateString2GoodExperienceFormat(joke.comment_date));
		holder.tv_like.setText(joke.vote_positive);
		holder.tv_comment_count.setText("");
		holder.tv_unlike.setText(joke.vote_negative);

		// 分享按钮的点击事件
		holder.img_share.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
				builder.setTitle("请选择需要的操作").setPositiveButton("分享段子", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 分享
						ShareUtil.shareText(mActivity, joke.text_content.trim());

					}
				}).setNegativeButton("复制段子", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 复制
						ClipboardManager manager = (ClipboardManager) mActivity.getSystemService(Context
								.CLIPBOARD_SERVICE);
						manager.setPrimaryClip(ClipData.newPlainText(null, joke.text_content));
						ShowToast.Short(ConstantString.COPY_SUCCESS);
					}
				}).show();


			}
		});

		holder.ll_comment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//				Intent intent = new Intent(mActivity, CommentListActivity.class);
				//				intent.putExtra("thread_key", "comment-" + joke.getComment_ID());
				//				mActivity.startActivity(intent);
			}
		});

		setAnimation(holder.card, position);

	}

	@Override
	public int getItemCount() {
		return mJokes.size();
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
			// TODO loadCache();
		}

	}

	/**
	 * 实际的加载数据的方法
	 */
	private void loadData() {

		JavaBeanRequest<JokerBean> request = new JavaBeanRequest<>(URLs.URL_JOKER, JokerBean.class);
		CallServer.getInstance().add(0, request, new OnResponseListener<JokerBean>() {
			@Override
			public void onStart(int what) {

			}

			@Override
			public void onSucceed(int what, Response<JokerBean> response) {
				mLoadFinishCallBack.loadFinish(null);
				mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
				if (mPage == 1) {
					mJokes.clear();
					// 首次正常加载之后, 清空之前的缓存
					// TODO
					//					JokeCache.getInstance(mActivity).clearAllCache();
				}

				mJokes.addAll(response.get().comments);
				notifyDataSetChanged();
			}

			@Override
			public void onFailed(int what, Response<JokerBean> response) {
				mLoadResultCallBack.onError(LoadResultCallBack.ERROR_NET, response.getException().getMessage());
				mLoadFinishCallBack.loadFinish(null);

			}

			@Override
			public void onFinish(int what) {

			}
		});


	}


	/**
	 * CardView向上插入的动画
	 * @param viewToAnimate
	 * @param position
	 */
	private void setAnimation(View viewToAnimate, int position) {
		if (position > mLastPosition) {
			Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.item_bottom_in);
			viewToAnimate.startAnimation(animation);
			mLastPosition = position;
		}
	}


	@Override
	public void onViewDetachedFromWindow(JokeViewHolder holder) {
		super.onViewDetachedFromWindow(holder);
		holder.card.clearAnimation();
	}


	static class JokeViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.tv_author)
		TextView     tv_author;
		@BindView(R.id.tv_time)
		TextView     tv_time;
		@BindView(R.id.tv_content)
		TextView     tv_content;
		@BindView(R.id.tv_like)
		TextView     tv_like;
		@BindView(R.id.tv_unlike)
		TextView     tv_unlike;
		@BindView(R.id.tv_comment_count)
		TextView     tv_comment_count;
		@BindView(R.id.img_share)
		ImageView    img_share;
		@BindView(R.id.card)
		CardView     card;
		@BindView(R.id.ll_comment)
		LinearLayout ll_comment;

		JokeViewHolder(View contentView) {
			super(contentView);
			ButterKnife.bind(this, contentView);
		}
	}

}
