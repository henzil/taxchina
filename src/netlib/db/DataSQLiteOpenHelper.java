package netlib.db;

import netlib.constant.DataSqlDBConstant;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataSQLiteOpenHelper extends SQLiteOpenHelper {

	public DataSQLiteOpenHelper(Context context, String name, int version) {
		super(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DataSqlDBConstant.NEW_TABLE_DATA);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.delete(DataSqlDBConstant.T_DATA, null, null);
		onCreate(db);
	}

}
