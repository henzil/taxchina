package netlib.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageUtil {

	public static final int DEFAULT_MAX_WIDTH = 320;
	public static final int DEFAULT_MAX_HEIGHT = 480;

//
//	public static Bitmap rotateBitmap(Bitmap bitmap, float degrees) {
//		if (bitmap == null)
//			return null;
//		if (bitmap.isRecycled())
//			return null;
//		Matrix matrix = new Matrix();
//		matrix.postRotate(degrees);
//
//		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//		if (resizedBitmap != bitmap) {
//			bitmap.recycle();
//			bitmap = resizedBitmap;
//		}
//		return bitmap;
//	}

	public static Bitmap getBitmapFromMedia(Context context, String pathName) {
		return getBitmapFromMedia(context, pathName, DEFAULT_MAX_WIDTH, DEFAULT_MAX_HEIGHT);
	}

//
	public static Bitmap getBitmapFromMedia(Context context, String pathName, int maxWidth, int maxHeight) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		try {
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(pathName, options);
			options.inJustDecodeBounds = false;

			int outputWidth = options.outWidth;
			int outputHeight = options.outHeight;
//			Log.e("ImageUtil", "&&&&&&&&pathName = " + pathName + " outputHeight = " + outputHeight);
			if (maxWidth <= 0) {
				maxWidth = DEFAULT_MAX_WIDTH;
			}
			if (maxHeight <= 0) {
				maxHeight = DEFAULT_MAX_HEIGHT;
			}
			if (outputWidth < maxWidth && outputHeight < maxHeight) {
				bitmap = BitmapFactory.decodeFile(pathName);
			} else {
				int inSampleSize = 0;
				int widthSmapleSize = (int) (outputWidth / maxWidth);
				int heightSmapleSize = (int) (outputHeight / maxHeight);
				if (widthSmapleSize >= heightSmapleSize) {
					inSampleSize = widthSmapleSize;
				} else {
					inSampleSize = heightSmapleSize;
				}
				options.inSampleSize = inSampleSize;
				bitmap = BitmapFactory.decodeFile(pathName, options);
			}

		} catch (OutOfMemoryError oom) {
			Log.e("ImageUtil", oom.getMessage(), oom);
			System.gc();
			return null;
		} catch (Exception e) {
			Log.e("ImageUtil", e.getMessage(), e);
			return null;
		}
		return bitmap;
	}

//	public static Bitmap drawableToBitmap(Drawable drawable) {
//		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
//				drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
//		Canvas canvas = new Canvas(bitmap);
//		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//		drawable.draw(canvas);
//		return bitmap;
//	}

	public static int getBitmapByteCount(Bitmap bitmap) {
		if (bitmap == null || bitmap.isRecycled()) {
			return 0;
		}
		return bitmap.getRowBytes() * bitmap.getHeight();
	}

//	public static String getPathName(String url) {
//		return url.substring(url.lastIndexOf("/") + 1);
//	}
//
//	public static String getExtentionName(String url) {
//		if ((url != null) && (url.length() > 0)) {
//			int dot = url.lastIndexOf('.');
//			if ((dot > -1) && (dot < (url.length() - 1))) {
//				return url.substring(dot + 1);
//			}
//		}
//		return null;
//	}
//
//	public static Bitmap.CompressFormat getImageType(String imageType) {
//		if (imageType.equalsIgnoreCase("png")) {
//			return Bitmap.CompressFormat.PNG;
//		} else if (imageType.equalsIgnoreCase("jpg")) {
//			return Bitmap.CompressFormat.JPEG;
//		} else {
//			return Bitmap.CompressFormat.PNG;
//		}
//	}

	public static boolean compressBitmap(Bitmap bitmap, Bitmap.CompressFormat compressFormat, String compressPath) {
		if (bitmap == null)
			return false;
		OutputStream fileOutputStream = null;
		File file = new File(compressPath);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		try {
			fileOutputStream = new FileOutputStream(file);

			bitmap.compress(compressFormat, 100, fileOutputStream);
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
