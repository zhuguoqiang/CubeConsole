package com.Cube.Console;

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

}
