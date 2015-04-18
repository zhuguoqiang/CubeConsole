package com.Cube.Console;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.cellcloud.core.Cellet;
import net.cellcloud.talk.dialect.ActionDelegate;
import net.cellcloud.talk.dialect.ActionDialect;

import org.json.JSONException;
import org.json.JSONObject;

public class Dispatcher {

	private Cellet cellet;
	DeamonTask threadTask = null;
	private Object mutex = new Object();
	boolean spinning = false;
	private Queue<ConvertTask> taskQueue = new LinkedList<ConvertTask>();

	public Dispatcher(Cellet cellet) {
		this.cellet = cellet;
	}

	public void startup() {
		threadTask = new DeamonTask(this.cellet, mutex, taskQueue);
		threadTask.start();
	}

	public void stop() {
		synchronized (this.mutex) {
			mutex.notifyAll();
		}
		threadTask = null;
	}

	public void dispatch(ActionDialect dialect) {
		String action = dialect.getAction();

		if (action.equals(CubeConsoleAPI.ACTION_CONVERT)) {

			dialect.act(new ActionDelegate() {
				@Override
				public void doAction(ActionDialect dialect) {
					try {
						String filePath = null;
						String targetPath = null;
						String taskTag = null;
						String stringData = dialect.getParamAsString("data");
						JSONObject data = new JSONObject(stringData);
						if (data.has("filePath")) {
							filePath = data.getString("filePath");
						}

						if (data.has("targetPath")) {
							targetPath = data.getString("targetPath");
						}

						if (data.has("taskTag")) {
							taskTag = data.getString("taskTag");

						}
						String tag = dialect.getOwnerTag();
						// 创建任务， 入队列
						ConvertTask task = new ConvertTask(filePath,
								targetPath, tag, taskTag);

						synchronized (mutex) {

							taskQueue.offer(task);
							task.state = StateCode.Queueing;
							threadTask.spinning = true;

							mutex.notify();

						}
						// 返回任务状态
						if (null != tag) {
							ActionDialect ad = new ActionDialect();
							ad.setAction(CubeConsoleAPI.ACTION_CONVERT_STATE);

							JSONObject value = new JSONObject();
							value.put("state", task.state.getCode());
							value.put("filePath", task.getFilePath());
							value.put("targetPath", task.getTargetFilePath());
							value.put("taskTag", task.getTaskTag());

							ad.appendParam("data", value);
							// 发送数据
							cellet.talk(tag, ad);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		} else if (action.equals(CubeConsoleAPI.ACTION_REQUEST_CONVERTED_FILE)) {
			dialect.act(new ActionDelegate() {
				@Override
				public void doAction(ActionDialect dialect) {
					try {
						String filePath = null;
						String targetPath = null;
						String taskTag = null;
						String stringData = dialect.getParamAsString("data");
						JSONObject data = new JSONObject(stringData);

						if (data.has("filePath")) {
							filePath = data.getString("filePath");
						}

						if (data.has("targetPath")) {
							targetPath = data.getString("targetPath");
						}

						if (data.has("taskTag")) {
							taskTag = data.getString("taskTag");
						}
						String tag = dialect.getOwnerTag();
						
						ConvertTask task = new ConvertTask(filePath,
								targetPath, tag, taskTag);

						//1 查找文件 find -name "*.png" | grep -H "MCE"
						String cmd = "find -name '*." + task.getFileExtension()
								+ "' | grep -H " + "'" + task.getFilePrefix() + "'";
						System.out.println(this.getClass() + " : " + cmd);
						String fileNames = JavaExeLinuxCmd.execut(
								new String[] { "/bin/sh", "-c", cmd }, null,
								null).toString();
						
						//2 提取转换后文件路径
						List<String> fileArray = new ArrayList();
						if (null != fileNames) {
							String[] names = fileNames.split("\n");
							for (String fileName : names) {
								int index = fileName.lastIndexOf("/");
								String subStr = fileName.substring(index + 1, fileName.length());
								fileArray.add(subStr);
							}
						}
						
						//3 移动转换后的文件到工作目录  mv -t /opt/soft/test/test4/ log1.txt log2.txt log3.txt
						int endIndex = filePath.lastIndexOf("/");
						String dirPath = filePath.substring(0, endIndex);
						// dirPath：  /home/lztxhost/Documents/
						// targetPath： /workspace/CubeCloud/assets/images/name/
						List<String> convertedFileArray = new ArrayList();
						for (String name: fileArray){
							String pngPath = dirPath + name;
							convertedFileArray.add(pngPath);
							String moveCmd = "mv -t " + targetPath +" " + pngPath;
							System.out.println(this.getClass() + " MOVE_PNG_FILE : " + moveCmd);
							String r = JavaExeLinuxCmd.execut(
									new String[] { "/bin/sh", "-c", moveCmd}, null,
									null).toString();
						}
						// 返回任务状态
						if (null != tag) {
							ActionDialect ad = new ActionDialect();
							ad.setAction(CubeConsoleAPI.ACTION_REQUEST_CONVERTED_FILE_RESULT);

							JSONObject value = new JSONObject();
							value.put("filePath", filePath);
							value.put("targetPath", targetPath);
							value.put("convertedFiles", convertedFileArray);
							value.put("taskTag", taskTag);

							ad.appendParam("data", value);
							// 发送数据
							cellet.talk(tag, ad);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

}
