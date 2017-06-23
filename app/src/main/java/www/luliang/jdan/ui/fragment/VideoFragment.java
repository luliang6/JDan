package www.luliang.jdan.ui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
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
import www.luliang.jdan.adapter.VideoAdapter;
import www.luliang.jdan.base.BaseFragment;
import www.luliang.jdan.base.ConstantString;
import www.luliang.jdan.callback.LoadMoreListener;
import www.luliang.jdan.callback.LoadResultCallBack;
import www.luliang.jdan.utils.ShowToast;
import www.luliang.jdan.view.AutoLoadRecyclerView;

/**
 * 小电影的Fragment
 */
public class VideoFragment extends BaseFragment implements LoadResultCallBack {


	@BindView(R.id.autoLoadRecyclerView)
	AutoLoadRecyclerView mRecyclerView;
	@BindView(R.id.swipeRefreshLayout)
	SwipeRefreshLayout   mSwipeRefreshLayout;
	@BindView(R.id.loading)
	RotateLoading        loading;

	private VideoAdapter mAdapter;

	public VideoFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_auto_load, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mRecyclerView.setHasFixedSize(false);
		mRecyclerView.setLoadMoreListener(new LoadMoreListener() {
			@Override
			public void loadMore() {
				mAdapter.loadNextPage();
			}
		});
		mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color
				.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);

		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mAdapter.loadFirst();
			}
		});
		mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));// 设置GridLayout, 2列

		mAdapter = new VideoAdapter(getActivity(), this, mRecyclerView);
		mRecyclerView.setAdapter(mAdapter);
		mAdapter.loadFirst();
		loading.start();
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

	@Override
	public void onSuccess(int result, Object object) {
		loading.stop();
		if (mSwipeRefreshLayout.isRefreshing()) {
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}

	@Override
	public void onError(int code, String msg) {
		loading.stop();
		ShowToast.Short(ConstantString.LOAD_FAILED);
		if (mSwipeRefreshLayout.isRefreshing()) {
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}
}
