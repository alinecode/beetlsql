package org.beetl.sql.core.kit;

import java.io.File;

public class GenKit {

    private static String srcPathRelativeToSrc = "/main/java";
    private static String resourcePathRelativeToSrc = "/main/resources";

    public static String getJavaSRCPath() {
        return getPath(srcPathRelativeToSrc);
    }

    public static String getJavaResourcePath() {
        return getPath(resourcePathRelativeToSrc);
    }

    /**
     * 设置java代码生成根路径（影响pojo、mapper的生成）
     *
     * @param srcPathRelativeToSrc 基于src目录的相对路径
     */
    public static void setSrcPathRelativeToSrc(String srcPathRelativeToSrc) {
        GenKit.srcPathRelativeToSrc = srcPathRelativeToSrc;
    }

    /**
     * 设置资源文件生成根路径（影响md的生成）
     *
     * @param resourcePathRelativeToSrc 基于src目录的相对路径
     */
    public static void setResourcePathRelativeToSrc(String resourcePathRelativeToSrc) {
        GenKit.resourcePathRelativeToSrc = resourcePathRelativeToSrc;
    }

    private static String getPath(String relativeToSrc) {
        String srcPath;
        String userDir = System.getProperty("user.dir");
        if (userDir == null) {
            throw new NullPointerException("用户目录未找到");
        }
        File src = new File(userDir, "src");
        File resSrc = new File(src.toString(), relativeToSrc);
        if (resSrc.exists()) {
            srcPath = resSrc.toString();
        } else {
            srcPath = src.toString();
        }
        return srcPath;
    }

}
