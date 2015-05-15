package cube.console;
/**
 * @discript java在linux环境下执行cmd, DEMO
 * @author zhgq
 * @date 2015-04-02
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import net.cellcloud.common.LogLevel;
import net.cellcloud.common.Logger;

public class JavaExeLinuxCmd {
	
	public JavaExeLinuxCmd() {
		
	}

	/**
	 * 在有指定环境和工作目录的独立进程中执行指定的字符串命令,命令无参数
	 * cmd：一条指定的系统命令
	 */
	public static int exec(String cmd, String[] envp, File dir, JavaExeLinuxCmd.Listener listener) {
		try {
			Process proc = Runtime.getRuntime().exec(cmd,envp,dir);

			StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR", cmd);
			StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT", cmd);

			errorGobbler.start();
            outputGobbler.start();
            outputGobbler.listener = listener;
            
			int exitVal = proc.waitFor();

			Logger.i(JavaExeLinuxCmd.class, "Command: {" + cmd + "} exit value: " + exitVal);

			

			return exitVal;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 *  在指定环境和工作目录的独立进程中执行指定的命令和变量 
	 *  
	 */
	public static int exec(String[] cmdarray, String[] envp, File dir, JavaExeLinuxCmd.Listener listener) {
		try {
			Process proc = Runtime.getRuntime().exec(cmdarray,envp,dir);

			StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR", cmdarray[2]);
			StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT", cmdarray[2]);
			outputGobbler.listener = listener;

			errorGobbler.start();
            outputGobbler.start();

			int exitVal = proc.waitFor();

			Logger.i(JavaExeLinuxCmd.class, "Command :: {" +cmdarray[2] + "} exit value: " + exitVal);

			return exitVal;
		} catch(Exception e) {
			Logger.log(JavaExeLinuxCmd.class, e, LogLevel.ERROR);
		}

		return -1;
	}
	
	/**
	 * 执行shell脚本 
	 */
	public static int executShell(String shPath, JavaExeLinuxCmd.Listener listener){
		
		String cmd = "chmod 777"+ shPath;
		StringBuilder stringBuilder = new StringBuilder();
					
		if (exec(new String[] { "/bin/sh", "-c", cmd }, null, null, new JavaExeLinuxCmd.Listener(){
			@Override
			public void onFinish(StringBuilder buf) {
			}
			@Override
			public void onError() {
			}
		}) == 0) {
			try {	
				String cmd2 = "/bin/sh "+ shPath;
				Process proc = Runtime.getRuntime().exec(cmd2);
				
				StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR", cmd);
				StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT", cmd);
				outputGobbler.listener = listener;
				errorGobbler.start();
	            outputGobbler.start();
	            
	            int exitVal = proc.waitFor();

				Logger.i(JavaExeLinuxCmd.class, "Command :: {" + cmd2 + "}  exit value: " + exitVal);

				return exitVal;
			} catch(Exception e) {
				Logger.log(JavaExeLinuxCmd.class, e, LogLevel.ERROR);
			}
		}
		return -1;
	}

	public static class StreamGobbler extends Thread {
		private InputStream is;
		private String type;
		private StringBuilder buf;
		private String cmd;
		protected Listener listener;

		protected StreamGobbler(InputStream is, String type, String cmd) {
		    this.is = is;
		    this.type = type;
		    this.cmd = cmd;
		    this.buf = new StringBuilder();
		}

		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null) {
					this.buf.append(line);
					Logger.i(this.getClass(), " Command : {" + cmd + "}" + type + " >>> " + line);
				}
			} catch (IOException ioe) {
				Logger.log(StreamGobbler.class, ioe, LogLevel.ERROR);
			}

			if (null != this.listener) {
				this.listener.onFinish(this.buf);
			}
		}
	}

	public interface Listener {
		public void onFinish(StringBuilder buf);
		public void onError();
	}
}
