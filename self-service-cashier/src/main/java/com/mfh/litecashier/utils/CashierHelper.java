package com.mfh.litecashier.utils;

import android.graphics.Color;
import android.graphics.Typeface;

import com.alibaba.fastjson.JSONArray;
import com.amulyakhare.textdrawable.TextDrawable;
import com.bingshanguxue.cashier.CashierFactory;
import com.bingshanguxue.cashier.database.entity.PosOrderEntity;
import com.bingshanguxue.cashier.database.entity.PosProductEntity;
import com.bingshanguxue.cashier.database.service.PosOrderItemService;
import com.bingshanguxue.cashier.database.service.PosOrderPayService;
import com.bingshanguxue.cashier.database.service.PosOrderService;
import com.bingshanguxue.cashier.database.service.PosProductService;
import com.mfh.comn.bean.TimeCursor;
import com.mfh.framework.core.logger.ZLogger;
import com.mfh.framework.core.utils.DensityUtil;
import com.mfh.framework.core.utils.StringUtils;
import com.mfh.framework.helper.SharedPreferencesManager;
import com.mfh.framework.login.logic.MfhLoginService;
import com.mfh.litecashier.CashierApp;
import com.mfh.litecashier.bean.PosCategory;
import com.mfh.litecashier.bean.wrapper.HangupOrder;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收银帮助类
 * Created by Nat.ZZN(bingshanguxue) on 15/9/9.
 */
public class CashierHelper {

    /**
     * 删除订单,同时删除对应订单的商品明细和支付记录
     *
     * @param orderEntity 订单
     * @return
     */
    public static void deleteCashierOrder(PosOrderEntity orderEntity) {
        if (orderEntity == null) {
            return;
        }
        PosOrderService.get().deleteById(String.valueOf(orderEntity.getId()));
        //删除订单明细
        PosOrderItemService.get().deleteBy(String.format("orderId = '%d'", orderEntity.getId()));
        //删除支付记录
        PosOrderPayService.get().deleteBy(String.format("orderId = '%d'", orderEntity.getId()));
    }



    /**
     * 合并订单,将相同交易编号的订单合并
     * 适用场景：挂单
     */
    public static List<HangupOrder> mergeHangupOrders(Integer bizType) {
        List<PosOrderEntity> orderEntities = CashierFactory.fetchActiveOrderEntities(bizType,
                PosOrderEntity.ORDER_STATUS_HANGUP);
        if (orderEntities == null || orderEntities.size() <= 0){
            return null;
        }

        Map<String, HangupOrder> mergeOrderMap = new HashMap<>();
        for (PosOrderEntity orderEntity : orderEntities) {
            String orderTradeNo = orderEntity.getBarCode();
            if (StringUtils.isEmpty(orderTradeNo)) {
                continue;
            }

            HangupOrder hangupOrder = mergeOrderMap.get(orderTradeNo);
            if (hangupOrder != null) {
                hangupOrder.setFinalAmount(hangupOrder.getFinalAmount() + orderEntity.getFinalAmount());
                hangupOrder.setUpdateDate(orderEntity.getUpdatedDate());
            } else {
                hangupOrder = new HangupOrder();
                hangupOrder.setOrderTradeNo(orderTradeNo);
                hangupOrder.setFinalAmount(orderEntity.getFinalAmount());
                hangupOrder.setUpdateDate(orderEntity.getUpdatedDate());
            }
            mergeOrderMap.put(orderTradeNo, hangupOrder);
        }

        List<HangupOrder> mergeOrderList = new ArrayList<>();
        mergeOrderList.addAll(mergeOrderMap.values());
        return mergeOrderList;
    }


