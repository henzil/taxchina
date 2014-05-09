package netlib.net;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import netlib.constant.BaseApiConstant;
import netlib.constant.BaseReturnCodeConstant;
import netlib.util.LibIOUtil;
import netlib.util.PhoneConnectionUtil;
import netlib.util.SettingUtil;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * 
 * @author henzil
 * @E-mail lizhen@dns.com.cn
 * @version create time:2013-7-24_下午9:26:19
 * @Description 请求网络最基础的工具，只管网络请求。
 */

@SuppressLint("DefaultLocale")
public class HttpClientUtil {

	private final static String TAG = "HttpClientUtil";

	private static final int SET_CONNECTION_TIMEOUT = 10000;
	private static final int SET_SOCKET_TIMEOUT = 20000;

	// HttpClient以post方式请求数据
	public static String doPostRequest(String urlString, HashMap<String, String> params, Context context) {
		String result = "";
		Log.e("tag", "开始执行网络请求-----");
		String tempURL = urlString + "?";
		int connectionTimeout = -1;
		int socketTimeout = -1;
		if (params != null && !params.isEmpty()) {
			if (params.get(BaseApiConstant.CONNECTION_TIMEOUT) != null) {
				connectionTimeout = Integer.parseInt(params.get(BaseApiConstant.CONNECTION_TIMEOUT));
			}
			if (params.get(BaseApiConstant.SOCKET_TIMEOUT) != null) {
				socketTimeout = Integer.parseInt(params.get(BaseApiConstant.SOCKET_TIMEOUT));
			}
		}
		if (!PhoneConnectionUtil.isNetworkAvailable(context)) {
			Log.i(TAG, "connnect fail");
			return BaseReturnCodeConstant.CONNECTION_FAIL;
		}
		DefaultHttpClient client = getNewHttpClient(context, connectionTimeout, socketTimeout);
		HttpUriRequest request = null;
		HttpPost httpPost = new HttpPost(urlString);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			if (params != null && !params.isEmpty()) {
				for (String key : params.keySet()) {
					nameValuePairs.add(new BasicNameValuePair(key, params.get(key)));
					tempURL = tempURL + key + "=" + params.get(key) + "&";
				}
			}
			Log.i(TAG, "json request url = " + tempURL);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
			request = httpPost;
//			HttpResponse response = client.execute(request, localContext);
			HttpResponse response = client.execute(request);
			StatusLine status = response.getStatusLine();
			int statusCode = status.getStatusCode();
			if (statusCode == 200) {
				result = read(response);
				Log.v("tag", "2");
				Log.i(TAG, "the client result jsonStr = " + result);
//				if (params.get("command").equals("register") || params.get("command").equals("login")||params.get("command").equals("thirdRegister")) {
//					// 如果是登录或者是注册，成功返回数据时，保存cookie
//					getCookie(context, client);
//				}

				return result == null ? BaseReturnCodeConstant.OTHEREXCEPTION : result;
			} else {
				return BaseReturnCodeConstant.CONNECTION_FAIL;
			}
		} catch (ClientProtocolException e) {
			return BaseReturnCodeConstant.CONNECTION_FAIL;
		} catch (UnknownHostException e) {
			return BaseReturnCodeConstant.CONNECTION_FAIL;
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
			return BaseReturnCodeConstant.TIME_OUT_EXCEPTION;
		} catch (SocketTimeoutException e) {
			return BaseReturnCodeConstant.TIME_OUT_EXCEPTION;
		} catch (Exception e) {
			e.printStackTrace();
			return BaseReturnCodeConstant.CONNECTION_FAIL;
		}
	}

	/**
	 * 获取标准 Cookie ，并存储
	 * 
	 * @param httpClient
	 */
