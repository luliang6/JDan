package www.luliang.jdan.view.imageloader;


import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import www.luliang.jdan.BuildConfig;

/**
 * ImageLoader的代理
 */
public class ImageLoaderProxy {

	private static final int MAX_DISK_CACHE   = 1024 * 1024 * 50;// 磁盘缓存50M
	private static final int MAX_MEMORY_CACHE = 1024 * 1024 * 10;// 内存缓存10M
	private static ImageLoader sImageLoader;
	private static boolean isShowLog = false;


	public static ImageLoader getImageLoader() {
		// 单例
		if (sImageLoader == null) {
			synchronized (ImageLoaderProxy.class) {
				sImageLoader = ImageLoader.getInstance();// 获取ImageLoader的实例
			}
		}

		return sImageLoader;
	}

	/**
	 * 加载图片列表专用，加载前会重置View
	 * {@link com.nostra13.universalimageloader.core.DisplayImageOptions.Builder#resetViewBeforeLoading} = true
	 *
	 * @param loadingResource
	 * @return
	 */
	public static DisplayImageOptions getOptions4PictureList(int loadingResource) {
		return new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).bitmapConfig(Bitmap.Config
				.RGB_565).resetViewBeforeLoading(true).showImageOnLoading(loadingResource).showImageOnFail
				(loadingResource).build();
	}

	/**
	 * 自定义图片显示的option
	 *
	 * @param url
	 * @param target
	 * @param options
	 */
	public static void displayImage(String url, ImageView target, DisplayImageOptions options) {
		sImageLoader.displayImage(url, target, options);
	}


	/**
	 * 初始化ImageLoader
	 *
	 * @param context
	 */
	public static void initImageLoader(Context context) {

		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context);
		builder.tasksProcessingOrder(QueueProcessingType.LIFO);
		builder.diskCacheSize(MAX_DISK_CACHE);
		builder.memoryCacheSize(MAX_MEMORY_CACHE);
		builder.memoryCache(new LruMemoryCache(MAX_MEMORY_CACHE));
		if (BuildConfig.DEBUG && isShowLog) {
			builder.writeDebugLogs();
		}
		getImageLoader().init(builder.build());
	}


	/**
	 * 图片列表页专用
	 *
	 * @param url
	 * @param target
	 * @param loadingResouce
	 * @param loadingListener
	 * @param progressListener
	 */
	public static void displayImageList(String url, ImageView target, int loadingResouce, SimpleImageLoadingListener
			loadingListener, ImageLoadingProgressListener progressListener) {
		sImageLoader.displayImage(url, target, getOptions4PictureList(loadingResouce), loadingListener,
				progressListener);
	}

	/**
	 * 自定义加载中图片
	 *
	 * @param url
	 * @param target
	 * @param loadingResource
	 */
	public static void displayImageWithLoadingPicture(String url, ImageView target, int loadingResource) {
		sImageLoader.displayImage(url, target, getOptions4PictureList(loadingResource));
	}

}