    /**
     * 清除旧数据
     *
     * @param saveDate 保存的天数
     */
    public static void clearOldPosOrder(int saveDate) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0 - saveDate);//
        String expireCursor = TimeCursor.InnerFormat.format(calendar.getTime());
        ZLogger.d(String.format("订单过期时间(%s)保留最近30天数据。", expireCursor));

        String lastUpdateCursor = SharedPreferencesHelper.getPosOrderLastUpdate();
        ZLogger.d(String.format("last posorder upload datetime(%s)。", lastUpdateCursor));

        if (!StringUtils.isEmpty(lastUpdateCursor)) {
            //得到指定模范的时间
            try {
                Date d1 = TimeCursor.InnerFormat.parse(lastUpdateCursor);
                Date d2 = TimeCursor.InnerFormat.parse(expireCursor);
//            Date d2 = new Date();
                if (d2.compareTo(d1) > 0) {
                    ZLogger.d("订单过期时间大于上次更新时间，暂不清除。");
                    return;
                }
            } catch (ParseException e) {
//            e.printStackTrace();
                ZLogger.e(e.toString());
            }
        }
        List<PosOrderEntity> entityList = PosOrderService.get()
                .queryAllBy(String.format("updatedDate < '%s'", expireCursor));
        if (entityList != null && entityList.size() > 0) {
            for (PosOrderEntity entity : entityList) {
                PosOrderItemService.get().deleteBy(String.format("orderId = '%d'", entity.getId()));
                PosOrderPayService.get().deleteBy(String.format("orderId = '%d'", entity.getId()));
                //删除订单
//                PosOrderService.get().deleteById(String.valueOf(entity.getBarCode()));
            }

            //清除订单
            PosOrderService.get().deleteBy(String.format("updatedDate < '%s'", expireCursor));
            ZLogger.d(String.format("清除过期订单数据(%s)。", expireCursor));
        } else {
            ZLogger.d(String.format("暂无过期订单数据需要清除(%s)。", expireCursor));
        }
    }

    /**
     * 查询商品
     *
     * @param barCode 条形码
     */
    public static PosProductEntity findProduct(String barCode) {
        if (StringUtils.isEmpty(barCode)) {
            return null;
        }

        List<PosProductEntity> entityList = PosProductService.get()
                .queryAllByDesc(String.format("barcode = '%s' and tenantId = '%d'",
                        barCode, MfhLoginService.get().getSpid()));
        if (entityList != null && entityList.size() > 0) {
            ZLogger.d(String.format("找到%d个商品:%s", entityList.size(), barCode));
            return entityList.get(0);
        } else {
            ZLogger.d("未找到商品:" + barCode);
        }

        return null;
    }

    /**
     * 读取前台类目缓存
     */
    public static List<PosCategory> readFrontCatetoryCache() {
        List<PosCategory> entityList = new ArrayList<>();
        //读取缓存，如果有则加载缓存数据，否则重新加载类目；应用每次启动都会加载类目
        String publicCateCache = ACacheHelper.getAsString(ACacheHelper.CK_PUBLIC_FRONT_CATEGORY);
        String customCateCache = ACacheHelper.getAsString(ACacheHelper.CK_CUSTOM_FRONT_CATEGORY);
        List<PosCategory> publicData = JSONArray.parseArray(publicCateCache, PosCategory.class);
        List<PosCategory> customData = JSONArray.parseArray(customCateCache, PosCategory.class);

        if (publicData != null) {
            entityList.addAll(publicData);
        }
        if (customData != null) {
            entityList.addAll(customData);
        }

        return entityList;
    }

    /**
     * 购物车数字
     */
    public static TextDrawable createFabDrawable(int number) {
        if (number > 99) {
            //最多显示两位
            number = 99;
        } else if (number < 0) {
            number = 0;
        }

        return TextDrawable.builder()
                .beginConfig()
                .textColor(Color.WHITE)
                .useFont(Typeface.DEFAULT)
                .toUpperCase()
                .width(DensityUtil.dip2px(CashierApp.getAppContext(), 60))  // width in px
                .height(DensityUtil.dip2px(CashierApp.getAppContext(), 60)) // height in px
                .fontSize(DensityUtil.sp2px(CashierApp.getAppContext(), 50))/* size in px */
                .endConfig()
                .buildRect(String.valueOf(number), Color.TRANSPARENT);
    }

    /**
     * 获取指定长度的设备终端编号,左对齐，不足右补空格(0)
     */
    public static String getTerminalId(int length) {
        String terminalId = SharedPreferencesManager.getTerminalId();

        StringBuilder sb = new StringBuilder();
        sb.append(terminalId);
        int len = length - terminalId.length();
        if (len > 0) {
            for (int i = 0; i < len; i++) {
                sb.append("0");
            }
        }

        return sb.toString();
    }

    /**
     * 获取指定长度的操作员编号,左对齐，不足右补空格(0)
     */
    public static String getOperateId(int length) {
        Long guid = MfhLoginService.get().getCurrentGuId();
        String operateId = guid != null ? String.valueOf(guid) : "";

        StringBuilder sb = new StringBuilder();
        sb.append(operateId);
        int len = length - operateId.length();
        if (len > 0) {
            for (int i = 0; i < len; i++) {
                sb.append("0");
            }
        }

        return sb.toString();
    }




}