//	private static void getCookie(Context context, DefaultHttpClient httpClient) {
//		List<Cookie> cookies = httpClient.getCookieStore().getCookies();
//		Log.d("cookie", "收到的cookies.size() = "+cookies.size());
//		StringBuffer sb = new StringBuffer();
//		for (int i = 0; i < cookies.size(); i++) {
//			Cookie cookie = cookies.get(i);
//			Log.d("cookie", "收到的cookie = "+cookie.toString());
//			sb.append(cookie.getName() + "~~~~" + cookie.getValue() + "~~~~" + cookie.getComment() + "~~~~"
//					+ cookie.getDomain() + "~~~~" + cookie.getPath() + "~~~~" + cookie.getVersion() + "####");
//			
//		}
//		if (sb.length() > 0) {
//			String co = sb.substring(0, sb.length() - 4);
//			Log.d("cookie", "储存cookie = "+co);
//			LoginUtil.setCookie(context, sb.toString());
//		}
//	}

	// HttpClient以get方式请求
	public static String doGetRequest(String urlString, HashMap<String, String> params, Context context) {
		String result = "";
		int connectionTimeout = -1;
		int socketTimeout = -1;
		if (params != null && !params.isEmpty()) {
			if (params.get(BaseApiConstant.CONNECTION_TIMEOUT) != null) {
				connectionTimeout = Integer.parseInt(params.get(BaseApiConstant.CONNECTION_TIMEOUT));
			}
			if (params.get(BaseApiConstant.SOCKET_TIMEOUT) != null) {
				socketTimeout = Integer.parseInt(params.get(BaseApiConstant.SOCKET_TIMEOUT));
			}
		}
		if (!PhoneConnectionUtil.isNetworkAvailable(context)) {
			Log.i(TAG, "connnect fail");
			return BaseReturnCodeConstant.CONNECTION_FAIL;
		}
		HttpClient client = getNewHttpClient(context, connectionTimeout, socketTimeout);
		HttpGet httpGet = new HttpGet(urlString);

		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine status = response.getStatusLine();
			int statusCode = status.getStatusCode();
			if (statusCode == 200) {
				result = read(response);
				return result == null ? "{}" : result;
			} else {
				return BaseReturnCodeConstant.CONNECTION_FAIL;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return BaseReturnCodeConstant.CONNECTION_FAIL;
		}
	}

	public static DefaultHttpClient getNewHttpClient(Context context, int connectionTimeout, int socketTimeout) {
		Cursor mCursor = null;
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			HttpParams params = new BasicHttpParams();
			if (connectionTimeout > 0) {
				HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);
			} else {
				HttpConnectionParams.setConnectionTimeout(params, SET_CONNECTION_TIMEOUT);
			}
			if (socketTimeout > 0) {
				HttpConnectionParams.setSoTimeout(params, socketTimeout);
			} else {
				HttpConnectionParams.setSoTimeout(params, SET_SOCKET_TIMEOUT);
			}
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			DefaultHttpClient client = new DefaultHttpClient(params);
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			if (!wifiManager.isWifiEnabled()) {
				// 获取当前正在使用的APN接入点
				Uri uri = Uri.parse("content://telephony/carriers/preferapn");
				mCursor = context.getContentResolver().query(uri, null, null, null, null);
				if (mCursor != null && mCursor.moveToFirst()) {
					// 游标移至第一条记录，当然也只有一条
					String proxyStr = mCursor.getString(mCursor.getColumnIndex("proxy"));
					if (proxyStr != null && proxyStr.trim().length() > 0) {
						HttpHost proxy = new HttpHost(proxyStr, 80);
						client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
					}
				}
			}
			return client;
		} catch (Exception e) {
			return new DefaultHttpClient();
		} finally {
			if (mCursor != null) {
				mCursor.close();
			}
		}
	}

	// 上传,json返回
	public static String uploadFile(String actionUrl, String uploadFile, Context context) {
		HttpURLConnection con = null;
		InputStream is = null;
		DataOutputStream ds = null;
		FileInputStream fStream = null;
		Log.i(TAG, "uploadUrl = " + actionUrl + ";uploadFile=" + uploadFile);
		if (!PhoneConnectionUtil.isNetworkAvailable(context)) {
			Log.i(TAG, "connnect fail");
			return BaseReturnCodeConstant.CONNECTION_FAIL;
		}
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		String fileName = "";
		if (uploadFile != null && uploadFile.lastIndexOf(LibIOUtil.FS) > -1)
			fileName = uploadFile.substring(uploadFile.lastIndexOf(LibIOUtil.FS) + 1);
		try {
			URL url = new URL(actionUrl);
			con = getNewHttpURLConnection(url, context);

			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setConnectTimeout(SET_CONNECTION_TIMEOUT);
			con.setReadTimeout(SET_SOCKET_TIMEOUT);
			con.setRequestMethod("POST");
			con.setRequestProperty("Connection", "Keep-Alive");// 设置维持长连接
			con.setRequestProperty("Charset", "UTF-8");// 设置字符集
			con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);// 设置文件类型
			ds = new DataOutputStream(con.getOutputStream());
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; " + "name=\"pushmessage\";filename=\"" + fileName + "\""
					+ end);
			/** 新添加的 **/
