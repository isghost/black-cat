package com.isghost.tool.image.utils;

import com.isghost.tool.image.consts.ImageType;
import com.isghost.tool.image.model.FileInfo;

import java.io.File;
import java.text.DecimalFormat;

public class FileUtils {

    public static FileInfo getFileInfo(File file) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setPath(file.getAbsolutePath());
        // 获取文件名
        String fileName = file.getName();
        fileInfo.setName(fileName);

        // 获取文件扩展名
        String fileExtension = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = fileName.substring(dotIndex + 1);
        }
        fileInfo.setImageType(ImageType.getImageType(fileExtension));

        fileInfo.setSize(file.length());
        fileInfo.setShowSize(formatFileSize(fileInfo.getSize()));
        return fileInfo;
    }

    public static FileInfo getFileInfo(String filePath) {
        File file = new File(filePath);
        return getFileInfo(file);
    }

    public static String formatFileSize(long fileSize) {
        if (fileSize <= 0) {
            return "0B";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(fileSize) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(fileSize / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
