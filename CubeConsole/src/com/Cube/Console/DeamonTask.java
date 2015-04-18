package com.Cube.Console;

import java.util.Queue;

import org.json.JSONException;
import org.json.JSONObject;

import net.cellcloud.core.Cellet;
import net.cellcloud.talk.dialect.ActionDialect;

public class DeamonTask extends Thread{

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

		synchronized(this.mutex){
			while(spinning){
				if(this.taskQueue.isEmpty()){
					try {
						this.mutex.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}else{
					ConvertTask task = taskQueue.poll();
					String tag = task.getTag();
					task.state = StateCode.Executing;
					//回传转换状态
					ActionDialect ad = convertActionDialec(task);
					this.cellet.talk(tag, ad);
					//开始转换
					task.convert();
					//回传转换状态
					ActionDialect dialect = convertActionDialec(task);
					this.cellet.talk(tag, dialect);
				}
			}
		}
	}
	
	private ActionDialect convertActionDialec(ConvertTask task){
		int state = task.state.getCode();
		ActionDialect ad = new ActionDialect();
		ad.setAction(CubeConsoleAPI.ACTION_CONVERT_STATE);
		JSONObject jo = new JSONObject();
		try {
			jo.put("state", state);
			jo.put("filePath", task.getFilePath());
			jo.put("filePrefix", task.getFilePrefix());
			jo.put("fileExtension", task.getFileExtension());
			jo.put("targetPath", task.getTargetFilePath());
			jo.put("taskTag", task.getTaskTag());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ad.appendParam("data", jo);
		
		return ad;
	}

}
