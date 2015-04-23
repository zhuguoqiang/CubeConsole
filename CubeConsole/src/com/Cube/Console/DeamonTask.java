package com.Cube.Console;

import java.util.List;
import java.util.Queue;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import net.cellcloud.core.Cellet;
import net.cellcloud.talk.dialect.ActionDialect;

public class DeamonTask extends Thread {

	private Cellet cellet = null;
	boolean spinning = true;
	String tag = null;

	private Queue<ConvertTask> taskQueue = null;
	private Object mutex = null;
	
	public DeamonTask(Cellet cellet, Object mutex, Queue<ConvertTask> taskQueue) {
		super();
		this.cellet = cellet;
		this.mutex = mutex;
		this.taskQueue = taskQueue;
	}

	@Override
	public void run() {

		synchronized (this.mutex) {
			while (spinning) {
				if (this.taskQueue.isEmpty()) {
					try {
						this.mutex.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					ConvertTask task = taskQueue.poll();
					String tag = task.getTag();
					task.state = StateCode.Started;
					
					// 回传转换状态,开始转换
					ActionDialect ad = convertActionDialec(task);
					this.cellet.talk(tag, ad);
					
					List<String> urls = null;
					
					String superType =  FileType.parseFileSuperType(task.getFileType());
					if (superType.equals("image")) {
						//移动
					}
					else if (superType.equals("office")) {
						//转换，移动
						task.unoconvOperation(this.cellet);
						task.pdftoppmOperation(this.cellet);
					}
					else if (superType.equals("pdf")) {
						//pdftoppm
						task.pdftoppmOperation(this.cellet);
					}
					else if (superType.equals("audio")) {
						//暂不处理
					}
					else if (superType.equals("vedio")) {
						//暂不处理
					}
					else if (superType.equals("")) {
						//暂不处理
					}
					// 开始转换
//					task.convert(this.cellet);

					// 将转换后的文件移动到工作目录
					urls = task.moveFileToWorkspace(this.cellet);

					// 回传转换状态,转换结束，返回urls
				
					if (null != tag) {
						ActionDialect dialect = new ActionDialect();
						dialect.setAction(CubeConsoleAPI.ACTION_CONVERT_STATE);
						JSONObject value = new JSONObject();
						
						
						
						try {
							
							String error = urls.get(0);
							if (error.equals("404")) {
								urls.remove(0);
								task.state = StateCode.Failed;
								value.put("faileCode", "404");
							}
							else {
								task.state = StateCode.Successed;
							}
							
							JSONArray jsonArray = new JSONArray(urls);
							value.put("state", task.state.getCode());
							value.put("filePath", task.getFilePath());
							value.put("subPath", task.getSubPath());
							value.put("convertedFileUrls", jsonArray);
							value.put("taskTag", task.getTaskTag());
						} catch (JSONException e) {
							e.printStackTrace();
						}
						dialect.appendParam("data", value);
						// 发送数据
						cellet.talk(tag, dialect);
					}
				}
			}
		}
	}

	private ActionDialect convertActionDialec(ConvertTask task) {
		int state = task.state.getCode();
		ActionDialect ad = new ActionDialect();
		ad.setAction(CubeConsoleAPI.ACTION_CONVERT_STATE);
		JSONObject jo = new JSONObject();
		try {
			jo.put("state", state);
			jo.put("filePath", task.getFilePath());
			jo.put("filePrefix", task.getFilePrefix());
			jo.put("fileExtension", task.getFileExtension());
			jo.put("subPath", task.getSubPath());
			jo.put("taskTag", task.getTaskTag());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ad.appendParam("data", jo);
		return ad;
	}
}
