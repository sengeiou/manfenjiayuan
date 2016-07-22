package com.manfenjiayuan.pda_supermarket.ui.goods;

import com.mfh.framework.api.scGoodsSku.ScGoodsSku;
import com.mfh.framework.mvp.MvpView;

import java.util.List;

/**
 * 商品
 * Created by bingshanguxue on 16/3/21.
 */
public interface IGoodsView extends MvpView {
    void onIGoodsViewProcess();
    void onIGoodsViewError(String errorMsg);
    void onIGoodsViewSuccess(List<ScGoodsSku> scGoodsSkus);
}
