package com.Cube.Console;

import java.util.LinkedList;
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
		synchronized(this.mutex){
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
						String filePrefix = null;
						String stringData = dialect.getParamAsString("data");
						JSONObject data = new JSONObject(stringData);
						if(data.has("filePath")){
							filePath = data.getString("filePath");
						}
						
						if(data.has("filePrefix")){
							filePrefix = data.getString("filePrefix");
						}
						String tag = dialect.getOwnerTag();
						//创建任务， 入队列
						ConvertTask task = new ConvertTask(filePath, filePrefix, tag);
						
						synchronized(mutex){
							
							taskQueue.offer(task);
							task.state = StateCode.Queueing;
							threadTask.spinning = true;
				
							mutex.notify();
							
						}
						//返回任务状态
						if (null != tag) {
							ActionDialect ad = new ActionDialect();
							ad.setAction(CubeConsoleAPI.ACTION_CONVERT_STATE);

							JSONObject value = new JSONObject();
							value.put("state", task.state.getCode());

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
