package www.luliang.jdan.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import www.luliang.jdan.model.Picture;

/**
 * 妹子图的Fragment
 * 
 * 暂时范围物内容
 */
public class SisterFragment extends PictureFragment {

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setHasOptionsMenu(true);
		mType = Picture.PictureType.Sister;
	}
}
