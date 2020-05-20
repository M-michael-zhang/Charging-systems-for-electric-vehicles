package cn.zy.charg.util;

import com.alibaba.fastjson.serializer.SerializerFeature;
import net.sf.json.JsonConfig;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JsonUtil {
    public static final String toJson(Object obj) {
        return toNetSFJson(obj);
    }

    private static String toNetSFJson(Object obj) {
        JsonConfig config = new JsonConfig();
        config.registerJsonValueProcessor(Date.class,
                new DateJsonValueProcessor("yyyy-MM-dd hh:mm:ss"));
        config.registerJsonValueProcessor(Timestamp.class,
                new DateJsonValueProcessor("yyyy-MM-dd hh:mm:ss"));
        String objStr = net.sf.json.JSONObject.fromObject(obj, config)
                .toString();
        return objStr;
    }

    public static final String toFastJson(Object obj) {
        String objStr = com.alibaba.fastjson.JSONObject.toJSONString(obj,
                SerializerFeature.WriteDateUseDateFormat,
                SerializerFeature.WriteMapNullValue);
        return objStr;
    }

}
