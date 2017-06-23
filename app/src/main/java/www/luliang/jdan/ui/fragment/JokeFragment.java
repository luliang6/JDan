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
import www.luliang.jdan.adapter.JokeAdapter;
import www.luliang.jdan.base.BaseFragment;
import www.luliang.jdan.callback.LoadMoreListener;
import www.luliang.jdan.callback.LoadResultCallBack;
import www.luliang.jdan.utils.ShowToast;
import www.luliang.jdan.view.AutoLoadRecyclerView;

import static www.luliang.jdan.R.id.loading;

/**
 * 段子的Fragment
 */
public class JokeFragment extends BaseFragment implements LoadResultCallBack {
	@BindView(R.id.autoLoadRecyclerView)
	AutoLoadRecyclerView mAutoLoadRecyclerView;

	@BindView(R.id.swipeRefreshLayout)
	SwipeRefreshLayout mSwipeRefreshLayout;

	@BindView(loading)
	RotateLoading mLoading;

	private JokeAdapter mAdapter;


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
			savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_auto_load, container, false);
		ButterKnife.bind(this, view);
		return view;

	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mAutoLoadRecyclerView.setHasFixedSize(false);
		mAutoLoadRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

		mAutoLoadRecyclerView.setLoadMoreListener(new LoadMoreListener() {
			@Override
			public void loadMore() {
				// 加载更多
				mAdapter.loadNextPage();
			}
		});

		mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color
				.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);

		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				// 加载第一页
				mAdapter.loadFirst();
			}
		});

		mAdapter = new JokeAdapter(getActivity(), mAutoLoadRecyclerView, this);
		mAutoLoadRecyclerView.setAdapter(mAdapter);

		mAdapter.loadFirst();
		mLoading.start();
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
		mLoading.stop();
		if (mSwipeRefreshLayout.isRefreshing()) {
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}

	@Override
	public void onError(int code, String msg) {
		mLoading.stop();
		ShowToast.Short(LOAD_FAILED);
		if (mSwipeRefreshLayout.isRefreshing()) {
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}
}
