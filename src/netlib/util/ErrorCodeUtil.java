package netlib.util;

import netlib.constant.BaseReturnCodeConstant;
import netlib.model.ErrorModel;
import android.content.Context;
import android.text.TextUtils;

public class ErrorCodeUtil implements BaseReturnCodeConstant {

	public static String convertErrorCode(Context context, String errorMsg) {
		String warnMsg = "";
		if (TextUtils.isEmpty(errorMsg)) {
			return "";
		}
		if (errorMsg.equals(CONNECTION_FAIL)) {
			warnMsg = "网络连接失败！";
		} else if (errorMsg.equals(UPLOAD_IMAGE_FAIL_MSG)) {
			warnMsg = "图片上传失败！";
		} else if (errorMsg.equals(TIME_OUT_EXCEPTION)) {
			warnMsg = "网络连接超时！";
		} else if (errorMsg.equals(OTHEREXCEPTION)) {
			warnMsg = "未知错误！";
		} else if (errorMsg.equals(EXCEPTION)) {
			warnMsg = "数据解析异常！";
		} else {
			warnMsg = errorMsg;
		}
		return warnMsg;
	}

	public static ErrorModel getError(String msg) {
		ErrorModel errorModel = new ErrorModel();
		if (TextUtils.isEmpty(msg)) {
			errorModel.setErrorCode(EXCEPTION);
			return errorModel;
		}

		if (msg.equals(CONNECTION_FAIL)) {
			errorModel.setErrorCode(CONNECTION_FAIL);
		} else if (msg.equals(UPLOAD_IMAGE_FAIL_MSG)) {
			errorModel.setErrorCode(UPLOAD_IMAGE_FAIL_MSG);
		} else if (msg.equals(TIME_OUT_EXCEPTION)) {
			errorModel.setErrorCode(TIME_OUT_EXCEPTION);
		} else if (msg.equals(OTHEREXCEPTION)) {
			errorModel.setErrorCode(OTHEREXCEPTION);
		} else if (msg.equals(EXCEPTION)) {
			errorModel.setErrorCode(EXCEPTION);
		} else {
			errorModel.setErrorCode(EXCEPTION);
		}
		return errorModel;
	}

	public static boolean isError(String msg) {
		if (msg.equals(CONNECTION_FAIL)) {
			return true;
		} else if (msg.equals(UPLOAD_IMAGE_FAIL_MSG)) {
			return true;
		} else if (msg.equals(TIME_OUT_EXCEPTION)) {
			return true;
		} else if (msg.equals(OTHEREXCEPTION)) {
			return true;
		} else if (msg.equals(EXCEPTION)) {
			return true;
		}
		return false;
	}
}
