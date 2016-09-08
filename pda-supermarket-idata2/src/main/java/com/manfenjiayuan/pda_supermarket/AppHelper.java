package com.manfenjiayuan.pda_supermarket;


import com.bingshanguxue.pda.database.service.InvCheckGoodsService;
import com.manfenjiayuan.pda_supermarket.database.logic.ShelveService;
import com.mfh.comn.bean.TimeCursor;
import com.mfh.framework.anlaysis.logger.ZLogger;

import java.util.Calendar;

/**
 * 应用程序UI工具包：封装UI相关的一些操作
 * Created by Nat on 2015/5/11.
 */
public class AppHelper {

//    /**
//     * 清空匿名用户账户数据
//     * */
//    public static void resetAnonymousAccountData(){
//
//    }
//
//    /**
//     * 广播微信支付结果
//     * */
//    public static void broadcastWXPayResp(int errCode, String errStr){
//        Bundle extras = new Bundle();
//        extras.putInt(Constants.BROADCAST_KEY_WXPAY_RESP_ERRCODE, errCode);
//        extras.putString(Constants.BROADCAST_KEY_WXPAY_RESP_ERRSTR, errStr);
//
//        if(HybridActivity.getInstance() != null){
//            HybridActivity.getInstance().parseWxpayResp(extras);
//        }
//        UIHelper.sendBroadcast(Constants.BROADCAST_ACTION_WXPAY_RESP);
//
////        No subscribers registered for event class com.mfh.enjoycity.events.WxPayEvent
//        EventBus.getDefault().post(
//                new WxPayEvent(errCode, errStr));
//    }

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

        //清除订单
        InvCheckGoodsService.get().deleteBy(String.format("updatedDate < '%s'", expireCursor));
        ShelveService.get().deleteBy(String.format("updatedDate < '%s'", expireCursor));
    }

}
