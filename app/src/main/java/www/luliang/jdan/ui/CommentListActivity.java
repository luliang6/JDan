package www.luliang.jdan.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.victor.loading.rotate.RotateLoading;

import butterknife.BindView;
import butterknife.ButterKnife;
import www.luliang.jdan.R;
import www.luliang.jdan.adapter.CommentAdapter;
import www.luliang.jdan.base.BaseActivity;
import www.luliang.jdan.callback.LoadResultCallBack;
import www.luliang.jdan.utils.ShowToast;

/**
 * 评论页面
 */
public class CommentListActivity extends BaseActivity implements LoadResultCallBack {
	@BindView(R.id.swipe_refresh)
	SwipeRefreshLayout mSwipeRefreshLayout;
	@BindView(R.id.recycler_view)
	RecyclerView       mRecyclerView;
	@BindView(R.id.toolbar)
	Toolbar            mToolbar;
	@BindView(R.id.loading)
	RotateLoading      mLoading;

	private String  mThreadKey;
	private String  mThreadId;
	private boolean mIsFromFreshNews;

	private CommentAdapter mAdapter;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment_list);
		initView();
		initData();
	}

	@Override
	protected void initView() {
		ButterKnife.bind(this);

		mToolbar.setTitleTextColor(Color.WHITE);
		setSupportActionBar(mToolbar);
		mToolbar.setTitle("评论");
		mToolbar.setNavigationIcon(R.drawable.ic_actionbar_back);

		mRecyclerView.setHasFixedSize(false);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

		// 设置下拉刷新的4中颜色切换
		mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color
				.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);

		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				if (mIsFromFreshNews) {  // true
					// TODO
					// mAdapter.loadData4FreshNews();
				} else {
					//					mAdapter.loadData();
				}
			}
		});

	}

	@Override
	protected void initData() {
		mThreadKey = getIntent().getStringExtra(DATA_THREAD_KEY);// 新鲜事的评论此值为null
		mThreadId = getIntent().getStringExtra(DATA_THREAD_ID); //
		mIsFromFreshNews = getIntent().getBooleanExtra(DATA_IS_FROM_FRESH_NEWS, false); // true

		if (mIsFromFreshNews) {
			mAdapter = new CommentAdapter(this, mThreadId, true, this);
			if (TextUtils.isEmpty(mThreadId) || mThreadId.equals("0")) {// 如果mThreadId为空或者为0
				ShowToast.Short(FORBID_COMMENTS);
				finish();
			}
		} else {
			mAdapter = new CommentAdapter(this, mThreadKey, false, this);
			if (TextUtils.isEmpty(mThreadKey) || mThreadKey.equals("0")) {// 如果mThreadId为空或者为0
				ShowToast.Short(FORBID_COMMENTS);
				finish();
			}
		}
		mRecyclerView.setAdapter(mAdapter);

		if (mIsFromFreshNews) {
			mAdapter.loadData4FreshNews();
		} else {
			mAdapter.loadData();

		}


	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_comment_list, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			case R.id.action_edit:
				// 发送评论

				/*Intent intent = new Intent(this, PushCommentActivity.class);
				intent.putExtra(DATA_THREAD_ID, mAdapter.getThreadId());
				startActivityForResult(intent, 100);*/
				return true;
		}
		return super.onOptionsItemSelected(item);

	}

	@Override
	public void onSuccess(int result, Object object) {
		if (result == LoadResultCallBack.SUCCESS_NONE) {
			ShowToast.Short(NO_COMMENTS);

		}
		mLoading.stop();
		mSwipeRefreshLayout.setRefreshing(false);
	}

	@Override
	public void onError(int code, String msg) {
		mSwipeRefreshLayout.setRefreshing(false);
		mLoading.stop();
		ShowToast.Short(LOAD_FAILED);
	}
}
