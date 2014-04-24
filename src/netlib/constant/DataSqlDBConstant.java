package netlib.constant;

public interface DataSqlDBConstant extends DataTableDBConstant{
	
	public static final String NEW_TABLE_DATA = "CREATE TABLE IF NOT EXISTS "
			+ T_DATA + "("
			+ DATA_ID + " integer primary key autoincrement,"
			+ DATA_TAG + " VARCHAR(30),"
			+ DATA_URL + " TEXT,"
			+ DATA_JSON_STR + " TEXT);";
	
	public static final String DATA_WHERE = DATA_TAG + " =? AND " + DATA_URL + " =?";
	
	public static final String DATA_URL_WHERE = DATA_URL + " =?";
	
}
