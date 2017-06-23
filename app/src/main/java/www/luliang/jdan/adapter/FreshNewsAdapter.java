package www.luliang.jdan.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
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

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import www.luliang.jdan.R;
import www.luliang.jdan.base.ConstantString;
import www.luliang.jdan.callback.LoadFinishCallBack;
import www.luliang.jdan.callback.LoadResultCallBack;
import www.luliang.jdan.model.HomeNewsBean;
import www.luliang.jdan.net.CallServer;
import www.luliang.jdan.net.JavaBeanRequest;
import www.luliang.jdan.net.URLs;
import www.luliang.jdan.ui.HomeNewsDetailActivity;
import www.luliang.jdan.utils.NetWorkUtil;
import www.luliang.jdan.utils.ShareUtil;
import www.luliang.jdan.utils.ShowToast;
import www.luliang.jdan.view.imageloader.ImageLoaderProxy;


/**
 * 新鲜事的Adapter
 */

public class FreshNewsAdapter extends RecyclerView.Adapter<FreshNewsAdapter.MyViewHolder> {
	private int mPage;
	private int mLastPosition = -1;
	private boolean  mIsLargeMode;
	private Activity mActivity;

	private final DisplayImageOptions mOptions;

	private ArrayList<HomeNewsBean.Posts> mNewses;
	private LoadFinishCallBack            mLoadFinishCallBack;

	private LoadResultCallBack mLoadResultCallBack;

	/**
	 * @param activity
	 * @param loadFinishCallBack
	 * @param loadResultCallBack
	 * @param isLargeMode        是否是大图模式, 新鲜事可以切换大图和小图模式
	 */
	public FreshNewsAdapter(Activity activity, LoadFinishCallBack loadFinishCallBack, LoadResultCallBack
			loadResultCallBack, boolean isLargeMode) {

		mActivity = activity;
		mLoadFinishCallBack = loadFinishCallBack;
		mLoadResultCallBack = loadResultCallBack;
		this.mIsLargeMode = isLargeMode;

		mNewses = new ArrayList<>();

		int loadingResouce = isLargeMode ? R.drawable.ic_loading_large : R.drawable.ic_loading_small;

		mOptions = ImageLoaderProxy.getOptions4PictureList(loadingResouce);


	}


	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		int  layoutId = mIsLargeMode ? R.layout.item_fresh_news : R.layout.item_fresh_news_small;
		View view     = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
		return new MyViewHolder(view);
	}

	@Override
	public void onBindViewHolder(MyViewHolder holder, final int position) {
		final HomeNewsBean.Posts news = mNewses.get(position);

		//
		ImageLoaderProxy.displayImage(news.url, holder.img, mOptions);
		holder.tv_title.setText(news.title);
		holder.tv_info.setText(news.author.name + "@" + news.tags);
		holder.tv_views.setText("浏览" + 123 + "次");

		if (mIsLargeMode) {
			// 分享按钮的点击事件
			holder.tv_share.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ShareUtil.shareText(mActivity, news.title + " " + news.url);
				}
			});

			// 给cardView设置监听事件
			holder.card.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// 到详情页面
					toDetailActivity(position);

				}
			});

			setAnimation(holder.card, position);


		} else {
			holder.ll_content.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// 到详情页面
					toDetailActivity(position);

				}
			});

			setAnimation(holder.ll_content, position);

		}

	}


	/**
	 * 首页新闻的详情页面
	 *
	 * @param position
	 */
	private void toDetailActivity(int position) {
		Intent intent = new Intent(mActivity, HomeNewsDetailActivity.class);
		intent.putExtra(HomeNewsDetailActivity.DATA_HOME_NEWS, mNewses);
		intent.putExtra(HomeNewsDetailActivity.DATA_POSITION, position);
		mActivity.startActivity(intent);


	}

	private void setAnimation(View target, int position) {
		if (position > mLastPosition) {
			Animation animation = AnimationUtils.loadAnimation(target.getContext(), R.anim.item_bottom_in);
			target.startAnimation(animation);
			mLastPosition = position;
		}
	}


	/**
	 * 当适配器创建的view（即列表项view）被窗口分离（即滑动离开了当前窗口界面）就会被调用
	 *
	 * @param holder
	 */
	@Override
	public void onViewDetachedFromWindow(MyViewHolder holder) {
		super.onViewDetachedFromWindow(holder);
		if (mIsLargeMode) {
			holder.card.clearAnimation();
		} else {
			holder.ll_content.clearAnimation();
		}
	}

	@Override
	public int getItemCount() {
		return mNewses.size();
	}


	public void loadFirst() {
		mPage = 1;
		loadDataByNetworkType();

	}

	/**
	 * 通过网络加载数据, 并且缓存数据数据库中
	 */
	private void loadDataByNetworkType() {

		if (NetWorkUtil.isNetWorkConnected(mActivity)) {
			// 直接请求JavaBean
			final Request<HomeNewsBean> request = new JavaBeanRequest<HomeNewsBean>(URLs.getUrlHomeNews(mPage),
					HomeNewsBean.class);
			CallServer.getInstance().add(0, request, new OnResponseListener<HomeNewsBean>() {
				mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
				mLoadFinishCallBack.loadFinish(null);

				if (mPage == 1) {
					mNewses.clear();
					// FreshNewsCache

					// TODO
				}
				mNewses.addAll(response.get().posts);// 重新添加数据
				notifyDataSetChanged();
				// 添加缓存到greenDao				@Override
				public void onStart(int what) {

				}

				@Override
				public void onSucceed(int what, com.yolanda.nohttp.rest.Response<HomeNewsBean> response) {


				}

				@Override
				public void onFailed(int what, com.yolanda.nohttp.rest.Response<HomeNewsBean> response) {
					mLoadResultCallBack.onError(LoadResultCallBack.ERROR_NET, response.getException().getMessage());
					mLoadFinishCallBack.loadFinish(null);

				}

				@Override
				public void onFinish(int what) {

				}
			});


		} else {    // 网络未连接
			mLoadResultCallBack.onSuccess(LoadResultCallBack.SUCCESS_OK, null);
			mLoadFinishCallBack.loadFinish(null);

			if (mPage == 1) {
				mNewses.clear();
				ShowToast.Short(ConstantString.LOAD_NO_NETWORK);

			}

			// mNewses.addAll(FreshNewsCache.get)
			notifyDataSetChanged();

		}
	}

	/**
	 * 加载下一页
	 */
	public void loadNextPage() {
		mPage++;
		loadDataByNetworkType();
	}

	static class MyViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.tv_title)
		TextView tv_title;

		@BindView(R.id.tv_info)
		TextView tv_info;

		@BindView(R.id.tv_views)
		TextView tv_views;

		@BindView(R.id.img)
		ImageView img;

		@Nullable
		@BindView(R.id.tv_share)
		TextView tv_share;

		@Nullable
		@BindView(R.id.card)
		CardView card;

		@Nullable
		@BindView(R.id.ll_content)
		LinearLayout ll_content;


		MyViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);   // adapter中使用ButterKnife
		}
	}


}
