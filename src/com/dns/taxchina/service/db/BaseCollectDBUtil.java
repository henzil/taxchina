package com.dns.taxchina.service.db;

import netlib.db.BaseDBUtil;
import android.content.Context;

import com.dns.taxchina.service.db.constant.MyCollectDBConstant;

/**
 * @author fubiao
 * @version create time:2014-3-19_上午11:50:28
 * @Description TODO
 */
public class BaseCollectDBUtil extends BaseDBUtil implements MyCollectDBConstant {
	
	protected Context context;
	protected String imei;
	
	public BaseCollectDBUtil(Context context, String imei){
		super();
		this.context = context;
		this.imei = imei;
		initDataInConStructors(context);
	}

	@Override
	protected void initDataInConStructors(Context arg0) {
		autogenDBOpenHelper = new CollectSQLiteOpenHelper(arg0, (imei + DATABASE_NAME), DB_VERSION);

	}
}