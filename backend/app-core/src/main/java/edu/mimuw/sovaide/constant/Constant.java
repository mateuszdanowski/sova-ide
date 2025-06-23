package edu.mimuw.sovaide.constant;

public class Constant {
	public static final String FILE_DIRECTORY = System.getProperty("user.home") + "/Downloads/uploads/";

	public static final String X_REQUESTED_WITH = "X-Requested-With";

	public static String getLocalFilePath(String projectId, String fileUrl) {
		String fileExtension = fileUrl.substring(fileUrl.lastIndexOf("."));
		return FILE_DIRECTORY + projectId + fileExtension;
	}
}
