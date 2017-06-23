package www.luliang.jdan.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import www.luliang.jdan.R;
import www.luliang.jdan.base.BaseActivity;
import www.luliang.jdan.model.HomeNewsBean;
import www.luliang.jdan.ui.fragment.HomeNewsDetailFragment;

/**
 * 首页新闻的详情页面
 */
public class HomeNewsDetailActivity extends BaseActivity {
	@BindView(R.id.viewpager)
	ViewPager mViewPager;

	@BindView(R.id.toolbar)
	Toolbar mToolbar;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_news_detail);

		initView();
		initData();
	}

	@Override
	protected void initView() {
		ButterKnife.bind(this);

		mToolbar.setTitleTextColor(Color.WHITE);
		setSupportActionBar(mToolbar);
		mToolbar.setNavigationIcon(R.drawable.ic_actionbar_back);
	}

	@Override
	protected void initData() {
		// toDetailActivity()传递过来的DATA_FRESH_NEWS, 即mFreshNews
		ArrayList<HomeNewsBean.Posts> mNewses = (ArrayList<HomeNewsBean.Posts>) getIntent().getSerializableExtra
				(DATA_HOME_NEWS);

		int position = getIntent().getIntExtra(DATA_POSITION, 0);
		mViewPager.setAdapter(new HomeNewsDetailAdapter(getSupportFragmentManager(), mNewses));
		mViewPager.setCurrentItem(position);
	}

	private class HomeNewsDetailAdapter extends FragmentPagerAdapter {

		private final ArrayList<HomeNewsBean.Posts> newses;

		HomeNewsDetailAdapter(FragmentManager fm, ArrayList<HomeNewsBean.Posts> homeNewses) {
			super(fm);
			this.newses = homeNewses;
		}

		@Override
		public Fragment getItem(int position) {

			return HomeNewsDetailFragment.getInstance(newses.get(position));
		}

		@Override
		public int getCount() {
			return newses.size();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);

	}
}
