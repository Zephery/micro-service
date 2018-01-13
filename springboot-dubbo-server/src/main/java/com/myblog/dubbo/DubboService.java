package com.myblog.dubbo;

/**
 * @author Zephery
 * @since 2018/1/13 15:57
 */
public interface DubboService {
    /**
     * 方法耗时
     *
     * @param methodName
     * @param time
     */
    public void insertMethodTime(String methodName, Long time);

}