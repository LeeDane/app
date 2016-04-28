package com.leedane.cn.util;

import java.io.Serializable;
import java.util.Map;

/**
 * 系列化后的Map集合封装
 * Created by LeeDane on 2016/4/25.
 */
public class SerializableMap implements Serializable {
    private Map<String,Object> map;

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}
