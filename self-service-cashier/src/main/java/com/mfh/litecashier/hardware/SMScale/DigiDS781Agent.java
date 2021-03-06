package com.mfh.litecashier.hardware.SMScale;

import com.alibaba.fastjson.JSON;
import com.mfh.framework.BizConfig;
import com.mfh.framework.core.logger.ZLogger;
import com.mfh.framework.core.utils.DataConvertUtil;
import com.mfh.framework.helper.SharedPreferencesManager;

/**
 * Created by bingshanguxue on 5/18/16.
 */
public class DigiDS781Agent {
    public static String PORT_SCALE_DS781 = "/dev/ttymxc1";
    public static String BAUDRATE_SCALE_DS781 = "9600";

    private static final String PREF_NAME = "pref_scale_ds781";
    private static final String PK_SCALE_PORT = "pref_scale_port";
    private static final String PK_SCALE_BAUDRATE = "pref_scale_baudrate";


    private static final int DATAFORMAT_A_TOTAL_LENGTH = 37;
    public static final String HEX_TERMINATION_CR = "0D";//CR The end of data 0x0d
    public static final String HEX_TERMINATION_LF = "0A";//LF The end of Text 0x0a
    public static final String HEX_HEADER_0 = "30";//‘0’ Net Price 0x30
    public static final String HEX_HEADER_4 = "34";//‘4’ Tare Price 0x34
    public static final String HEX_HEADER_U = "55";//‘U’ Unit Price 0x55
    public static final String HEX_HEADER_T = "54";//‘T’ Total Price 0x54

    static {
        initialize();
    }

    public static void initialize() {
        PORT_SCALE_DS781 = SharedPreferencesManager.getText(
                PREF_NAME, PK_SCALE_PORT, "");
        BAUDRATE_SCALE_DS781 = SharedPreferencesManager.getText(
                PREF_NAME, PK_SCALE_BAUDRATE, "9600");
    }

    public static void setPort(String port) {
        SharedPreferencesManager.set(PREF_NAME, PK_SCALE_PORT, port);
    }

    public static void setBaudrate(String baudrate) {
        SharedPreferencesManager.set(
                PREF_NAME, PK_SCALE_BAUDRATE, baudrate);
    }

    /**
     * DataFormat
     * <ol>
     * Without additional parity (Total 37 Bytes )
     * </ol>
     * <p/>
     * <p/>
     * //[
     * 43 0D
     * // 30 30 30 2E 30 30 30 0D
     * // 34 30 30 2E 30 30 30 0D
     * // 55 30 30 30 2E 30 30 0D
     * // 54 30 30 30 30 2E 30 30 0D
     * // 0A
     * ]
     * <p/>
     * // [
     * // C CR                      （StatusFlag+WeightConditionFlag+CR）
     * // 0 0 0 . 0 0 0 CR     1+6+1（HeaderCode+ NetWeight+ CR）
     * // 4 0 0 . 0 0 0 CR     1+6+1（HeaderCode+ TareWeight+CR）
     * // U 0 0 0 . 0 0 CR     1+6+1（HeaderCode+ UnitPrice+ CR）
     * // T 0 0 0 0 . 0 0 CR   1+7+1（HeaderCode+ TotalPrice+CR）
     * // LF                        （LF）
     * // ]
     */
    public static DS781A parseData(byte[] data) {
        if (data == null) {
//            ZLogger.d("解析寺冈电子秤串口数据失败，空数据");
            return null;
        }

        if (data.length == 37) {
            return parseFormat37(data);
        } else if (data.length == 35) {
            return parseFormat35(data);
        }

//        ZLogger.e(String.format("解析寺冈电子秤串口数据失败:<%d><%s>",
//                data.length, DataConvertUtil.ByteArrToHex(data)));
        return null;
    }

    private static DS781A parseFormatA(byte[] data) {
        String rswStr = new String(data);
//        rswStr.split("")
        byte statusFlag = data[0];
        byte weightConditionFlag = data[1];

        byte[] netWeightData = new byte[6];
        System.arraycopy(data, 4, netWeightData, 0, 6);

        byte[] tareWeightData = new byte[6];
        System.arraycopy(data, 12, tareWeightData, 0, 6);

        byte[] unitpriceData = new byte[6];
        System.arraycopy(data, 20, unitpriceData, 0, 6);

        byte[] totalPriceData = new byte[7];
        System.arraycopy(data, 28, totalPriceData, 0, 6);


        return null;
    }

