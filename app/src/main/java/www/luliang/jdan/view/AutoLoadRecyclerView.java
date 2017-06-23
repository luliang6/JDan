package www.luliang.jdan.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.nostra13.universalimageloader.core.ImageLoader;

import www.luliang.jdan.callback.LoadFinishCallBack;
import www.luliang.jdan.callback.LoadMoreListener;

/**
 * 自定义的RecyclerView
 */

public class AutoLoadRecyclerView extends RecyclerView implements LoadFinishCallBack {
	private boolean mIsLoadingMore;// 是否加载更多

	private LoadMoreListener mLoadMoreListener;

	public AutoLoadRecyclerView(Context context) {
		this(context, null);
	}

	public AutoLoadRecyclerView(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AutoLoadRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mIsLoadingMore = false;
		// 添加滑动的监听
		addOnScrollListener(new AutoLoadScrollListener(null, true, true));

	}

	/**
	 * 设置滑动时的监听参数
	 * @param pauseOnScroll
	 * @param pauseOnFling
	 */
	public void setOnPauseListenerParams(boolean pauseOnScroll, boolean pauseOnFling) {
		addOnScrollListener(new AutoLoadScrollListener(ImageLoader.getInstance(), pauseOnScroll, pauseOnFling));
	}


	// 设置接口的监听
	public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
		this.mLoadMoreListener = loadMoreListener;
	}

	@Override
	public void loadFinish(Object obj) {
		mIsLoadingMore = false;
	}


	private class AutoLoadScrollListener extends OnScrollListener {
		private       ImageLoader imageLoader;
		private final boolean     pauseOnScroll;
		private final boolean     pauseOnFling;

		/**
		 * @param imageLoader
		 * @param pauseOnScroll 滑动时暂停加载
		 * @param pauseOnFling  快速滑动时暂停加载
		 */
		public AutoLoadScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling) {
			super();
			this.imageLoader = imageLoader;
			this.pauseOnScroll = pauseOnScroll;
			this.pauseOnFling = pauseOnFling;
		}

		// 滑动时的回调
		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			super.onScrolled(recyclerView, dx, dy);

			//由于GridLayoutManager是LinearLayoutManager子类，所以也适用
			if (getLayoutManager() instanceof LinearLayoutManager) {
				int lastVisibleItemPosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
				int totalItemCount          = AutoLoadRecyclerView.this.getAdapter().getItemCount();

				//有回调接口，并且开启加载更多，并且剩下2个item，并且向下滑动，则自动加载
				if (mLoadMoreListener != null && !mIsLoadingMore && lastVisibleItemPosition >= totalItemCount - 2 &&
						dy > 0) {
					mLoadMoreListener.loadMore();
					mIsLoadingMore = true;
				}
			}

		}

		// 滑动状态改变时的回调
		@Override
		public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
			super.onScrollStateChanged(recyclerView, newState);

			if (imageLoader != null) {
				switch (newState) {
					case SCROLL_STATE_IDLE:     // 停止滚动
						imageLoader.resume();   // 开始加载数据
						break;

					case SCROLL_STATE_DRAGGING: // pager处于正在拖拽中
						if (pauseOnScroll) {
							imageLoader.pause();
						} else {
							imageLoader.resume();
						}
						break;

					case SCROLL_STATE_SETTLING: // pager正在自动沉降，相当于松手后，pager恢复到一个完整pager的过程
						if (pauseOnFling) {
							imageLoader.pause();
						} else {
							imageLoader.resume();
						}
						break;

					default:
						break;
				}

			}

		}


	}
}
