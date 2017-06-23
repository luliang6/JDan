package www.luliang.jdan.ui.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import www.luliang.jdan.MainActivity;
import www.luliang.jdan.R;
import www.luliang.jdan.base.BaseFragment;
import www.luliang.jdan.model.MenuItem;

import static www.luliang.jdan.ui.fragment.SettingFragment.ENABLE_SISTER;

/**
 * 菜单的Fragment
 */

public class MainMenuFragment extends BaseFragment {
	@BindView(R.id.recycler_view)
	RecyclerView mRecyclerView;

	@BindView(R.id.rl_container)
	RelativeLayout mRlContainer;


	private MainActivity        mMainActivity;
	private LinearLayoutManager mLayoutManager;

	private MenuItem.FragmentType mCurrentFragment = MenuItem.FragmentType.FreshNews;
	private MenuAdapter mAdapter;

	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (activity instanceof MainActivity) {
			mMainActivity = (MainActivity) activity;
		} else {
			throw new IllegalArgumentException("The activity must be a MainActivity !");
		}

	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
			savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_drawer, container, false);
		ButterKnife.bind(this, view);

		mLayoutManager = new LinearLayoutManager(getActivity());
		mRecyclerView.setLayoutManager(mLayoutManager); // 设置线性布局管理器

		// 设置的点击事件
		mRlContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO
				//				startActivity(new Intent(getActivity(), SettingActivity.class));
				mMainActivity.closeDrawer();    // 关闭菜单
			}
		});

		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mAdapter = new MenuAdapter();
		// 添加数据, 新鲜事, 无聊图 段子 小电影
		addAllMenuItems(mAdapter);
		mRecyclerView.setAdapter(mAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		//要显示妹子图而现在没显示，则重新设置适配器
		if (sp.getBoolean(ENABLE_SISTER, false) /*&& mAdapter.mMenuItems.size() == 4*/) {
			addAllMenuItems(mAdapter);
			mAdapter.notifyDataSetChanged();
		} else if (!sp.getBoolean(ENABLE_SISTER, false)/* && mAdapter.mMenuItems.size() == 5*/) {
			//			addAllMenuItemsNoSister(mAdapter);
			mAdapter.notifyDataSetChanged();

		}


	}


	public class MenuAdapter extends RecyclerView.Adapter<MyViewHolder> {

		private ArrayList<MenuItem> mMenuItems; // MenuItems在addAllMenuItems()或者addMenuItemsNoSister()中获取

		public MenuAdapter() {
			mMenuItems = new ArrayList<>();
		}

		@Override
		public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item, parent, false);

			return new MyViewHolder(view);
		}

		@Override
		public void onBindViewHolder(MyViewHolder holder, int position) {
			final MenuItem menuItem = mMenuItems.get(position);
			holder.tv_title.setText(menuItem.getTitle());
			holder.img_menu.setImageResource(menuItem.getResourceId());


			// 点击事件

			holder.rl_container.setOnClickListener(new View.OnClickListener() {
				                                       @Override
				                                       public void onClick(View v) {
					                                       try {
						                                       if (mCurrentFragment != menuItem.getType()) {
							                                       // 反射
							                                       Fragment fragment = (Fragment) Class.forName
									                                       (menuItem.getFragment().getName())
									                                       .newInstance();
							                                       mMainActivity.replaceFragment(R.id.frame_container,
									                                       fragment);
							                                       mCurrentFragment = menuItem.getType();

						                                       }
					                                       } catch (Exception e) {
						                                       e.printStackTrace();
					                                       }
					                                       mMainActivity.closeDrawer();
				                                       }


			                                       }

			);

		}


		@Override
		public int getItemCount() {
			return mMenuItems.size();
		}
	}

	private static class MyViewHolder extends RecyclerView.ViewHolder {
		private ImageView      img_menu;
		private TextView       tv_title;
		private RelativeLayout rl_container;

		public MyViewHolder(View itemView) {
			super(itemView);
			img_menu = (ImageView) itemView.findViewById(R.id.img_menu);
			tv_title = (TextView) itemView.findViewById(R.id.tv_title);
			rl_container = (RelativeLayout) itemView.findViewById(R.id.rl_container);
		}

	}


	/**
	 * 添加所有菜单项, 包含妹子图
	 */
	private void addAllMenuItems(MenuAdapter adapter) {
		mAdapter.mMenuItems.clear();

		mAdapter.mMenuItems.add(new MenuItem("首页", R.drawable.ic_explore_white_24dp, MenuItem.FragmentType.FreshNews,
				HomeNewsFragment.class));
		mAdapter.mMenuItems.add(new MenuItem("无聊图", R.drawable.ic_mood_white_24dp, MenuItem.FragmentType
				.BoringPicture, PictureFragment.class));
		mAdapter.mMenuItems.add(new MenuItem("妹子图", R.drawable.ic_local_florist_white_24dp, MenuItem.FragmentType
				.Sister, SisterFragment.class));
		mAdapter.mMenuItems.add(new MenuItem("段子", R.drawable.ic_chat_white_24dp, MenuItem.FragmentType.Joke,
				JokeFragment.class));

		mAdapter.mMenuItems.add(new MenuItem("小电影", R.drawable.ic_movie_white_24dp, MenuItem.FragmentType.Video,
				VideoFragment.class));

	}

	/**
	 * 添加所有菜单项, 不包含妹子图
	 */
	private void addMenuItemsNoSister(MenuAdapter mAdapter) {
		mAdapter.mMenuItems.clear();
		mAdapter.mMenuItems.add(new MenuItem("新鲜事", R.drawable.ic_explore_white_24dp, MenuItem.FragmentType.FreshNews,
				HomeNewsFragment.class));
		mAdapter.mMenuItems.add(new MenuItem("无聊图", R.drawable.ic_mood_white_24dp, MenuItem.FragmentType
				.BoringPicture, PictureFragment.class));
        /*mAdapter.mMenuItems.add(new MenuItem("段子", R.drawable.ic_chat_white_24dp, MenuItem.FragmentType.Joke,
                JokeFragment.class));
		mAdapter.mMenuItems.add(new MenuItem("小电影", R.drawable.ic_movie_white_24dp, MenuItem.FragmentType.Video,
				VideoFragment.class));*/
	}


}
