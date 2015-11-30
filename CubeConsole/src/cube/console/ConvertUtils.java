package cube.console;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConvertUtils {

	/**
	 * 根据文件路径提取文件及后缀。test.doc
	 * 
	 * @param filePath
	 * @return
	 */
	public static String extractFileName(String filePath) {
		int lastIndex = filePath.lastIndexOf("/");
		if (lastIndex < 0) {
			return null;
		}

		return filePath.substring(lastIndex + 1, filePath.length());
	}

	/**
	 * 根据文件名提取文件后缀名 doc
	 * 
	 * @param fileName
	 * @return
	 */
	public static String extractFileExtensionFromFileName(String fileName) {
		int lastIndex = fileName.lastIndexOf(".");
		if (lastIndex < 0) {
			return null;
		}

		return fileName.substring(lastIndex + 1, fileName.length());
	}

	/**
	 * 提取文件名,无后缀。test
	 * 
	 * @param fileName
	 * @return
	 */
	public static String extractFileNameWithoutExtension(String filePath) {
		String fileName = extractFileName(filePath);
		int lastIndex = fileName.lastIndexOf(".");
		if (lastIndex < 0) {
			return null;
		}

		return fileName.substring(0, lastIndex);
	}
	
	/**
	 * 根据路径提取文件后缀 doc
	 * 
	 * @param filePath
	 * @return
	 */
	public static String extractFileExtensionFromFilePath(String filePath) {
		
		int lastIndex = filePath.lastIndexOf(".");
		if (lastIndex < 0) {
			return null;
		}

		return filePath.substring(lastIndex + 1, filePath.length());
	}

	/**
	 * 提取子路径  subpath
	 * 
	 * @param filePath
	 * @return 
	 */
	public static String extractFileSubPathFromFilePath(String filePath) {
		
		int endIndex = filePath.lastIndexOf("/");
		String uploadDirPath = filePath.substring(0, endIndex + 1);
		
		//TODO 取subpath
		String[] strings = uploadDirPath.split("/");
		int i = strings.length - 1;
		String subPath = strings[i] +"/";

		return subPath;
	}
}
