package netlib.net;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import netlib.util.LibIOUtil;
import netlib.util.PhoneConnectionUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author liangjie
 * @version create time:2014-4-21下午4:52:33
 * @Email liangjie@witmob.com
 * @Description 上传多张图片
 */
@SuppressLint("HandlerLeak")
public class UploadUtil {
	private final static String TAG = "UploadImgsUtil";

	private static final int SET_CONNECTION_TIMEOUT = 10000;
	private static final int SET_SOCKET_TIMEOUT = 20000;

	private static final int SUCCESS = 10;
	private static final int FAIL = 20;

	private Context context;

	public UploadUtil(Context context) {
		super();
		this.context = context;
	}

	// 上传文件
	/***
	 * 
	 * @param context
	 * @param actionUrl
	 *            请求地址
	 * @param paramMap
	 *            参数集合
	 * @param map
	 *            文件路径集合
	 * @return
	 */
	public void excute(final String actionUrl, final Map<String, String> paramMap, final Map<String, String> map,
			final UploadListener listener) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case SUCCESS:
					if (listener != null) {
						listener.Success(msg.obj.toString());
					}
					break;
				case FAIL:
					if (listener != null) {
						listener.Fail(msg.obj.toString());
					}
					break;
				default:
					break;
				}
			}
		};
		new Thread(new Runnable() {

			@Override
			public void run() {
				HttpURLConnection con = null;
				InputStream is = null;
				DataOutputStream ds = null;
				FileInputStream fStream = null;
//				Map<String, String> returnMap = new HashMap<String, String>();
				Log.i(TAG, "uploadUrl = " + actionUrl + ";uploadFile=" + map);
				if (!PhoneConnectionUtil.isNetworkAvailable(context)) {
					Log.e(TAG, "connnect fail");
					Message message = new Message();
					message.what = FAIL;
					message.obj = "网络连接失败！";
					handler.sendMessage(message);
					return;
				}
				String end = "\r\n";
				String twoHyphens = "--";
				String boundary = "*****";// 分割线
				String CHARSET = "UTF-8";
				List<String> keys = new ArrayList<String>();
				URL url;
				try {
					url = new URL(actionUrl);
					con = HttpClientUtil.getNewHttpURLConnection(url, context);

					con.setDoInput(true);// 允许输入流
					con.setDoOutput(true);// 允许输出流
					con.setUseCaches(false);// 是否缓存
					con.setConnectTimeout(SET_CONNECTION_TIMEOUT);
					con.setReadTimeout(SET_SOCKET_TIMEOUT);
					con.setRequestMethod("POST");// 请求方式
					con.setRequestProperty("Connection", "Keep-Alive");// 设置维持长连接
					con.setRequestProperty("Charset", "UTF-8");// 设置字符集
					con.setRequestProperty("Content-Type", "multipart/form-data;name=\"f\"; boundary=" + boundary);// 设置文件类型
					ds = new DataOutputStream(con.getOutputStream());
					if (paramMap.size() != 0) {
						// 传递参数参数
						StringBuffer sb = new StringBuffer();
						for (Map.Entry<String, String> entry : paramMap.entrySet()) {
							sb.append(twoHyphens + boundary + "\r\n");
							sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n");
							sb.append(entry.getValue());
							sb.append("\r\n");
						}
						ds.write(sb.toString().getBytes());
					}
					if (map.size() == 0) {
						Message message = new Message();
						message.what = FAIL;
						message.obj = "上传图片为空！";
						handler.sendMessage(message);
						return;
					}
					// 文本类型数据
					for (String key : map.keySet()) {
						keys.add(key);
						String fileName = "";
						if (map.get(key).toString() != null && map.get(key).toString().lastIndexOf(LibIOUtil.FS) > -1)
							fileName = map.get(key).toString()
									.substring(map.get(key).toString().lastIndexOf(LibIOUtil.FS) + 1);
						StringBuffer params = new StringBuffer();
						params.append(twoHyphens + boundary + end);
						params.append("Content-Disposition: form-data; type=\"file\";name=\"" + key
								+ "\";multiple=\"multiple\";filename=\"" + fileName + "\"" + end);
						params.append("Content-Type: text/plain; charset=" + CHARSET + end);
						params.append(end);
						params.append(fileName);
						params.append(end);
						ds.write(params.toString().getBytes());
					}
					// 发送文件数据
					for (int i = 0; i < keys.size(); i++) {
						String name = map.get(keys.get(i)).toString();
						String fileName = "";
						if (name != null && name.lastIndexOf(LibIOUtil.FS) > -1)
							fileName = name.substring(name.lastIndexOf(LibIOUtil.FS) + 1);
						StringBuffer params = new StringBuffer();
						params.append(twoHyphens + boundary + end);
						params.append("Content-Disposition: form-data; name=\"" + keys.get(i) + "\"; filename=\""
								+ fileName + "\"" + end);
						params.append("Content-Type: application/octet-stream; charset=" + CHARSET + end);
						params.append(end);
						ds.write(params.toString().getBytes());
						File file = new File(name);
						fStream = new FileInputStream(file);
						int bufferSize = 1024;
						byte[] buffer = new byte[bufferSize];

						int length = -1;
						while ((length = fStream.read(buffer)) != -1) {
							Log.i(TAG, "<<<<<<<<<<<<<<<<<<<<<" + buffer.toString());
							ds.write(buffer, 0, length);
						}
						ds.writeBytes(end);
						fStream.close();
					}
					ds.writeBytes(twoHyphens + boundary + twoHyphens + end);// 数据传完标志
					ds.flush();
					int res = con.getResponseCode();// 获取响应码 code=200 成功
					if (res != 200) {
						Message message = new Message();
						message.what = FAIL;
						message.obj = "文件上传失败";
						handler.sendMessage(message);
						return;
					}
					is = con.getInputStream();// 获取响应流
					String jsonStr = LibIOUtil.convertStreamToJson(is);
					Log.e(TAG, res + "<<<<<<<<<<<@@@@<<<<<<<jsonStr  = " + jsonStr);
					if (TextUtils.isEmpty(jsonStr)) {
						Message message = new Message();
						message.what = FAIL;
						message.obj = "文件上传失败";
						handler.sendMessage(message);
						return;
					}
//					// 将返回数据转成Map集合，可根据实际情况转换
//					returnMap = new Gson().fromJson(jsonStr, new TypeToken<Map<String, String>>() {
//					}.getType());
					// 解析失败
					// 解析成功
					Message message = new Message();
					message.what = SUCCESS;
					message.obj = jsonStr;
					handler.sendMessage(message);
				} catch (MalformedURLException e) {
					e.printStackTrace();
					Message message = new Message();
					message.what = FAIL;
					message.obj = "网络异常";
					handler.sendMessage(message);
					return;
				} catch (ProtocolException e) {
					e.printStackTrace();
					Message message = new Message();
					message.what = FAIL;
					message.obj = "网络连接失败";
					handler.sendMessage(message);
					return;
				} catch (IOException e) {
					e.printStackTrace();
					Message message = new Message();
					message.what = FAIL;
					message.obj = "文件上传失败";
					handler.sendMessage(message);
					return;
				} finally {
					try {
						if (is != null) {
							is.close();
						}
						if (fStream != null) {
							fStream.close();
						}
						if (ds != null) {
							ds.flush();
							ds.close();
						}
						if (con != null) {
							con.disconnect();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public interface UploadListener {
		public void Success(String jsonStr);

		public void Fail(String failInfo);
	}
}
