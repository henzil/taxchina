package netlib.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class BaseDBUtil {

	protected SQLiteOpenHelper autogenDBOpenHelper;
	protected SQLiteDatabase writableDatabase;
	protected SQLiteDatabase readableDatabase;

	protected boolean isRowExisted(SQLiteDatabase database, String table, String column, long id) {
		Cursor c = null;
		try {
			c = database.query(table, null, column + "=" + id, null, null, null, null);
			if (c.getCount() > 0) {
				return true;
			}
		} catch (Exception e) {
			return false;
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return false;
	}

	protected boolean isRowExisted(SQLiteDatabase database, String table, String column, String id) {
		Cursor c = null;
		try {
			c = database.query(table, null, column + "='" + id + "'", null, null, null, null);
			if (c.getCount() > 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return false;
	}

	protected boolean isRowExisted(SQLiteDatabase database, String table, String selection, String[] selectArgs) {
		Cursor c = null;
		try {
			c = database.query(table, null, selection, selectArgs, null, null, null);
			if (c.getCount() > 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return false;
	}

	protected boolean removeAllEntries(String table) {
		try {
			openWriteableDB();
			writableDatabase.delete(table, null, null);
		} catch (Exception e) {
			return false;
		} finally {
			closeWriteableDB();
		}
		return true;
	}

	protected boolean tabbleIsExist(SQLiteDatabase database, String table) {
		boolean result = false;
		if (table == null) {
			return false;
		}
		Cursor c = null;
		try {
			String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='" + table.trim() + "' ";
			c = database.rawQuery(sql, null);
			if (c.moveToNext()) {
				int count = c.getInt(0);
				if (count > 0) {
					result = true;
				}
			}

		} catch (Exception e) {
			return false;
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return result;
	}

	protected synchronized void openWriteableDB() {
		writableDatabase = autogenDBOpenHelper.getWritableDatabase();
	}

	protected synchronized void openReadableDB() {
		readableDatabase = autogenDBOpenHelper.getReadableDatabase();
	}

	protected void closeWriteableDB() {
		try {
			autogenDBOpenHelper.close();
		} catch (Exception e) {
		}
		if (writableDatabase != null && writableDatabase.isOpen()) {
			try {
				writableDatabase.close();
				writableDatabase = null;
			} catch (Exception e) {

			}
		}
	}

	protected void closeReadableDB() {
		try {
			autogenDBOpenHelper.close();
		} catch (Exception e) {
		}
		if (readableDatabase != null && readableDatabase.isOpen()) {
			try {
				readableDatabase.close();
				readableDatabase = null;
			} catch (Exception e) {

			}
		}
	}

	protected abstract void initDataInConStructors(Context context);

}
