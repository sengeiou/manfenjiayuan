package com.manfenjiayuan.pda_supermarket.bean.wrapper;

import java.io.Serializable;

/**
 * Created by kun on 15/9/2.
 */
public class HomeMenu implements Serializable{

    public static final Long OPTION_ID_GOOODS       = 1L;//商品（修改售价和标准库存）
    public static final Long OPTION_ID_PACKAGE      = 2L;//包裹(取快递)
    public static final Long OPTION_ID_STOCK_TAKE   = 3L;//盘点（修改当前库存）
    public static final Long OPTION_ID_DISTRIBUTION = 4L;//收货（商品配送）
    public static final Long OPTION_ID_BIND_GOODS_2_TAGS    = 11L;//商品绑定标签
    public static final Long OPTION_ID_CREATE_INV_RETURNORDER    = 12L;//采购退货
    public static final Long OPTION_ID_CREATE_INV_LOSSORDER    = 13L;//报损订单
    public static final Long OPTION_ID_INV_CONVERT   = 14L;//库存转换

    public static final Long OPTION_ID_ALPHA    = 21L;//


    private Long id;
    private String name;
    private int resId;

    public HomeMenu(Long id, String name, int resId) {
        this.id = id;
        this.name = name;
        this.resId = resId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
