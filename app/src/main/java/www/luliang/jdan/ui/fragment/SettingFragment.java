package www.luliang.jdan.ui.fragment;

import android.preference.Preference;
import android.preference.PreferenceFragment;

/**
 * 设置中心的fragment, 在activity_setting.xml文件中使用
 */

public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

	public static final String CLEAR_CACHE      = "clear_cache";
	public static final String ABOUT_APP        = "about_app";
	public static final String APP_VERSION      = "app_version";
	public static final String ENABLE_SISTER    = "enable_sister";      // 开启妹子图
	public static final String ENABLE_FRESH_BIG = "enable_fresh_big";   // 开启新鲜事的大图模式


	@Override
	public boolean onPreferenceClick(Preference preference) {
		return false;
	}
}
