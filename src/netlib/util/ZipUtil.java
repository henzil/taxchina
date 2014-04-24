package netlib.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.util.Log;

/**
 * @author henzil
 * @version create time:2013-8-7_下午9:50:02
 * @Description zip包 压缩，解压缩
 */
public class ZipUtil {

	/**
	 * 压缩文件,文件夹
	 * 
	 * @param srcFileString
	 *            要压缩的文件/文件夹名字
	 * @param zipFileString
	 *            指定压缩的目的和名字
	 * @throws Exception
	 */
	public static void ZipFolder(String srcFileString, String zipFileString){
		Log.v("ZipUtil", "ZipFolder(String, String)");
		//使用指定校验和创建输出流
		CheckedOutputStream csum = null;
		ZipOutputStream outZip = null;
		try {
			csum = new CheckedOutputStream(new java.io.FileOutputStream(zipFileString), new CRC32());
			// 创建Zip包
			outZip = new ZipOutputStream(csum);

			// 打开要输出的文件
			File file = new File(srcFileString);

			// 压缩
			ZipFiles(file.getParent() + File.separator, file.getName(), outZip);
			// 完成,关闭
			outZip.finish();
			outZip.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(outZip != null){
				try {
					outZip.finish();
					outZip.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}// end of func

	/**
	 * 压缩文件
	 * 
	 * @param folderString
	 * @param fileString
	 * @param zipOutputSteam
	 * @throws Exception
	 */
	private static void ZipFiles(String folderString, String fileString, ZipOutputStream zipOutputSteam)
			throws Exception {
		Log.v("ZipUtil", "ZipFiles(String, String, ZipOutputStream)");

		if (zipOutputSteam == null)
			return;
		//启用压缩
		zipOutputSteam.setMethod(ZipOutputStream.DEFLATED);
		//压缩级别为最强压缩，但时间要花得多一点
		zipOutputSteam.setLevel(Deflater.BEST_COMPRESSION);
		File file = new File(folderString + fileString);

		// 判断是不是文件
		if (file.isFile()) {

			ZipEntry zipEntry = new ZipEntry(fileString);
			@SuppressWarnings("resource")
			FileInputStream inputStream = new FileInputStream(file);
			zipOutputSteam.putNextEntry(zipEntry);

			int len;
			byte[] buffer = new byte[4096];

			while ((len = inputStream.read(buffer)) != -1) {
				zipOutputSteam.write(buffer, 0, len);
			}

			zipOutputSteam.closeEntry();
		} else {

			// 文件夹的方式,获取文件夹下的子文件
			String fileList[] = file.list();

			// 如果没有子文件, 则添加进去即可
			if (fileList.length <= 0) {
				ZipEntry zipEntry = new ZipEntry(fileString + java.io.File.separator);
				zipOutputSteam.putNextEntry(zipEntry);
				zipOutputSteam.closeEntry();
			}

			// 如果有子文件, 遍历子文件
			for (int i = 0; i < fileList.length; i++) {
				ZipFiles(folderString, fileString + java.io.File.separator + fileList[i], zipOutputSteam);
			}// end of for

		}// end of if

	}// end of func
}
