package com.manfenjiayuan.pda_wholesaler.database.entity;

import com.mfh.comn.annotations.Table;
import com.mfh.comn.bean.ILongId;
import com.mfh.framework.core.MfhEntity;

/**
 * 发货拣货－商品
 * Created by Nat.ZZN(bingshanguxue) on 15-09-06..
 */
@Table(name="pda_wholesaler_invio_pickgoods")
public class InvIoPickGoodsEntity extends MfhEntity<Long> implements ILongId{
//    private String id;
    private Long proSkuId;//
    private Long chainSkuId;//
    private String productName;//商品名称
    private Double price;//价格
    private Double amount;//总价
    private String unitSpec;//单位
    private String barcode;//条码

    private Long providerId;//供应商编号
    private Integer isPrivate;//（0：不是 1：是）

    private Double quantityCheck;//实际签收数量，默认与单据数量一致

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getPrice() {
        if (price == null){
            return 0D;
        }
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getUnitSpec() {
        return unitSpec;
    }

    public void setUnitSpec(String unitSpec) {
        this.unitSpec = unitSpec;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Long getProSkuId() {
        return proSkuId;
    }

    public void setProSkuId(Long proSkuId) {
        this.proSkuId = proSkuId;
    }

    public Long getChainSkuId() {
        return chainSkuId;
    }

    public void setChainSkuId(Long chainSkuId) {
        this.chainSkuId = chainSkuId;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public Integer getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Integer isPrivate) {
        this.isPrivate = isPrivate;
    }

    public Double getQuantityCheck() {
        if (quantityCheck == null){
            return 0D;
        }
        return quantityCheck;
    }

    public void setQuantityCheck(Double quantityCheck) {
        this.quantityCheck = quantityCheck;
    }
}
