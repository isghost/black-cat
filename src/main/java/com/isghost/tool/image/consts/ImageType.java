package com.isghost.tool.image.consts;

public enum ImageType {
    JPG("jpg"),
    JPEG("jpeg"),
    PNG("png"),
    BMP("bmp"),
    GIF("gif");

    private final String desc;

    ImageType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public static ImageType getImageType(String desc) {
        for (ImageType imageType : ImageType.values()) {
            if (imageType.getDesc().equals(desc)) {
                return imageType;
            }
        }
        return null;
    }
}
