package com.mfh.framework.login;

import com.mfh.comn.net.data.IResponseData;
import com.mfh.framework.MfhApplication;
import com.mfh.framework.core.logger.ZLogger;
import com.mfh.framework.network.NetWorkUtil;
import com.mfh.framework.core.utils.StringUtils;
import com.mfh.framework.login.logic.Callback;
import com.mfh.framework.login.logic.MfhLoginService;
import com.mfh.framework.api.MfhApi;
import com.mfh.framework.net.AfinalFactory;
import com.mfh.framework.net.NetCallBack;
import com.mfh.framework.net.NetFactory;

import net.tsz.afinal.http.AjaxParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 保存登录相关信息
 * Created by Nat.ZZN on 16/1/25.
 */
public class MfhUserManager {

    //当前登录用户功能模块
    private static List<String> mCurrentModules = new ArrayList<>();

    private static MfhUserManager instance = null;

    /**
     * 获取实例
     *
     * @return
     */
    public static MfhUserManager getInstance() {
        if (instance == null) {
            synchronized (MfhUserManager.class) {
                if (instance == null) {
                    instance = new MfhUserManager();
                }
            }
        }
        return instance;
    }


    /**
     * 退出登录
     * */
    public void logout(final Callback callback) {
        if (!NetWorkUtil.isConnect(MfhApplication.getAppContext())
                || !MfhLoginService.get().haveLogined()) {
            if (callback != null) {
                callback.onError(-1, "网络未连接");
            }
            return;
        }

        String sessionId = MfhLoginService.get().getCurrentSessionId();
        if (StringUtils.isEmpty(sessionId)) {
            ZLogger.d("会话已经失效");
            if (callback != null) {
                callback.onSuccess();
            }
            return;
        }

        AjaxParams params = new AjaxParams();
        params.put(NetFactory.KEY_JSESSIONID, sessionId);
        AfinalFactory.postDefault(MfhApi.URL_LOGOUT, params,
                new NetCallBack.NormalNetTask<String>(String.class) {
                    @Override
                    public void processResult(IResponseData rspData) {
//                      {"code":"0","msg":"退出成功","version":"1","data":""}
                        ZLogger.d("退出登录成功");
                        if (callback != null) {
                            callback.onSuccess();
                        }

                        //TODO 注销消息桥，发广播。。。
                    }

                    @Override
                    protected void processFailure(Throwable t, String errMsg) {
                        super.processFailure(t, errMsg);
                        ZLogger.d("退出登录失败：" + errMsg);
                        if (callback != null) {
                            callback.onError(-2, errMsg);
                        }
                    }
                });
    }


    public void updateModules(){
        String[] moduleNames = MfhLoginService.get().getModuleNameArray();
        if (moduleNames != null){
            mCurrentModules = Arrays.asList(moduleNames);
        }
        else{
            mCurrentModules = new ArrayList<>();
        }
    }

    public boolean containsModule(String moduleName){
        if (mCurrentModules == null){
            return false;
        }

        return mCurrentModules.contains(moduleName);
    }





}
