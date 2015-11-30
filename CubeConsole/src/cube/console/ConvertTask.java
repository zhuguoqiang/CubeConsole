package cube.console;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import net.cellcloud.common.Logger;
import net.cellcloud.core.Cellet;
import net.cellcloud.talk.dialect.ActionDialect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ConvertTask extends BaseTask {

	/*转换状态*/
	StateCode state;
	/*文件路径*/
	// /data/tomcat/webapps/ROOT/cubewhiteboard/shared/13245/xxxx.doc
	String filePath = null; 
	/*转换输出路径*/
	///data/tomcat/webapps/ROOT/cubewhiteboard/shared
	String outPutPath = null; 
	/*文件前缀*/
	String filePrefix = null;
	/*文件后缀*/
	String fileExtension = null;
	/*文件类型*/
	FileType fileType;
	/*文件子路径*/
	String subPath = null;
	/*转换任务标示*/
	String taskTag = null;
	/*cellet tag*/
	String tag = null;
	/*cellet*/
	Cellet cellet = null;
	final List<String> convertedFileList = new ArrayList<String>();
	
	/*转换工具工作路径*/
	// /data/cubeconsole
	private String convertWorkPath = null;
	
	private final String UNOCONV_PDF = "unoconv -f pdf";
	private final String SOFFICE_PDF = "soffice --headless --convert-to pdf";
	private final String PDFTOPPM_PNG = "pdftoppm -png";
	private final String CP = "cp -f ";
	private final String FILE_EXTENSION = "png";

	public ConvertTask(Cellet cellet, String filePath, String outPath, String tag, String taskTag) {
		super();
		this.cellet = cellet;
		this.filePath = filePath;
		this.outPutPath = outPath;
		this.filePrefix = ConvertUtils.extractFileNameWithoutExtension(filePath);
		this.fileExtension = FILE_EXTENSION;
		String extension = ConvertUtils.extractFileExtensionFromFilePath(filePath).toLowerCase();
		this.fileType = FileType.parseType(extension);
		this.subPath = ConvertUtils.extractFileSubPathFromFilePath(filePath);
		this.tag = tag;
		this.taskTag = taskTag;
		this.convertWorkPath = System.getProperty("user.dir");
	}

	public void setStateCode(StateCode state) {
		this.state = state;
	}

	public StateCode getStateCode() {
		return this.state;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return this.tag;
	}

	public void setFilePath(String fileP) {
		this.filePath = fileP;
	}

	public String getFilePath() {
		return this.filePath;
	}
	
	public void setOutPutPath(String outP) {
		this.outPutPath = outP;
	}

	public String getOutPutPath() {
		return this.outPutPath;
	}

	public void setFilePrefix(String filePre) {
		this.filePrefix = filePre;
	}

	public String getFilePrefix() {
		return this.filePrefix;
	}

	public void setFileExtension(String fileExt) {
		this.fileExtension = fileExt;
	}

	public String getFileExtension() {
		return this.fileExtension;
	}

	public void setSubPath(String subPath) {
		this.subPath = subPath;
	}

	public String getSubPath() {
		return this.subPath;
	}

	public FileType getFileType() {
		return this.fileType;
	}

	public void setTaskTag(String taskTag) {
		this.taskTag = taskTag;
	}

	public String getTaskTag() {
		return this.taskTag;
	}

	/** public void setConvertedFileList(List<String> list) {
		this.convertedFileList = list;
	}*/

	public List<String> getConvertedFileList() {
		return this.convertedFileList;
	}
	
	public void fireConvert() {
		
		this.state = StateCode.Started;
		// 回传转换状态,开始转换
		ActionDialect ad = convertActionDialec();
		this.cellet.talk(tag, ad);
		
		this.convertedFileList.clear();
		String superType =  FileType.parseFileSuperType(this.getFileType());
		if (superType.equals("image")) {
			(new Thread() {
				@Override
				public void run() {
					List<String> uris = null;
					if (copyImageFileToConvertDir()) {
						uris = moveFileToWorkspace();
					}
					else {
						state = StateCode.Failed;
					}
					
					// TODO
					if (null != tag) {
						ActionDialect dialect = new ActionDialect();
						dialect.setAction(CubeConsoleAPI.ACTION_CONVERT_STATE);
						JSONObject value = new JSONObject();

						try {	

							//state
							String error = null;
							if (state != StateCode.Failed) {
								//uri
								if (null != uris && !uris.isEmpty())
								{
									error = uris.get(0);
									Logger.d(getClass(), "image uri not empty, and uri(0) = " + error);
									if (error.equals("404")) {
										uris.clear();
										state = StateCode.Failed;
									}
									else {
										state = StateCode.Successed;
										JSONArray jsonArray = new JSONArray(uris);
										value.put("convertedFileUris", jsonArray);
									}
								}else
								{	
									state = StateCode.Failed;
									Logger.d(getClass(), "image uri is empty!!");
								}
							}
							value.put("state", state.getCode());
							value.put("filePath", getFilePath());
							value.put("outPath", getOutPutPath());
							value.put("taskTag", getTaskTag());
						} catch (JSONException e) {
							e.printStackTrace();
						}
						dialect.appendParam("data", value);
						// 发送数据
						cellet.talk(tag, dialect);
					}
				}
			}).start();
		}
		else if (superType.equals("office")) {
			//转换，移动
			(new Thread() {
				@Override
				public void run() {
					List<String> uris = null;
					if (sofficeOperation()) {
						String pdfFilePath = convertWorkPath + "/" + filePrefix + ".pdf";
						if (pdftoppmOperation(pdfFilePath)) {
							uris = moveFileToWorkspace();
						} else {
							state = StateCode.Failed;
						}
					} else {
						state = StateCode.Failed;
					}
					
					if (null != tag) {
						ActionDialect dialect = new ActionDialect();
						dialect.setAction(CubeConsoleAPI.ACTION_CONVERT_STATE);
						JSONObject value = new JSONObject();

						try {	
							//state
							String error = null;
							if (state != StateCode.Failed) {
								//uri
								if (null != uris && !uris.isEmpty())
								{
									error = uris.get(0);
									Logger.d(getClass(), "office uri not empty, and uri(0) = " + error);
									if (error.equals("404")) {
										uris.clear();
										state = StateCode.Failed;
									}
									else {
										state = StateCode.Successed;
										JSONArray jsonArray = new JSONArray(uris);
										value.put("convertedFileUris", jsonArray);
									}
								}else
								{	
									state = StateCode.Failed;
									Logger.d(getClass(), "office uri is empty!!");
								}
							}

							value.put("state", state.getCode());
							value.put("filePath", getFilePath());
							value.put("outPath", getOutPutPath());
							value.put("taskTag", getTaskTag());
							
						} catch (JSONException e) {
							e.printStackTrace();
						}
						Logger.d(getClass(), "office dialect value = " + value.toString());
						dialect.appendParam("data", value);
						// 发送数据
						cellet.talk(tag, dialect);
					}
				}
			}).start();
		}
		else if (superType.equals("pdf")) {
			//pdftoppm
			//转换，移动
			(new Thread() {
				@Override
				public void run() {
					List<String> uris = null;
					if (pdftoppmOperation(filePath)) {
						uris = moveFileToWorkspace();
					}
					else {
						state = StateCode.Failed;
					}
					if (null != tag) {
						ActionDialect dialect = new ActionDialect();
						dialect.setAction(CubeConsoleAPI.ACTION_CONVERT_STATE);
						JSONObject value = new JSONObject();

						try {	
							//state
							String error = null;
							if (state != StateCode.Failed) {
								//uri
								if (null != uris && !uris.isEmpty())
								{
									error = uris.get(0);
									Logger.d(getClass(), "pdf uri not empty, and uri(0) = " + error);
									if (error.equals("404")) {
										uris.clear();
										state = StateCode.Failed;
									}
									else {
										state = StateCode.Successed;
										JSONArray jsonArray = new JSONArray(uris);
										value.put("convertedFileUris", jsonArray);
									}
								}else
								{	
									state = StateCode.Failed;
									Logger.d(getClass(), "pdf uri is empty!!");
								}
							}
							value.put("state", state.getCode());
							value.put("filePath", getFilePath());
							value.put("outPath", getOutPutPath());
							value.put("taskTag", getTaskTag());
						} catch (JSONException e) {
							e.printStackTrace();
						}
						Logger.d(getClass(), "pdf dialect value = " + value.toString());
						dialect.appendParam("data", value);
						// 发送数据
						cellet.talk(tag, dialect);
					}
				}
			}).start();
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
	}

    //	soffice --headless --convert-to pdf /data/dddd.doc
	public boolean sofficeOperation() {
		this.state = StateCode.Executing;
		responseTaskState(this);

		final AtomicInteger stop = new AtomicInteger(0);
		String sofficeCmd = SOFFICE_PDF + " " + this.filePath;
		int exitVal = JavaExeLinuxCmd.exec(
				new String[] { "/bin/sh", "-c", sofficeCmd }, null, null, new JavaExeLinuxCmd.Listener() {
					@Override
					public void onFinish(List<String> list, final AtomicInteger stop) {
					}
				}, stop);
		
		int total = 0;

		while (stop.get() != 200 && stop.get() != 404) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			total++;
			
			if (total > 6000)
				break;
		}
		
		if (stop.get() == 404) {
			return false;
		} else if (stop.get() == 200) {
			return true;
		}else
		{
			return false;
		}
	}
	

	// pdftoppm -png file.pdf file_prefix
	public boolean pdftoppmOperation(String pdfFilePath) {
		this.state = StateCode.Executing;
		responseTaskState(this);

		//2.pdftoppm -png /home/lztxhost/Documents/CubeConsole/dddd.pdf file_prefix
		final AtomicInteger stop = new AtomicInteger(0);
		String pdftoppmCmd = PDFTOPPM_PNG + " " + pdfFilePath + " "
				+ this.filePrefix;
		int pdftoppmExitVal = JavaExeLinuxCmd.exec(
				new String[] { "/bin/sh", "-c", pdftoppmCmd }, null, null,  new JavaExeLinuxCmd.Listener(){
					@Override
					public void onFinish(List<String> list, final AtomicInteger stop) {
					}
				}, stop);
		
		int total = 0;

		while (stop.get() != 200 && stop.get() != 404) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			total++;
			
			if (total > 6000)
				break;
		}
		
		if (stop.get() == 404) {
			return false;
		} else if (stop.get() == 200) {
			return true;
		} else
		{
			return false;
		}
	}

	
	public boolean copyImageFileToConvertDir() {
		// 1.cp /home/lztxhost/apache-tomcat-7.0.61/webapps/ROOT/local/upload/admin/dddd.png
		// /home/lztxhost/Documents/CubeConsole/dddd.png
		this.state = StateCode.Executing;
		File tmpFile = new File(convertWorkPath);
		if (!tmpFile.exists()) {
			if (tmpFile.mkdirs()) {
				Logger.d(getClass(), "mkdirs " + convertWorkPath + "succused");
			} else {
				Logger.d(getClass(), "mkdirs " + convertWorkPath + "failed");
			}
		}
		String tmpFilePath = convertWorkPath + "/" + ConvertUtils.extractFileName(this.filePath);
		//图片不经过转换， 后缀名不一定是png
		this.setFileExtension(ConvertUtils.extractFileExtensionFromFilePath(filePath));
		final String cpCmd = CP + this.filePath + " " + tmpFilePath;
		final AtomicInteger stop = new AtomicInteger(0);
		int exitVal = JavaExeLinuxCmd.exec(
				new String[] { "/bin/sh", "-c", cpCmd }, null, null,  new JavaExeLinuxCmd.Listener(){
					@Override
					public void onFinish(List<String> list, final AtomicInteger stop) {
						if (stop.get() == 404) {
							//error
						} else if (stop.get() == 200) {
							//success
						}
					}
				}, stop);
		return exitVal == 0;
	}

	public List<String> moveFileToWorkspace() {
		this.state = StateCode.Executing;
		responseTaskState(this);

		// 1 查找文件
		// find -name "*.png" | grep -H "MCE"
//		 find -name '*.png' | grep 'jgkafrlBfVuGCfIAeNbcNSaRTuYKVdag' | awk -F"./" '{print $2}' && mv -i $(find -name '*.png' | grep 'jgkafrlBfVuGCfIAeNbcNSaRTuYKVdag' | awk -F"./" '{print $2}') /home/cubehost/backupFile/
		// 2 移动转换后的文件到工作目录
		//convertWorkDirPath  /home/lztxhost/Documents/CubeConsole/
		String workDirPath = outPutPath + subPath;
		// workDirPath： /home/lztxhost/apache-tomcat-7.0.61/webapps/ROOT/cubewhiteboard/shared/admin/
		File tmpFile = new File(workDirPath);
		if (!tmpFile.exists()) {
			if (tmpFile.mkdirs()) {
				Logger.d(getClass(), "mkdirs " + workDirPath + "succused");
			} else {
				Logger.d(getClass(), "mkdirs " + workDirPath + "failed");
			}
		}
		
		String fileExtent = this.getFileExtension();
		
		String filePrefix = this.getFilePrefix();
		
		String findCmd = "find -name '*." + fileExtent
				+ "' | grep '" + filePrefix
				+ "' | awk -F \"./\" '{print $2}' && mv -i $(find -name '*." 
				+ fileExtent
				+ "' | grep '" + filePrefix
				+ "' | awk -F \"./\" '{print $2}') "
				+ workDirPath;

		final AtomicInteger  stop = new AtomicInteger(0);
		int exitVal = JavaExeLinuxCmd.exec(
				new String[] { "/bin/sh", "-c", findCmd }, null, null,
				new JavaExeLinuxCmd.Listener() {
					@Override
					public void onFinish(List<String> list, final AtomicInteger  stop) {
						if (stop.get() == 404) {
							//error
							convertedFileList.add("404");
						} else if (stop.get() == 200) {
							//success
							if (null != list && !list.isEmpty()) {
								for (String name : list) {						
									convertedFileList.add(subPath + name);
								}
								Logger.d(getClass(), "convertedFileList  = " + convertedFileList.size());
		
							} else {
								convertedFileList.add("404");
							}
						}
					}
				}, stop);
		
		int total = 0;

		while (stop.get() != 200 && stop.get() != 404) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			total++;
			
			if (total > 6000)
				break;
		}
		
		// 返回URIs
		return convertedFileList;
	}

	private void responseTaskState(ConvertTask task) {
		int state = task.state.getCode();
		ActionDialect ad = new ActionDialect();
		ad.setAction(CubeConsoleAPI.ACTION_CONVERT_STATE);
		JSONObject jo = new JSONObject();
		try {
			jo.put("state", state);
			jo.put("filePath", task.getFilePath());
			jo.put("outPath", getOutPutPath());
			jo.put("taskTag", task.getTaskTag());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ad.appendParam("data", jo);

		// 发送数据
		cellet.talk(this.getTag(), ad);
	}

	private ActionDialect convertActionDialec() {
		int state = this.state.getCode();
		ActionDialect ad = new ActionDialect();
		ad.setAction(CubeConsoleAPI.ACTION_CONVERT_STATE);
		JSONObject jo = new JSONObject();
		try {
			jo.put("state", state);
			jo.put("filePath", this.getFilePath());
			jo.put("outPath", getOutPutPath());
			jo.put("taskTag", this.getTaskTag());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ad.appendParam("data", jo);
		return ad;
	}
}
