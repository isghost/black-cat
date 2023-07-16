package com.all.tool.alltool;

import com.all.tool.alltool.consts.ProcessStatus;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Data;

/**
 * 文件名
 * 路径
 * 结果
 * 文件大小
 * 压缩后大小
 * 节省空间
 * 节省空间(%)
 * // 添加字段并加上注释
 */
@Data
public class ImageRecord {
    private boolean selected; // 是否选中
    private String fileName; // 文件名
    private String filePath; // 路径
    private Boolean result; // 结果
    private String showResult; // 结果
    private String fileShowSize; // 文件大小
    private long fileSize; // 文件
    private String compressedSize; // 压缩后大小
    private String spaceSaved; // 节省空间
    private String spaceSavedPercent; // 节省空间百分比

    public static ImageRecord createDefault() {
        ImageRecord record = new ImageRecord();
        record.setSelected(true);
        record.setFileName("example.jpg");
        record.setFilePath("/path/to/example.jpg");
        record.setResult(true);
        record.setFileSize(1024);
        record.setFileShowSize("1KB");
        record.setCompressedSize("512L");
        record.setSpaceSaved("512L");
        record.setSpaceSavedPercent("%");
        return record;
    }

    public BooleanProperty selectedProperty() {
        return new SimpleBooleanProperty(selected);
    }

    public StringProperty showResultProperty() {
        if (result == null) {
            return null;
        }
        return new SimpleStringProperty(result ? ProcessStatus.SUCCESS.getDesc() : ProcessStatus.FAIL.getDesc());
    }

}
