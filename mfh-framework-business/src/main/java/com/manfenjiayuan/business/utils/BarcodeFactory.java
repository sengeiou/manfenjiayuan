package com.manfenjiayuan.business.utils;

import com.mfh.framework.anlaysis.logger.ZLogger;
import com.mfh.framework.core.utils.StringUtils;

/**
 * 条码工厂
 * Created by bingshanguxue on 5/11/16.
 */
public class BarcodeFactory {
    public static final String CHAR_ASTERISK = "*";

    /**
     * 过滤指定的字符串：截取指定两个字符间的字符串，不满足条件的返回原字符串
     * 因业务关系可能会出现特殊的条码，首位加上了特殊的字符，需要过滤调。
     */
    public static String filter(String src, String prefix, String suffix) {
//        ZLogger.d(String.format("%s(%s,%s)", src, prefix, suffix));
        if (StringUtils.isEmpty(src) || StringUtils.isEmpty(prefix)) {
            return src;
        }

        try {
            int preIndex = src.indexOf(prefix);
            int suffixIndex = src.lastIndexOf(suffix);//(0, count]
//            ZLogger.d(String.format("%d,%d", preIndex, suffixIndex));
            if (preIndex >= 0 && suffixIndex >= 0 && preIndex <= suffixIndex - 2) {
                return src.substring(preIndex + 1, suffixIndex - 1);
            }
        } catch (Exception e) {
            ZLogger.e("filter failed:" + e.toString());
        }

        return src;
    }

}
