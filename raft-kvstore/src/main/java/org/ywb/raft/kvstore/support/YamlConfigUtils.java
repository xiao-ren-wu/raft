package org.ywb.raft.kvstore.support;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;

/**
 * @author yuwenbo1
 * @date 2021/6/29 10:45 下午 星期二
 * @since 1.0.0
 */
public class YamlConfigUtils {

    private static final Yaml YAML = new Yaml();

    public static <T> T load(File yamlFile, Class<T> tClass) throws FileNotFoundException {
        return YAML.loadAs(new FileReader(yamlFile), tClass);
    }

    public static <T> T load(InputStream inputStream, Class<T> tClass) {
        return YAML.loadAs(inputStream, tClass);
    }

}
