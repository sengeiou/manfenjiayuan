package com.mfh.litecashier.hardware.SMScale;

import java.io.Serializable;

/**
 * Created by bingshanguxue on 4/21/16.
 */
public class SMGoods implements Serializable{
    private String barcode; //PLU码，最小商品库存单元的条形码
    private String name; // 商品名称
    private String unit; // 单位，如箱、瓶
    private Double price; // 单价，单位是分。
    private int category;//分类

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
