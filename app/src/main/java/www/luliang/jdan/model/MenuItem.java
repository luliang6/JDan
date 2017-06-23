package www.luliang.jdan.model;

import android.support.v4.app.Fragment;

/**
 * 左侧的菜单项
 */

public class MenuItem {
	public enum FragmentType {
		FreshNews, BoringPicture, Sister, Joke, Video
	}

	private String                    title;
	private int                       resourceId;
	private FragmentType              type;
	private Class<? extends Fragment> fragment;

	public MenuItem() {
	}

	public MenuItem(String title, int resourceId, Class<? extends Fragment> fragment) {
		this.resourceId = resourceId;
		this.title = title;
		this.fragment = fragment;
	}

	public MenuItem(String title, int resourceId, FragmentType type, Class<? extends Fragment> fragment) {
		this.title = title;
		this.resourceId = resourceId;
		this.type = type;
		this.fragment = fragment;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getResourceId() {
		return resourceId;
	}

	public Class<? extends Fragment> getFragment() {
		return fragment;
	}

	public void setFragment(Class<? extends Fragment> fragment) {
		this.fragment = fragment;
	}


	public FragmentType getType() {
		return type;
	}

	public void setType(FragmentType type) {
		this.type = type;
	}

}
