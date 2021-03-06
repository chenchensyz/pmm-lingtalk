package cn.com.cybertech.model;

import java.util.List;
import java.util.Map;

public class AppConfig{
    private Integer id;

    private String key;

    private String value;

    private Integer appId;

    private List<Map<String,Object>> appConfigs;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key == null ? null : key.trim();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value == null ? null : value.trim();
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public List<Map<String, Object>> getAppConfigs() {
        return appConfigs;
    }

    public void setAppConfigs(List<Map<String, Object>> appConfigs) {
        this.appConfigs = appConfigs;
    }
}