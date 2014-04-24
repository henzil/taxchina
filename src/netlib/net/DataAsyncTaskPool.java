package netlib.net;

public class DataAsyncTaskPool {

	protected BaseDataAsyncTask previousAsyncTask;

	protected BaseDataAsyncTask currAsyncTask;

	public DataAsyncTaskPool() {
		super();
	}

	public void execute(BaseDataAsyncTask asyncTask, Object... params) {
		if (previousAsyncTask != null) {
			previousAsyncTask.cancel(true);
			previousAsyncTask = null;
		}
		previousAsyncTask = currAsyncTask;
		currAsyncTask = asyncTask;// 责任链模式
		currAsyncTask.execute(params);
	}

}
