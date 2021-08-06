package org.beetl.sql.gen;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * 生成java代码，无论是entity，还是mapper或者自定义的service等，需要生成的包名
 * @author xiandafu
 */
@Data
public class PackageList {
    private Set<String> pkgs = new TreeSet<>();

}
