package com.all.tool.alltool.consts;

public enum ProcessStatus {
    SUCCESS("成功"),
    FAIL("压缩失败");

    private final String desc;

    ProcessStatus(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
