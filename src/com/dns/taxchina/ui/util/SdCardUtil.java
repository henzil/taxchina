package com.dns.taxchina.ui.util;

import java.io.File;
import java.text.NumberFormat;

import android.app.Activity;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

/**
 * @author fubiao
 * @version create time:2014-5-15_上午10:39:16
 * @Description 获取sd卡容量 util
 */
public class SdCardUtil {
	
	private Activity context;

	public SdCardUtil(Activity activity) {
		this.context = activity;
	}

	/**
	 * 判断是否已经安装SD卡
	 * 
	 * @return
	 */
	public static boolean isSDCardExist() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * 获得SD卡总大小
	 * 
	 * @return
	 */
	public String getSDTotalSize() {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return Formatter.formatFileSize(context, blockSize * totalBlocks);
	}

	/**
	 * 获得sd卡剩余容量，即可用大小
	 * 
	 * @return
	 */
	public String getSDAvailableSize() {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return Formatter.formatFileSize(context, blockSize * availableBlocks);
	}
	
	/**
	 * 获得sd卡剩余容量百分比
	 * 
	 * @return
	 */
	public String getSDAvailableFormat() {
		long total = 0, alreadyUse = 0;
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		long availableBlocks = stat.getAvailableBlocks();
		total = blockSize * totalBlocks;
		alreadyUse = blockSize * availableBlocks;
		NumberFormat percentFormat = NumberFormat.getPercentInstance();
		return percentFormat.format((float) alreadyUse / total);
	}

	/**
	 * 获得机身内存总大小
	 * 
	 * @return
	 */
	public String getRomTotalSize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return Formatter.formatFileSize(context, blockSize * totalBlocks);
	}

	/**
	 * 获得机身可用内存
	 * 
	 * @return
	 */
	public String getRomAvailableSize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return Formatter.formatFileSize(context, blockSize * availableBlocks);
	}
	
	/**
	 * 获得机身可用内存百分比
	 * 
	 * @return
	 */
	public String getRomAvailableSizeFormat() {
		long total = 0, alreadyUse = 0;
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		long availableBlocks = stat.getAvailableBlocks();
		total = blockSize * totalBlocks;
		alreadyUse = blockSize * availableBlocks;
		NumberFormat percentFormat = NumberFormat.getPercentInstance();
		return percentFormat.format((float) alreadyUse / total);
	}
}