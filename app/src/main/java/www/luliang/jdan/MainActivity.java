package www.luliang.jdan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import www.luliang.jdan.base.BaseActivity;
import www.luliang.jdan.ui.fragment.HomeNewsFragment;
import www.luliang.jdan.ui.fragment.MainMenuFragment;
import www.luliang.jdan.utils.ShowToast;

/**
 * 程序主界面
 */
public class MainActivity extends BaseActivity {
	@BindView(R.id.toolbar)
	Toolbar mToolbar;

	@BindView(R.id.drawer_layout)
	DrawerLayout mDrawerLayout;

	// Toolbar切换的toggle
	private ActionBarDrawerToggle mActionBarDrawerToggle;
	private BroadcastReceiver     mNetStateReceiver;

	private long mExitTime;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initData();

	}

	@Override
	protected void onStart() {
		super.onStart();
		//		EventBus.getDefault().register(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		//		EventBus.getDefault().unregister(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mNetStateReceiver);
	}

	@Override
	protected void initView() {
		ButterKnife.bind(this);

		// 设置Toolbar的文字和颜色
		mToolbar.setTitleTextColor(Color.WHITE);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);// 设置允许返回

		// Drawer的切换
		mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string
				.app_name) {
			@Override
			public void onDrawerClosed(View drawerView) {
				// 重新加载OptionsMenu
				invalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				invalidateOptionsMenu();
			}
		};

		mActionBarDrawerToggle.syncState();

		mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);// mActionBarDrawerToggle实现了DrawerListener接口

		replaceFragment(R.id.drawer_container, new MainMenuFragment());// 替换菜单部分
		replaceFragment(R.id.frame_container, new HomeNewsFragment());// 替换内容部分
	}

	@Override
	protected void initData() {

	}

	@Override
	protected void initEvent() {
		// 网络状态的广播
		mNetStateReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
					// TODO

					/*if (NetWorkUtil.isNetWorkConnected(MainActivity.this)) {
						// 通过EventBus来发送网络连接情况
						EventBus.getDefault().post(new NetWorkEvent(NetWorkEvent.AVAILABLE));

					} else {
						EventBus.getDefault().post(new NetWorkEvent(NetWorkEvent.UNAVAILABLE));

					}*/
				}
			}
		};

		registerReceiver(mNetStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

	}

	// 似乎没有用到?
	/*public void onEvent(NetWorkEvent event) {

		if (event.getType() == NetWorkEvent.UNAVAILABLE) {

			if (noNetWorkDialog == null) {
				noNetWorkDialog = new MaterialDialog.Builder(MainActivity.this).title("无网络连接").content("去开启网络?")
						.positiveText("是").backgroundColor(getResources().getColor(JDApplication.COLOR_OF_DIALOG))
						.contentColor(JDApplication.COLOR_OF_DIALOG_CONTENT).positiveColor(JDApplication
								.COLOR_OF_DIALOG_CONTENT).negativeColor(JDApplication.COLOR_OF_DIALOG_CONTENT)
						.titleColor(JDApplication.COLOR_OF_DIALOG_CONTENT).negativeText("否").callback(new
								                                                                              MaterialDialog.ButtonCallback() {
									                                                                              @Override
									                                                                              public void onPositive(MaterialDialog dialog) {
										                                                                              Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
										                                                                              startActivity(intent);
									                                                                              }

									                                                                              @Override
									                                                                              public void onNegative(MaterialDialog dialog) {
									                                                                              }
								                                                                              })
								                                                                              .cancelable(false).build();
			}
			if (!noNetWorkDialog.isShowing()) {
				noNetWorkDialog.show();
			}
		}

	}*/

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 返回键按下
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if (System.currentTimeMillis() - mExitTime > 2000) {
				ShowToast.Short("再按一次退出程序!");
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}

	///////////////////////////////////////////////////////////////////////////
	// Drawer Method
	///////////////////////////////////////////////////////////////////////////

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(@Nullable Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mActionBarDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mActionBarDrawerToggle.onConfigurationChanged(newConfig);
	}

	public void closeDrawer() {
		mDrawerLayout.closeDrawers();
	}


}
