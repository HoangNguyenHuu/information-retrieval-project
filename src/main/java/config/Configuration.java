package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by hoangnh on 30/10/2017.
 */
public class Configuration {

    HashMap<String, String> map;

    public Configuration() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("config/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        map = new HashMap<String, String>((Map) properties);
    }

    public String getProperty(String name) {
        return map.get(name);
    }
}
