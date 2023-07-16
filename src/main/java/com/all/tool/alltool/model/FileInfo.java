package com.all.tool.alltool.model;

import lombok.Data;

@Data
public class FileInfo {
    // 路径
    private String path;
    // 文件名
    private String name;
    // 扩展名
    private String extension;
    // 文件大小
    private long size;
    // 显示的文件大小
    private String showSize;
}
