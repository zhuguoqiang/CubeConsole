package cube.console;

public enum FileType {
	JPG("jpg"), PNG("png"), BMP("bmp"), JPEG("jpeg"), GIF("gif"), IMAGE("image"),

	DOC("doc"), DOCX("docx"), PPT("ppt"), PPTX("pptx"), OFFICE("office"),

	PDF("pdf"),

	OGG("ogg"), MP3("mp3"), WAV("wav"), AUDIO("audio"), MP4("mp4"), VEDIO(
			"vedio"),

	UNKNOWN("");

	private String extension;

	FileType(String extension) {
		this.extension = extension;
	}

	public String getExtension() {
		return this.extension;
	}

	public static FileType parseType(String extension) {
		for (FileType t : FileType.values()) {
			if (t.extension.equals(extension)) {
				return t;
			}
		}

		return FileType.UNKNOWN;
	}

	public static String parseFileSuperType(FileType type) {

		String extension = type.extension;
		if (extension.equals("jpg") || extension.equals("jpeg")
				|| extension.equals("png") || extension.equals("bmp")
				|| extension.equals("gif")) {
			return IMAGE.extension;

		}
		else if (extension.equals("doc") || extension.equals("docx")
				|| extension.equals("ppt") || extension.equals("pptx")
				) {
			return OFFICE.extension;
		}else if (extension.equals("pdf")) {
			return PDF.extension;
		}else if (extension.equals("ogg") || extension.equals("mp3")
				|| extension.equals("wav")) {
			return AUDIO.extension;
		}
		else if (extension.equals("mp4")) {
			return VEDIO.extension;
		}
		
		return UNKNOWN.extension;
	}

	// protected enum FileTypeImage {
	//
	// private String imageExtension;
	//
	// FileTypeImage(String imageExtension) {
	// this.imageExtension = imageExtension;
	// }
	//
	// public String getImageExtension() {
	// return this.imageExtension;
	// }
	// }
	//
	// protected enum FileTypeOffice {
	//
	//
	// private String officeExtension;
	//
	// FileTypeOffice(String officeExtension) {
	// this.officeExtension = officeExtension;
	// }
	//
	// public String getOfficeExtension() {
	// return this.officeExtension;
	// }
	// }
	//
	// protected enum FileTypeAudio {
	//
	//
	// private String audioExtension;
	//
	// FileTypeAudio(String audioExtension) {
	// this.audioExtension = audioExtension;
	// }
	//
	// public String getImageExtension() {
	// return this.audioExtension;
	// }
	// }
}
