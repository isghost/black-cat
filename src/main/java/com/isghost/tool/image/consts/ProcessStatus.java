package com.isghost.tool.image.consts;

public enum ProcessStatus {
    SUCCESS("成功"),
    FAIL("压缩失败,图片没有压缩空间或者不存在等原因");

    private final String desc;

    ProcessStatus(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