    private static DS781A parseFormat37(byte[] data) {
        try {
            //0-2
            byte statusFlag = data[0];
            byte weightConditionFlag = data[1];

            //3-10
            byte[] bNetWeight = new byte[6];
            System.arraycopy(data, 4, bNetWeight, 0, 6);
            //11-18
            byte[] bTareWeight = new byte[6];
            System.arraycopy(data, 12, bTareWeight, 0, 6);
            //19-26
            byte[] bUnitPrice = new byte[6];
            System.arraycopy(data, 20, bUnitPrice, 0, 6);
            //27-35
            byte[] bTotalPrice = new byte[7];
            System.arraycopy(data, 28, bTotalPrice, 0, 7);

            return generateDS781A(bNetWeight, bTareWeight, bUnitPrice, bTotalPrice);
        } catch (Exception e) {
            ZLogger.e(e.toString());
            return null;
        }
    }

    /**
     * without statusflag and weightConditionFlag
     *
     * @param data <0D
     *             30 30 30 2E 30 37 30 0D
     *             34 30 30 2E 30 30 30 0D
     *             55 30 30 30 2E 30 30 0D
     *             54 30 30 30 30 2E 30 30 0D
     *             0A >
     */
    private static DS781A parseFormat35(byte[] data) {
//        ZLogger.d(String.format("解析寺冈电子秤串口数据:<%s>, hex:<%s>, 长度为: %d",
//                new String(data),
//                DataConvertUtil.ByteArrToHex(data), data.length));

        try {
            byte[] bNetWeight = new byte[6];
            for (int i = 0; i < 6; i++) {
                bNetWeight[i] = data[2 + i];
            }
            byte[] bTareWeight = new byte[6];
            for (int i = 0; i < 6; i++) {
                bTareWeight[i] = data[10 + i];
            }
            byte[] bUnitPrice = new byte[6];
            for (int i = 0; i < 6; i++) {
                bUnitPrice[i] = data[18 + i];
            }
            byte[] bTotalPrice = new byte[7];
            for (int i = 0; i < 7; i++) {
                bTotalPrice[i] = data[26 + i];
            }

            return generateDS781A(bNetWeight, bTareWeight, bUnitPrice, bTotalPrice);
        } catch (Exception e) {
//            ZLogger.e(String.format("解析寺冈电子秤串口数据失败:<%d><%s>/n%s",
//                    data.length, DataConvertUtil.ByteArrToHex(data), e.toString()));
            return null;
        }
    }

    private static DS781A generateDS781A(byte[] bNetWeight, byte[] bTareWeight, byte[] bUnitPrice, byte[] bTotalPrice) {
        try {
            String sNetWeight = DataConvertUtil.ByteArrToHex(bNetWeight, "");
            String sTareWeight = DataConvertUtil.ByteArrToHex(bTareWeight, "");
            String sUnitPrice = DataConvertUtil.ByteArrToHex(bUnitPrice, "");
            String sTotalPrice = DataConvertUtil.ByteArrToHex(bTotalPrice, "");
//            ZLogger.d(String.format("netWeight:%s, tareWeight=%s, unitPrice=%s, totalPrice=%s",
//                    sNetWeight, sTareWeight, sUnitPrice, sTotalPrice));

            String sNetWeight2 = DataConvertUtil.hexStr2Str(sNetWeight);
            String sTareWeight2 = DataConvertUtil.hexStr2Str(sTareWeight);
            String sUnitPrice2 = DataConvertUtil.hexStr2Str(sUnitPrice);
            String sTotalPrice2 = DataConvertUtil.hexStr2Str(sTotalPrice);

//            ZLogger.d(String.format("netWeight:%s, tareWeight=%s, unitPrice=%s, totalPrice=%s",
//                    sNetWeight2, sTareWeight2, sUnitPrice2, sTotalPrice2));

            DS781A ds781A = new DS781A();
            ds781A.setNetWeight(Double.parseDouble(sNetWeight2));
            ds781A.setTareWeight(Double.parseDouble(sTareWeight2));
            ds781A.setUnitPrice(Double.parseDouble(sUnitPrice2));
            ds781A.setTotalPrice(Double.parseDouble(sTotalPrice2));
            if (!BizConfig.RELEASE) {
                ZLogger.d(String.format("ds781A:%s", JSON.toJSONString(ds781A)));
            }

            return ds781A;
        } catch (Exception e) {
            ZLogger.d("generateDS781A failed, " + e.toString());
            return null;
        }
    }

}
