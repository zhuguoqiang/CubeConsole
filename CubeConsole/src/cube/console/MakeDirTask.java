package cube.console;

import org.json.JSONException;
import org.json.JSONObject;

import net.cellcloud.core.Cellet;
import net.cellcloud.talk.dialect.ActionDialect;
import net.cellcloud.common.Logger;

public class MakeDirTask extends BaseTask {
	String dirPath = null;
	String tag = null;
	Cellet cellet = null;
	
	public MakeDirTask(Cellet cellet, String path, String tag) {
		super();
		this.cellet = cellet;
		this.dirPath = path;
		this.tag = tag;
	}
	
	public String getTag() {
		return this.tag;
	}

	public void setDirPath(String path) {
		this.dirPath = path;
	}

	public String getDirPath() {
		return this.dirPath;
	}
	
	public void mkdirOperation() {
		
//		// mkdir /../../../
//		String mkdirCmd =  "mkdir " + this.dirPath;
//		StringBuilder buf = new StringBuilder();
//		int mkExitVal = JavaExeLinuxCmd.exec(new String[] { "/bin/sh", "-c", mkdirCmd },
//				null, null, buf);
//		if (0 == mkExitVal) {
//			Logger.d(getClass(), "Commod : " + mkdirCmd + "exe successed");
//		}
//		else {
//			Logger.d(getClass(), "Commod : " + mkdirCmd + "exe failed");
//		}
//		
//		//chmod 777 /home/lztxhost/
//		String chmodCmd = "chmod 777 " + this.dirPath;
//		StringBuilder buffer = new StringBuilder();
//		int chmodExitVal = JavaExeLinuxCmd.exec(new String[] { "/bin/sh", "-c", chmodCmd },
//				null, null, buffer);
//		
//		if (0 == chmodExitVal) {
//			Logger.d(getClass(), "Commod : " + chmodCmd + "exe successed");
//		}
//		else {
//			Logger.d(getClass(), "Commod : " + chmodCmd + "exe failed");
//		}
//		
//		// 返回任务状态
//		if (null != tag) {
//			ActionDialect ad = new ActionDialect();
//			ad.setAction(CubeConsoleAPI.ACTION_MKDIR_ACK);
//
//			JSONObject json = new JSONObject();
//			try {
//				json.put("state", "200");
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			ad.appendParam("data", json);
//			// 发送数据
//			cellet.talk(tag, ad);
//		}
	}
}
