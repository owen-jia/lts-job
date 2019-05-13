package com.github.ltsopensource.admin.support;

import com.github.ltsopensource.core.logger.Logger;
import com.github.ltsopensource.core.logger.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 多线程上下文内容
 * @author: Owen Jia
 * @time: 2019/5/13 16:17
 */
public class ThreadLocalUtil {

    private static ThreadLocal<Map<String, Object>> attrsThreadLocalHolder = new ThreadLocal<Map<String, Object>>();

    private static Logger LOGGER = LoggerFactory.getLogger(ThreadLocalUtil.class);

    public static void setAttr(String key, Object object) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("设置线程属性 key:{}, value: {}", key, object);
        }
        getAttrs().put(key, object);
    }

    public static Object getAttr(String key) {
        Object value = getAttrs().get(key);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("设置线程属性 key:{}, value: {}", key, value);
        }
        return value;
    }

    private static Map<String, Object> getAttrs() {
        Map<String, Object> attrs = attrsThreadLocalHolder.get();
        if (attrs == null) {
            attrs = new HashMap<String, Object>();
        }
        attrsThreadLocalHolder.set(attrs);
        return attrs;
    }

    public static void cleanAttrs() {
        attrsThreadLocalHolder.set(new HashMap<String, Object>());
    }

    public static void removeAttr(String key) {
        getAttrs().remove(key);
    }
}
