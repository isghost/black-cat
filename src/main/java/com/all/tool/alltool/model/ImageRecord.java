package com.all.tool.alltool.model;

import com.all.tool.alltool.consts.ImageType;
import com.all.tool.alltool.consts.ProcessStatus;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
    private BooleanProperty selected; // 是否选中
    private String fileName; // 文件名
    private String filePath; // 路径
    private Boolean result; // 结果
    private String showResult; // 结果
    private String fileShowSize; // 文件大小
    private long fileSize; // 文件
    private String compressedSize; // 压缩后大小
    private String spaceSaved; // 节省空间
    private String spaceSavedPercent; // 节省空间百分比
    private ImageType imageType; // 图片类型
    public ImageRecord(){
        this.selected = new SimpleBooleanProperty(false);
    }

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
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public boolean getSelected() {
        return this.selected.get();
    }

    public String getShowResult() {
        if (result == null) {
            return null;
        }
        return result ? ProcessStatus.SUCCESS.getDesc() : ProcessStatus.FAIL.getDesc();
    }

}
