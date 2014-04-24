package netlib.helper;

public interface DataServiceHelper {

	void preExecute();

	void postExecute(String TAG, Object result, Object... params);

	Object doInBackground(Object... params);

}
