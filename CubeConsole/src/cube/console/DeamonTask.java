package cube.console;

import java.util.Queue;

public class DeamonTask extends Thread {
	boolean spinning = true;
	String tag = null;

	private Queue<BaseTask> taskQueue = null;
	
	public DeamonTask(Queue<BaseTask> taskQueue) {
		super();
		this.taskQueue = taskQueue;
	}

	public void shutdown() {
		this.spinning = false;
	}

	@Override
	public void run() {
		while (spinning) {
			try{
				Thread.sleep(1000);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}

			BaseTask baseTask = null;
			synchronized (this.taskQueue){
				if (!this.taskQueue.isEmpty()) {
					baseTask = taskQueue.poll();	
				}	
			}

			if (null != baseTask) {
				if (baseTask instanceof ConvertTask) {
					ConvertTask task = (ConvertTask)baseTask;
					task.fireConvert();
				}
				else if (baseTask instanceof MakeDirTask) {
					MakeDirTask mkTask = (MakeDirTask)baseTask;
					mkTask.mkdirOperation();
				}
			}			
		}
	}
}
