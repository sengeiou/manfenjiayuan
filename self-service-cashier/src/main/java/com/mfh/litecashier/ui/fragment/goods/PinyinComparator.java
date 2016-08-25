package com.mfh.litecashier.ui.fragment.goods;

import com.mfh.framework.anlaysis.logger.ZLogger;

import java.util.Comparator;

public class PinyinComparator implements Comparator<ScGoodsSkuWrapper> {

    @Override
    public int compare(ScGoodsSkuWrapper lhs, ScGoodsSkuWrapper rhs) {
        // TODO Auto-generated method stub
        return sort(lhs, rhs);
    }

    /**
     * */
    private int sort(ScGoodsSkuWrapper lhs, ScGoodsSkuWrapper rhs) {
        try {
//            ZLogger.d(String.format("lhs(%s-%s), rhs(%s-%s)",
//                    lhs.getNameSortLetter(), lhs.getNamePinyin(),
//                    rhs.getNameSortLetter(), rhs.getNamePinyin()));
//        // 获取ascii值
            int lhs_ascii = lhs.getNameSortLetter().toUpperCase().charAt(0);
            int rhs_ascii = rhs.getNameSortLetter().toUpperCase().charAt(0);
            // 判断若不是字母，则排在字母之后
            if (lhs_ascii < 65 || lhs_ascii > 90) {
                if (rhs_ascii >= 65 && rhs_ascii <= 90)
                    return 1;
                else
                    return lhs.getNamePinyin().compareTo(rhs.getNamePinyin());
            } else {
                if (rhs_ascii < 65 || rhs_ascii > 90)
                    return -1;
                else
                    return lhs.getNamePinyin().compareTo(rhs.getNamePinyin());
            }
        } catch (Exception e) {
            ZLogger.e(e.toString());
            return -1;
        }

    }

}