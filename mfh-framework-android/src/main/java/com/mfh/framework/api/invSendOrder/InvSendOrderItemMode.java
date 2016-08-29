package com.mfh.framework.api.invSendOrder;

import com.mfh.comn.net.data.IResponseData;
import com.mfh.comn.net.data.RspBean;
import com.mfh.framework.MfhApplication;
import com.mfh.framework.anlaysis.logger.ZLogger;
import com.mfh.framework.mvp.OnPageModeListener;
import com.mfh.framework.network.NetCallBack;
import com.mfh.framework.network.NetProcessor;

/**
 * 采购订单明细
 * Created by bingshanguxue on 16/3/17.
 */
public class InvSendOrderItemMode {

    public void loadOrderItems(Long id, final OnPageModeListener<InvSendOrderItem> listener) {
        if (listener != null) {
            listener.onProcess();
        }

        if (id == null){
            if (listener != null) {
                listener.onError("缺少id参数");
            }
            return;
        }

        NetCallBack.NetTaskCallBack responseCallback = new NetCallBack.NetTaskCallBack<InvSendOrderItemBrief,
                NetProcessor.Processor<InvSendOrderItemBrief>>(
                new NetProcessor.Processor<InvSendOrderItemBrief>() {
                    @Override
                    public void processResult(IResponseData rspData) {

                        if (rspData == null) {
                            if (listener != null) {
                                 listener.onSuccess(null, null);
                            }
                            return;
                        }
                        //com.mfh.comn.net.data.RspBean cannot be cast to com.mfh.comn.net.data.RspValue
                        RspBean<InvSendOrderItemBrief> retValue = (RspBean<InvSendOrderItemBrief>) rspData;
                        InvSendOrderItemBrief orderDetail = retValue.getValue();

                        if (listener != null) {
                            listener.onSuccess(null, orderDetail.getItems());
                        }
                    }

                    @Override
                    protected void processFailure(Throwable t, String errMsg) {
                        super.processFailure(t, errMsg);
                        ZLogger.d("加载采购订单明细失败：" + errMsg);
                        if (listener != null) {
                            listener.onError(errMsg);
                        }
                    }
                }
                , InvSendOrderItemBrief.class
                , MfhApplication.getAppContext()) {
        };

        InvSendOrderApiImpl.getById(id, responseCallback);
    }
}
