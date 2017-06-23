package www.luliang.jdan.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import www.luliang.jdan.adapter.FreshNewsAdapter;
import www.luliang.jdan.base.BaseFragment;
import www.luliang.jdan.base.ConstantString;
import www.luliang.jdan.callback.LoadMoreListener;
import www.luliang.jdan.callback.LoadResultCallBack;
import www.luliang.jdan.utils.ShowToast;
import www.luliang.jdan.view.AutoLoadRecyclerView;

/**
 * 新鲜事的fragment
 */
public class HomeNewsFragment extends BaseFragment implements LoadResultCallBack {
	@BindView(R.id.autoLoadRecyclerView)
	AutoLoadRecyclerView mAutoLoadRecyclerView;

	@BindView(R.id.swipeRefreshLayout)
	SwipeRefreshLayout mSwipeRefreshLayout;// 下拉刷新

	@BindView(R.id.loading)
	RotateLoading mRotateLoading;   // 开源的进度条
	private FreshNewsAdapter mAdapter;


	public HomeNewsFragment() {

	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置Menu菜单
		setHasOptionsMenu(true);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
			savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_auto_load, container, false);
		// ButterKnife绑定view
		ButterKnife.bind(this, view);

		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mAutoLoadRecyclerView.setHasFixedSize(false);// RecyclerView的大小是否固定, 有利于优化
		mAutoLoadRecyclerView.setLoadMoreListener(new LoadMoreListener() {
			@Override
			public void loadMore() {
				mAdapter.loadNextPage();

			}
		});

		// 下拉刷新圆圈内部颜色的切换
		mSwipeRefreshLayout.setColorSchemeColors(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
				android.R.color.holo_orange_light, android.R.color.holo_red_light);

		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mAdapter.loadFirst();
			}
		});

		mAutoLoadRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));// 设置linearLayout布局管理器


		mAutoLoadRecyclerView.setOnPauseListenerParams(false, true);

		SharedPreferences sp          = PreferenceManager.getDefaultSharedPreferences(getActivity());
		boolean           isLargeMode = sp.getBoolean(SettingFragment.ENABLE_FRESH_BIG, true);

		mAdapter = new FreshNewsAdapter(getActivity(), mAutoLoadRecyclerView, this, isLargeMode);
		mAutoLoadRecyclerView.setAdapter(mAdapter);
		mAdapter.loadFirst();
		mRotateLoading.start();// 正在加载的进度条(圆圈)


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

	// 自己完成具体的实现
	@Override
	public void onSuccess(int result, Object object) {

		mRotateLoading.stop();// 加载成功, 进度条消失
		if (mSwipeRefreshLayout.isRefreshing()) {
			mSwipeRefreshLayout.setRefreshing(false);
		}

	}

	@Override
	public void onError(int code, String msg) {
		mRotateLoading.stop();
		ShowToast.Short(ConstantString.LOAD_FAILED);
		if (mSwipeRefreshLayout.isRefreshing()) {
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}
}
