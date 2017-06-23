package www.luliang.jdan.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.victor.loading.rotate.RotateLoading;

import butterknife.BindView;
import butterknife.ButterKnife;
import www.luliang.jdan.R;
import www.luliang.jdan.adapter.PictureAdapter;
import www.luliang.jdan.base.BaseFragment;
import www.luliang.jdan.callback.LoadFinishCallBack;
import www.luliang.jdan.callback.LoadMoreListener;
import www.luliang.jdan.callback.LoadResultCallBack;
import www.luliang.jdan.model.Picture;
import www.luliang.jdan.view.AutoLoadRecyclerView;

import static www.luliang.jdan.R.id.loading;

/**
 * 无聊图页面
 */
public class PictureFragment extends BaseFragment implements LoadResultCallBack, LoadFinishCallBack {

	@BindView(R.id.autoLoadRecyclerView)
	AutoLoadRecyclerView mRecyclerView;

	@BindView(R.id.swipeRefreshLayout)
	SwipeRefreshLayout mSwipeRefreshLayout;

	@BindView(loading)
	RotateLoading mLoading;

	private PictureAdapter mAdapter;
	private boolean isFirstChange = false;  // 用于记录是否是首次进入
	protected Picture.PictureType mType;


	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);    // 是否可以按下Menu键
		isFirstChange = true;
		mType = Picture.PictureType.BoringPicture;
	}

	@Override
	public void onStart() {
		super.onStart();
		// TODO
		//		EventBus.getDefault().register(this);

	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
			savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_auto_load, container, false);
		// 注册
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mRecyclerView.setHasFixedSize(false);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

		mRecyclerView.setLoadMoreListener(new LoadMoreListener() {
			@Override
			public void loadMore() {
				mAdapter.loadNextPage();    // 加载更多
			}
		});

		// 下拉刷新的颜色切换
		mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color
				.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);

		// 下拉刷新的监听
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mAdapter.loadFirst();

			}
		});

		mRecyclerView.setOnPauseListenerParams(false, true);
		mAdapter = new PictureAdapter(getActivity(), this, mRecyclerView, mType);   // 其中mRecyclerView实现了
		// LoadFinishCallBack接口
		mRecyclerView.setAdapter(mAdapter);

		// PictureAdapter中设置的回调, loadFinish()回调
		mAdapter.setSaveFileCallBack(this);

		mAdapter.loadFirst();// 加载数据
		mLoading.start();


	}

	@Override
	public void onStop() {
		super.onStop();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	// toolBar上的刷新按钮
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_refresh, menu);
	}

	// toolBar上的刷新按钮被点击
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.action_refresh) {
			mSwipeRefreshLayout.setRefreshing(true);
			mAdapter.loadFirst();
			return true;
		}
		return false;
	}


	// 加载完成的回调
	@Override
	public void loadFinish(Object obj) {


	}

	// 加载成功的回调
	@Override
	public void onSuccess(int result, Object object) {

		mLoading.stop();    // 停止下拉刷新
		if (mSwipeRefreshLayout.isRefreshing()) {
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}

	// 加载失败的回调
	@Override
	public void onError(int code, String msg) {
		mLoading.stop();    // 停止下拉刷新
		if (mSwipeRefreshLayout.isRefreshing()) {
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}
}