//			ds.writeBytes("Content-Type: application/octet-stream\r\n");
			ds.writeBytes("Content-Type: " + fileName.substring(fileName.indexOf("."), fileName.length()) + "\r\n");
			ds.writeBytes(end);

			fStream = new FileInputStream(uploadFile);
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];

			int length = -1;
			while ((length = fStream.read(buffer)) != -1) {
				ds.write(buffer, 0, length);
			}
			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

			ds.flush();
			is = con.getInputStream();
			String jsonStr = LibIOUtil.convertStreamToJson(is);
			Log.i(TAG, "jsonStr=" + jsonStr);
			if (TextUtils.isEmpty(jsonStr)) {
				return BaseReturnCodeConstant.OTHEREXCEPTION;
			}
			return jsonStr;

		} catch (ClientProtocolException e) {
			return BaseReturnCodeConstant.CONNECTION_FAIL;
		} catch (UnknownHostException e) {
			return BaseReturnCodeConstant.CONNECTION_FAIL;
		} catch (ConnectTimeoutException e) {
			Log.i(TAG, "SocketTimeoutException");
			return BaseReturnCodeConstant.TIME_OUT_EXCEPTION;
		} catch (SocketTimeoutException e) {
			Log.i(TAG, "SocketTimeoutException");
			return BaseReturnCodeConstant.TIME_OUT_EXCEPTION;
		} catch (Exception e) {
			e.printStackTrace();
			return BaseReturnCodeConstant.UPLOAD_IMAGE_FAIL_MSG;
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
	
	// 上传多个文件,json返回
//	public static String uploadFile(String actionUrl, Map<String, String> uploadFileMap, Context context) {
//		HttpURLConnection con = null;
//		InputStream is = null;
//		DataOutputStream ds = null;
//		FileInputStream fStream = null;
//		Log.i(TAG, "uploadUrl = " + actionUrl + ";uploadFileMap=" + uploadFileMap.toString());
//		if (!PhoneConnectionUtil.isNetworkAvailable(context)) {
//			Log.i(TAG, "connnect fail");
//			return BaseReturnCodeConstant.CONNECTION_FAIL;
//		}
//		String end = "\r\n";
//		String twoHyphens = "--";
//		String boundary = "*****";
////		String fileName = "";
////		if (uploadFile != null && uploadFile.lastIndexOf(LibIOUtil.FS) > -1)
////			fileName = uploadFile.substring(uploadFile.lastIndexOf(LibIOUtil.FS) + 1);
//		try {
//			URL url = new URL(actionUrl);
//			con = getNewHttpURLConnection(url, context);
//
//			con.setDoInput(true);
//			con.setDoOutput(true);
//			con.setUseCaches(false);
//			con.setConnectTimeout(SET_CONNECTION_TIMEOUT);
//			con.setReadTimeout(SET_SOCKET_TIMEOUT);
//			con.setRequestMethod("POST");
//			con.setRequestProperty("Connection", "Keep-Alive");// 设置维持长连接
//			con.setRequestProperty("Charset", "UTF-8");// 设置字符集
//			con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);// 设置文件类型
//			ds = new DataOutputStream(con.getOutputStream());
//			ds.writeBytes(twoHyphens + boundary + end);
////			ds.writeBytes("Content-Disposition: form-data; " + "name=\"f\""+ end);
//			ds.writeBytes("Content-Disposition: form-data; " + "name=\"f\";filename=\"" + fileName + "\""
//					+ end);
//			/** 新添加的 **/
////				ds.writeBytes("Content-Type: application/octet-stream\r\n");
//			ds.writeBytes("Content-Type: " + fileName.substring(fileName.indexOf("."), fileName.length()) + "\r\n");
//			ds.writeBytes(end);
//
//			fStream = new FileInputStream(uploadFile);
//			int bufferSize = 1024;
//			byte[] buffer = new byte[bufferSize];
//
//			int length = -1;
//			while ((length = fStream.read(buffer)) != -1) {
//				ds.write(buffer, 0, length);
//			}
//			ds.writeBytes(end);
//			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
//
//			ds.flush();
//			is = con.getInputStream();
//			String jsonStr = LibIOUtil.convertStreamToJson(is);
//			Log.i(TAG, "jsonStr=" + jsonStr);
//			if (TextUtils.isEmpty(jsonStr)) {
//				return BaseReturnCodeConstant.OTHEREXCEPTION;
//			}
//			return jsonStr;
//
//		} catch (ClientProtocolException e) {
//			return BaseReturnCodeConstant.CONNECTION_FAIL;
//		} catch (UnknownHostException e) {
//			return BaseReturnCodeConstant.CONNECTION_FAIL;
//		} catch (ConnectTimeoutException e) {
//			Log.i(TAG, "SocketTimeoutException");
//			return BaseReturnCodeConstant.TIME_OUT_EXCEPTION;
//		} catch (SocketTimeoutException e) {
//			Log.i(TAG, "SocketTimeoutException");
//			return BaseReturnCodeConstant.TIME_OUT_EXCEPTION;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return BaseReturnCodeConstant.UPLOAD_IMAGE_FAIL_MSG;
//		} finally {
//			try {
//				if (is != null) {
//					is.close();
//				}
//				if (fStream != null) {
//					fStream.close();
//				}
//				if (ds != null) {
//					ds.flush();
//					ds.close();
//				}
//				if (con != null) {
//					con.disconnect();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//		}
//	}

	private static String read(HttpResponse response) throws Exception {
		String result = "";
		HttpEntity entity = response.getEntity();
		InputStream inputStream = null;
		ByteArrayOutputStream content = null;
		try {
			inputStream = entity.getContent();
			content = new ByteArrayOutputStream();

			Header header = response.getFirstHeader("Content-Encoding");
			if (header != null && header.getValue().toLowerCase().indexOf("gzip") > -1) {
				inputStream = new GZIPInputStream(inputStream);
			}

			// Read response into a buffered stream
			int readBytes = 0;
			byte[] sBuffer = new byte[512];
			while ((readBytes = inputStream.read(sBuffer)) != -1) {
				content.write(sBuffer, 0, readBytes);
			}
			// Return result from buffered stream
			result = new String(content.toByteArray());
			return result;
		} catch (IllegalStateException e) {
			throw new Exception(e);
		} catch (IOException e) {
			throw new Exception(e);
		} finally {
			if (content != null) {
				content.flush();
				content.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

//	public static class MySSLSocketFactory extends SSLSocketFactory {
//		SSLContext sslContext = SSLContext.getInstance("TLS");
//
//		public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException,
//				KeyStoreException, UnrecoverableKeyException {
//			super(truststore);
//
//			TrustManager tm = new X509TrustManager() {
//				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//				}
//
//				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//				}
//
//				public X509Certificate[] getAcceptedIssuers() {
//					return null;
//				}
//			};
//
//			sslContext.init(null, new TrustManager[] { tm }, null);
//		}
//
//		@Override
//		public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException,
//				UnknownHostException {
//			return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
//		}
//
//		@Override
//		public Socket createSocket() throws IOException {
//			return sslContext.getSocketFactory().createSocket();
//		}
//	}

	private static HashMap<String, String> urlMap = new HashMap<String, String>();

	// 下载文件
	public static long downloadFile(String url, File f, Context context) {
		if (!urlMap.containsKey(url)) {
			urlMap.put(url, url);
		} else {
			while (urlMap.containsKey(url)) {

			}
			return f.length();
		}

		long fileLength = 0;
		URL myFileUrl = null;
		try {
			Log.i(TAG, "Image url is " + url);
			myFileUrl = new URL(url);
		} catch (Exception e) {
			return fileLength;
		}
		HttpURLConnection connection = null;
		InputStream is = null;
		FileOutputStream fo = null;
		try {
			connection = getDownHttpURLConnection(myFileUrl, context);
			connection.setDoInput(true);
			connection.connect();

			is = connection.getInputStream();
			fileLength = connection.getContentLength();
//			if(f.exists()){
//				return;
//			}
			if (f.createNewFile()) {
				Log.i(TAG, "f.getAbsolutePath() = " + f.getAbsolutePath());
				fo = new FileOutputStream(f);
				byte[] buffer = new byte[256];
				int size;
				while ((size = is.read(buffer)) > 0) {
					fo.write(buffer, 0, size);
				}
			}
		} catch (MalformedURLException e) {
			Log.i(TAG, "URL is format error");
		} catch (IOException e) {
			e.printStackTrace();
			Log.i(TAG, "IO error when download file");
			Log.i(TAG, "The URL is " + url + ";the file name " + f.getName());
		} finally {
			try {
				if (fo != null) {
					fo.flush();
					fo.close();
				}
				if (is != null) {
					is.close();
				}
				if (connection != null) {
					connection.disconnect();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			urlMap.remove(url);
		}
		return fileLength;
	}

	public static HttpURLConnection getDownHttpURLConnection(URL url, Context context) {
		HttpURLConnection connection = null;
		Cursor mCursor = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			Log.e("tag", "wifiManager.isWifiEnabled() = " + wifiManager.isWifiEnabled());
			if (!wifiManager.isWifiEnabled()) {
				if (SettingUtil.getWifiImage(context)) {
					// 设置非wifi下不下载图片之后，返回null。
					return null;
				} else {
					// 获取当前正在使用的APN接入点
					Uri uri = Uri.parse("content://telephony/carriers/preferapn");
					mCursor = context.getContentResolver().query(uri, null, null, null, null);
					if (mCursor != null && mCursor.moveToFirst()) {
						// 游标移至第一条记录，当然也只有一条
						String proxyStr = mCursor.getString(mCursor.getColumnIndex("proxy"));
						if (proxyStr != null && proxyStr.trim().length() > 0) {
							Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxyStr, 80));
							connection = (HttpURLConnection) url.openConnection(proxy);
						}
					}
				}
			}
			return connection;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				return (HttpURLConnection) url.openConnection();
			} catch (IOException e1) {
				e1.printStackTrace();
				return null;
			}
		} finally {
			if (mCursor != null) {
				mCursor.close();
			}
		}

	}

	public static HttpURLConnection getNewHttpURLConnection(URL url, Context context) {
		HttpURLConnection connection = null;
		Cursor mCursor = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			Log.e("tag", "wifiManager.isWifiEnabled() = " + wifiManager.isWifiEnabled());
			if (!wifiManager.isWifiEnabled()) {
				// 获取当前正在使用的APN接入点
				Uri uri = Uri.parse("content://telephony/carriers/preferapn");
				mCursor = context.getContentResolver().query(uri, null, null, null, null);
				if (mCursor != null && mCursor.moveToFirst()) {
					// 游标移至第一条记录，当然也只有一条
					String proxyStr = mCursor.getString(mCursor.getColumnIndex("proxy"));
					if (proxyStr != null && proxyStr.trim().length() > 0) {
						Proxy proxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxyStr, 80));
						connection = (HttpURLConnection) url.openConnection(proxy);
					}
				}
			}
			return connection;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				return (HttpURLConnection) url.openConnection();
			} catch (IOException e1) {
				e1.printStackTrace();
				return null;
			}
		} finally {
			if (mCursor != null) {
				mCursor.close();
			}
		}

	}
}
