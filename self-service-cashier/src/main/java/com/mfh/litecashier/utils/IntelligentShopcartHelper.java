package com.mfh.litecashier.utils;

/**
 * Created by bingshanguxue on 5/5/16.
 */
public class IntelligentShopcartHelper extends ShopcartHelper {
    private static IntelligentShopcartHelper instance;

    public static IntelligentShopcartHelper getInstance() {
        if (instance == null) {
            synchronized (IntelligentShopcartHelper.class) {
                if (instance == null) {
                    instance = new IntelligentShopcartHelper();
                }
            }
        }
        return instance;
    }
}
