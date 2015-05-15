package cube.console;

import java.util.LinkedList;
import java.util.Queue;

import net.cellcloud.core.Cellet;
import net.cellcloud.talk.dialect.ActionDialect;

import org.json.JSONException;
import org.json.JSONObject;

public class Dispatcher {
	private Cellet cellet;
	private DeamonTask threadTask = null;
	private Queue<BaseTask> taskQueue = new LinkedList<BaseTask>();

	public Dispatcher(Cellet cellet) {
		this.cellet = cellet;
	}

	public void startup() {
		threadTask = new DeamonTask(taskQueue);
		threadTask.start();
	}

	public void stop() {

		threadTask.shutdown();
		threadTask = null;
	}

	public void dispatch(ActionDialect dialect) {
		String action = dialect.getAction();
		if (action.equals(CubeConsoleAPI.ACTION_CONVERT)) {
			try {
				String filePath = null;
				String outPutPath = null;
				String taskTag = null;
				String stringData = dialect.getParamAsString("data");
				JSONObject data = new JSONObject(stringData);
				if (data.has("filePath")) {
					filePath = data.getString("filePath");
				}
				if (data.has("outPut")) {
					outPutPath = data.getString("outPut");
				}
				if (data.has("taskTag")) {
					taskTag = data.getString("taskTag");
				}
				String tag = dialect.getOwnerTag();

				// 创建任务， 入队列
				ConvertTask task = new ConvertTask(cellet, filePath, outPutPath, tag, taskTag);

				synchronized (taskQueue) {
					taskQueue.offer(task);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} 
		else if (action.equals(CubeConsoleAPI.ACTION_MKDIR)) {
			try {
				String dirPath = null;
				String stringData = dialect.getParamAsString("data");
				JSONObject data = new JSONObject(stringData);
				if (data.has("dirPath")) {
					dirPath = data.getString("dirPath");
				}
				String tag = dialect.getOwnerTag();
				
				// 创建任务， 入队列
				MakeDirTask task = new MakeDirTask(cellet, dirPath, tag);

				synchronized (taskQueue) {
					taskQueue.offer(task);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} 
	}
}
