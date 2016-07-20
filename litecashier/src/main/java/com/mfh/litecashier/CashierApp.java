package com.mfh.litecashier;

import android.content.ComponentCallbacks2;

import com.manfenjiayuan.im.IMClient;
import com.mfh.framework.BizConfig;
import com.mfh.framework.MfhApplication;
import com.mfh.framework.core.AppException;
import com.mfh.framework.core.logger.ZLogger;
import com.mfh.framework.helper.SharedPreferencesManager;
import com.mfh.litecashier.utils.ACacheHelper;
import com.mfh.litecashier.utils.AppHelper;
import com.mfh.litecashier.utils.SharedPreferencesHelper;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.io.File;


/**
 * Created by Nat.ZZN(bingshanguxue) on 2015/7/10.
 */
public class CashierApp extends MfhApplication {
    private RefWatcher mRefWatcher;

    @Override
    protected boolean isReleaseVersion() {
        return SharedPreferencesManager.isReleaseVersion();
    }

    @Override
    public void onCreate() {
        AppException.CRASH_FOLDER_PATH = getPackageName() + File.separator + "crash";

        super.onCreate();

        ZLogger.CRASH_FOLDER_PATH = getPackageName() + File.separator + "zlogger";

        mRefWatcher = LeakCanary.install(this);

        if (BizConfig.RELEASE){
//            ZLogger.d("正式版本");
            ZLogger.LOG_ENABLED = true;
            SharedPreferencesHelper.PREF_NAME_PREFIX = SharedPreferencesHelper.RELEASE_PREFIX;
//            Constants.CACHE_NAME = "ACache_Release";
//            DebugHelper.debug();
        }
        else{
//            ZLogger.d("测试版本");
            ZLogger.LOG_ENABLED = true;
            SharedPreferencesHelper.PREF_NAME_PREFIX = SharedPreferencesHelper.DEV_PREFIX;
            ACacheHelper.CACHE_NAME = "ACache_Dev";
//          DebugHelper.debug();
        }

//            DebugHelper.debug();

//        //注册应用id到微信
//        WXAPIFactory.createWXAPI(this, WXConstants.APP_ID, false).registerApp(WXConstants.APP_ID);
//

        int pid = android.os.Process.myPid();
        String processAppName = getProcessName(this, pid);
        // 如果app启用了远程的service，此application:onCreate会被调用2次
        // 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回
        if (processAppName != null && processAppName.equalsIgnoreCase(getPackageName())) {
            //初始化IM模块
            IMClient.getInstance().init(getApplicationContext());
        }

        ZLogger.d(String.format("initialize finished(%s)", processAppName));
        ZLogger.d(String.format("networkType:%s\nwifiMacAddress:%s\nwifiIpAddress:%s\n" +
                "hostAddress:%s\nhostIpAddress:%s", getNetworkType(), getWifiMacAddress(),
                getWifiIpAddress(), getHostAddress(), getHostIpAddress()));
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        //do release operation
        ZLogger.d("onLowMemory");

        AppHelper.clearCache();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        ZLogger.d("onTrimMemory:" + level);

        if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            AppHelper.clearCache();
        }
    }

    @Override public void onTerminate() {
        super.onTerminate();
    }

}
