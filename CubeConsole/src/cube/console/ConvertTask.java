package cube.console;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.cellcloud.common.Logger;
import net.cellcloud.core.Cellet;
import net.cellcloud.talk.dialect.ActionDialect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ConvertTask extends BaseTask {

	StateCode state;
	String filePath = null;
	String outPutPath = null;
	String filePrefix = null;
	String fileExtension = null;
	FileType fileType;
	String subPath = null;
	String taskTag = null;
	String tag = null;
	Cellet cellet = null;
	List<String> convertedFileList = null;
	String tmpConvertDirPath = null;

	private static final String rootPath = "/home/lztxhost/apache-tomcat-7.0.61/webapps/ROOT/cubewhiteboard/shared/";
	private static final String URL = "http:211.103.217.154/cubewhiteboard/shared/";
	private static final String convertWorkDirPath = "/home/lztxhost/Documents/CubeConsole/";
	private final String UNOCONV_PDF = "unoconv -f pdf";
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
		this.convertedFileList = new ArrayList<String>();
	}

	public static String getRootPath() {
		return rootPath;
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

	public void setConvertedFileList(List<String> list) {
		this.convertedFileList = list;
	}

	public List<String> getConvertedFileList() {
		return this.convertedFileList;
	}
	
	public void fireConvert() {
		
		this.state = StateCode.Started;
		// 回传转换状态,开始转换
		ActionDialect ad = convertActionDialec();
		this.cellet.talk(tag, ad);

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
//					// TODO
//					if (null != tag) {
//						ActionDialect dialect = new ActionDialect();
//						dialect.setAction(CubeConsoleAPI.ACTION_CONVERT_STATE);
//						JSONObject value = new JSONObject();
//
//						try {	
//							if (state != StateCode.Failed) {
//								String error = uris.get(0);
//								if (error.equals("404")) {
//									uris.remove(0);
//									state = StateCode.Failed;
//									value.put("faileCode", "404");
//								}
//								else {
//									state = StateCode.Successed;
//								}
//							}
//							JSONArray jsonArray = new JSONArray(uris);
//							value.put("state", state.getCode());
//							value.put("filePath", getFilePath());
//							value.put("convertedFileUris", jsonArray);
//							value.put("taskTag", getTaskTag());
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//						dialect.appendParam("data", value);
//						// 发送数据
//						cellet.talk(tag, dialect);
//					}
				}
			}).start();
		}
		else if (superType.equals("office")) {
			//转换，移动
			(new Thread() {
				@Override
				public void run() {
					List<String> uris = null;
					if (unoconvOperation()) {
						if (pdftoppmOperation()) {
							uris = moveFileToWorkspace();
						}
						else {
							state = StateCode.Failed;
						}
					}
					else {
						state = StateCode.Failed;
					}
					if (null != tag) {
						ActionDialect dialect = new ActionDialect();
						dialect.setAction(CubeConsoleAPI.ACTION_CONVERT_STATE);
						JSONObject value = new JSONObject();

						try {	
							if (state != StateCode.Failed) {
								String error = uris.get(0);
								if (error.equals("404")) {
									uris.remove(0);
									state = StateCode.Failed;
									value.put("faileCode", "404");
								}
								else {
									state = StateCode.Successed;
								}
							}
							
							JSONArray jsonArray = new JSONArray(uris);
							value.put("state", state.getCode());
							value.put("filePath", getFilePath());
							value.put("outPath", getOutPutPath());
							value.put("convertedFileUris", jsonArray);
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
		else if (superType.equals("pdf")) {
			//pdftoppm
			//转换，移动
			(new Thread() {
				@Override
				public void run() {
					List<String> uris = null;
					if (pdftoppmOperation()) {
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
							if (state != StateCode.Failed) {
								String error = uris.get(0);
								if (error.equals("404")) {
									uris.remove(0);
									state = StateCode.Failed;
									value.put("faileCode", "404");
								}
								else {
									state = StateCode.Successed;
								}
							}
							
							JSONArray jsonArray = new JSONArray(uris);
							value.put("state", state.getCode());
							value.put("filePath", getFilePath());
							value.put("outPath", getOutPutPath());
							value.put("convertedFileUris", jsonArray);
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
//		task.convert(this.cellet);

		// 回传转换状态,转换结束，返回uris

		/*
		 if (null != tag) {
			ActionDialect dialect = new ActionDialect();
			dialect.setAction(CubeConsoleAPI.ACTION_CONVERT_STATE);
			JSONObject value = new JSONObject();

			try {	
				String error = uris.get(0);
				if (error.equals("404")) {
					uris.remove(0);
					this.state = StateCode.Failed;
					value.put("faileCode", "404");
				}
				else {
					this.state = StateCode.Successed;
				}
				
				JSONArray jsonArray = new JSONArray(uris);
				value.put("state", this.state.getCode());
				value.put("filePath", this.getFilePath());
				value.put("convertedFileUris", jsonArray);
				value.put("taskTag", this.getTaskTag());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			dialect.appendParam("data", value);
			// 发送数据
			cellet.talk(tag, dialect);
		}
		*/
	}

	// unoconv -f pdf /home/lztxhost/apache-tomcat-7.0.61/webapps/ROOT/local/upload/admin/dddd.doc
	public boolean unoconvOperation() {
		this.state = StateCode.Executing;
		responseTaskState(this);

		String unoconvCmd = UNOCONV_PDF + " " + this.filePath;
		int exitVal = JavaExeLinuxCmd.exec(
				new String[] { "/bin/sh", "-c", unoconvCmd }, null, null, new JavaExeLinuxCmd.Listener() {
					@Override
					public void onFinish(StringBuilder buf) {
					}
					@Override
					public void onError() {
					}
				});
		return  0 == exitVal; 
	}

	// pdftoppm -png file.pdf file_prefix
	public boolean pdftoppmOperation() {
		this.state = StateCode.Executing;
		responseTaskState(this);

		int index = this.filePath.lastIndexOf(".");
		String pdfFilePath = this.filePath.substring(0, index + 1) + "pdf";

		// 1.cp /home/lztxhost/apache-tomcat-7.0.61/webapps/ROOT/local/upload/admin/dddd.pdf
		// /home/lztxhost/Documents/CubeConsole/dddd.pdf

		File tmpFile = new File(convertWorkDirPath);
		if (!tmpFile.exists()) {
			if (tmpFile.mkdirs()) {
				Logger.d(getClass(), "mkdirs " + convertWorkDirPath + "succused");
			} else {
				Logger.d(getClass(), "mkdirs " + convertWorkDirPath + "failed");
				return false;
			}
		}
		String tmpFilePath = convertWorkDirPath + ConvertUtils.extractFileName(pdfFilePath);

		String cpCmd = CP + pdfFilePath + " " + tmpFilePath;
		int exitVal = JavaExeLinuxCmd.exec(
				new String[] { "/bin/sh", "-c", cpCmd }, null, null,  new JavaExeLinuxCmd.Listener(){
					@Override
					public void onFinish(StringBuilder buf) {
					}
					@Override
					public void onError() {
					}
				});
		if (exitVal == 0) {
			//2.pdftoppm -png /home/lztxhost/Documents/CubeConsole/dddd.pdf file_prefix

			String pdftoppmCmd = PDFTOPPM_PNG + " " + tmpFilePath + " "
					+ this.filePrefix;
			int pdftoppmExitVal = JavaExeLinuxCmd.exec(
					new String[] { "/bin/sh", "-c", pdftoppmCmd }, null, null,  new JavaExeLinuxCmd.Listener(){
						@Override
						public void onFinish(StringBuilder buf) {
						}
						@Override
						public void onError() {
						}
					});
			return pdftoppmExitVal == 0;
		}
		else {
			return false;
		}
	}
	
	public boolean copyImageFileToConvertDir() {
		// 1.cp /home/lztxhost/apache-tomcat-7.0.61/webapps/ROOT/local/upload/admin/dddd.png
		// /home/lztxhost/Documents/CubeConsole/dddd.png
		this.state = StateCode.Executing;
		File tmpFile = new File(convertWorkDirPath);
		if (!tmpFile.exists()) {
			if (tmpFile.mkdirs()) {
				Logger.d(getClass(), "mkdirs " + convertWorkDirPath + "succused");
			} else {
				Logger.d(getClass(), "mkdirs " + convertWorkDirPath + "failed");
			}
		}
		String tmpFilePath = convertWorkDirPath + ConvertUtils.extractFileName(this.filePath);
		//图片不经过转换， 后缀名不一定是png
		this.setFileExtension(ConvertUtils.extractFileExtensionFromFilePath(filePath));
		final String cpCmd = CP + this.filePath + " " + tmpFilePath;
		int exitVal = JavaExeLinuxCmd.exec(
				new String[] { "/bin/sh", "-c", cpCmd }, null, null,  new JavaExeLinuxCmd.Listener(){
					@Override
					public void onFinish(StringBuilder buf) {
					}
					@Override
					public void onError() {
					}
				});
		return exitVal == 0;
	}

	public List<String> moveFileToWorkspace() {
		this.state = StateCode.Executing;
		responseTaskState(this);
		
		// 1 查找文件
		// find -name "*.png" | grep -H "MCE"
		String findCmd = "find -name '*." + this.getFileExtension()
				+ "' | grep -H " + "'" + this.getFilePrefix() + "'";

		int exitVal = JavaExeLinuxCmd.exec(
				new String[] { "/bin/sh", "-c", findCmd }, null, null,
				new JavaExeLinuxCmd.Listener() {
					@Override
					public void onFinish(StringBuilder buf) {
						String error = null;
						
						// 输出完成
						// 2 提取转换后 文件名 数组
						String fileNames = buf.toString();
						Logger.d(getClass(), "fileNames = " + fileNames);
						List<String> fileNameArray = new ArrayList<String>();
						if (fileNames.length() > 0) {
							String[] names = fileNames.split("/");
							for (String fileName : names) {
								int index = fileName.lastIndexOf("(");
								if (index > 0) {
									String subStr = fileName
											.substring(0, index);
									fileNameArray.add(subStr);
								}
								else if (index < 0) {
									String subStr = fileName;
									fileNameArray.add(subStr);	
								}
								else {
								}
							}
						} else {
							error = "404";
						}
						
						// 3 移动转换后的文件到工作目录
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

						if (fileNameArray.size() > 0) {
							for (String name : fileNameArray) {
								String pngFilePath = convertWorkDirPath + name;
								// mv -t /opt/soft/test/test4/ log1.txt log2.txt log3.txt
			
								String moveCmd = "mv -f -t " + workDirPath + " " + pngFilePath;
								int mvExitVal = JavaExeLinuxCmd.exec(
										new String[] { "/bin/sh", "-c", moveCmd }, null, null,  new JavaExeLinuxCmd.Listener(){
											@Override
											public void onFinish(StringBuilder buf) {
											}
											@Override
											public void onError() {
											}
										});
								if (0 == mvExitVal) {
									convertedFileList.add(subPath + name);
								}
							}

							Logger.d(getClass(), "convertedFileList  = " + convertedFileList.size());
							
							
						} else {
							convertedFileList.add(error);
						}
						
						// TODO
						if (null != tag) {
							ActionDialect dialect = new ActionDialect();
							dialect.setAction(CubeConsoleAPI.ACTION_CONVERT_STATE);
							JSONObject value = new JSONObject();

							try {	
								if (state != StateCode.Failed) {
									String er = convertedFileList.get(0);
									if (er.equals("404")) {
										convertedFileList.remove(0);
										state = StateCode.Failed;
										value.put("faileCode", "404");
									}
									else {
										state = StateCode.Successed;
									}
								}
								JSONArray jsonArray = new JSONArray(convertedFileList);
								value.put("state", state.getCode());
								value.put("filePath", getFilePath());
								value.put("outPath", getOutPutPath());
								value.put("convertedFileUris", jsonArray);
								value.put("taskTag", getTaskTag());
							} catch (JSONException e) {
								e.printStackTrace();
							}
							dialect.appendParam("data", value);
							// 发送数据
							cellet.talk(tag, dialect);
						}
					}
					
					@Override
					public void onError() {
						
					}
				});

		if (exitVal == 0) {
			Logger.d(getClass(), "Command :: '" + findCmd + "' exe successed");
		}
		else {
			Logger.d(getClass(), "Command : '" + findCmd + "' exe failed");
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
