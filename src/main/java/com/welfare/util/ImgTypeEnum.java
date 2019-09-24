package com.welfare.util;

public enum ImgTypeEnum {

    JPG("jpg", "image/jpeg"),
    JPEG("jpeg", "image/jpeg"),
    PNG("png", "image/png"),
    GIF("gif", "image/gif"),
    ;

    /**
     * 图片类型
     */
    private String code;
    /**
     * 图片contentType值
     */
    private String contentType;

    ImgTypeEnum(String code, String contentType) {
        this.code = code;
        this.contentType = contentType;
    }

    public static ImgTypeEnum valueOfCode(String code) {
        ImgTypeEnum[] values = ImgTypeEnum.values();
        for (ImgTypeEnum value : values) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getContentType() {
        return contentType;
    }
}
