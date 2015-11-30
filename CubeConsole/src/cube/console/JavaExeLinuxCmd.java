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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import net.cellcloud.common.LogLevel;
import net.cellcloud.common.Logger;

public class JavaExeLinuxCmd {
	
	public JavaExeLinuxCmd() {
		
	}

	/**
	 * 在有指定环境和工作目录的独立进程中执行指定的字符串命令,命令无参数
	 * cmd：一条指定的系统命令
	 */
	public static int exec(String cmd, String[] envp, File dir, JavaExeLinuxCmd.Listener listener, final AtomicInteger stop) {
		try {
			Process proc = Runtime.getRuntime().exec(cmd,envp,dir);

			//StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR", cmd);
			StreamGobbler outputGobbler = new StreamGobbler(proc, "OUTPUT", cmd, stop);

			//errorGobbler.start();
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
	public static int exec(String[] cmdarray, String[] envp, File dir, JavaExeLinuxCmd.Listener listener, final AtomicInteger stop) {
		try {
			Process proc = Runtime.getRuntime().exec(cmdarray,envp,dir);

			//StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR", cmdarray[2]);
			StreamGobbler outputGobbler = new StreamGobbler(proc, "OUTPUT", cmdarray[2], stop);
			outputGobbler.listener = listener;

			//errorGobbler.start();
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
	public static int executShell(String shPath, JavaExeLinuxCmd.Listener listener, final AtomicInteger stop){
		
		String cmd = "chmod 777"+ shPath;
		StringBuilder stringBuilder = new StringBuilder();
					
		if (exec(new String[] { "/bin/sh", "-c", cmd }, null, null, new JavaExeLinuxCmd.Listener(){
			@Override
			public void onFinish(List<String> list, final AtomicInteger stop) {
			}
		}, stop) == 0) {
			try {	
				String cmd2 = "/bin/sh "+ shPath;
				Process proc = Runtime.getRuntime().exec(cmd2);
				
				//StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR", cmd);
				StreamGobbler outputGobbler = new StreamGobbler(proc, "OUTPUT", cmd, stop);
				outputGobbler.listener = listener;
				//errorGobbler.start();
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
		
		private final String CMD_END = "CMD_END";
		private final int CMD_ERROR = 404;
		private final int CMD_SUCCESSED = 200;
		
		private InputStream is;
		private Process process;
		private String type;
		private StringBuilder buf;
		private String cmd;
		protected Listener listener;
		private AtomicInteger stop;
		private List<String> list;

		protected StreamGobbler(Process process, String type, String cmd,  final AtomicInteger stop) {
		    //this.is = is;
			this.process = process;
		    this.type = type;
		    this.cmd = cmd + " && echo " + CMD_END;
		    this.buf = new StringBuilder();
		    this.stop = stop;
		    this.list =  new ArrayList() ;
		}

		public void run() {
			boolean error =  false;
			try {
				InputStreamReader isr = new InputStreamReader(this.process.getInputStream());
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				int count = 0;
				
				while ((line = br.readLine()) != null) {
					Logger.i(this.getClass(), " Command : {" + cmd + "} " + type + " >>> " + line.toString());
					if (line.toString().equals(CMD_END)) {
						break;
					}
					
					if (line.toString().contains("soffice:")
							||line.toString().contains("pdftoppm:")
							||line.toString().contains("find:")
							||line.toString().contains("mv:")
							|| line.toString().contains("-bash:")) {
						list.clear();
						error = true;
						break;
					}

					list.add(line.toString());
					
					try {
						Thread.sleep(10);
						count++;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (count > 6000)
						break;
				}
				
				process.destroy();
				
			} catch (IOException ioe) {
				Logger.log(StreamGobbler.class, ioe, LogLevel.ERROR);
			}

			if (null != this.listener) {
				if (error) {
					this.stop.set(CMD_ERROR);
				}else
				{
					this.stop.set(CMD_SUCCESSED);
				}	
				this.listener.onFinish(list, this.stop);
			}
		}
	}

	public interface Listener {
		public void onFinish(List<String> list, final AtomicInteger stop);
	}
}
