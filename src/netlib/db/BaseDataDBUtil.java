package netlib.db;


import netlib.constant.DataSqlDBConstant;
import android.content.Context;

public class BaseDataDBUtil extends BaseDBUtil implements DataSqlDBConstant {

	protected Context context;

	public BaseDataDBUtil(Context context) {
		super();
		initDataInConStructors(context);
	}

	@Override
	protected void initDataInConStructors(Context context) {
		autogenDBOpenHelper = new DataSQLiteOpenHelper(context, DATABASE_NAME, DB_VERSION);
	}

}
