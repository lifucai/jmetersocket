package com.test.socket.jmeter;

import com.test.socket.util.Location;
import com.test.socket.util.MsgSend;
import com.test.socket.util.NioConnect;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.mina.core.session.IoSession;

import java.text.DecimalFormat;

public class SinglePointSend {

    private static IoSession session;

    //解析机或者分路网关ip
    private static String ip = "xxx";
    private static String port = "xxx";
    private static String msg_0200 = "7E 02 00 00 1A 06 00 00 00 36 00 00 07 00 00 00 00 00 00 00 03 02 6A F8 66 06 91 05 B8 27 22 03 E8 00 DA 19 04 19 11 20 42 58 7E";



    public static void main(String args[]) {
        //经度
        double lng = 103.9842;
        //纬度
        double lat = 30.628459;
        //速度是km/h
        int speed = 100;
        //终端号
        String imei = "011234028888";

        //十六进制报警位
        String alarm = "00 00 00 02";


        //转换经纬度
        String hexLngStr = decimalFormat(lng);
        String hexLatStr = decimalFormat(lat);

        //速度转换
        String hexSpeedStr = Integer.toHexString(speed*10);
        hexSpeedStr = supplementZero(hexSpeedStr,4);
        hexSpeedStr = MsgSend.formatting(hexSpeedStr).toUpperCase();


        //替换报警位
        msg_0200 = msg_0200.substring(0, 39)+alarm+msg_0200.substring(50);
        //替换速度
        msg_0200 = msg_0200.substring(0,93)+hexSpeedStr+msg_0200.substring(98);


        //建立连接
        session = NioConnect.getSession(ip, port);
        System.out.println(hexLatStr+","+hexLngStr);
        String response = MsgSend.sendTraceMsg(session, imei, msg_0200, hexLngStr, hexLatStr, true);
        session.closeNow();

    }

    /**
     * @param data
     * @return
     */
    private static String decimalFormat(double data) {
        DecimalFormat format = new DecimalFormat("0.000000");
        String str = format.format(data).replace(".","");
        String hexStr = Integer.toHexString(Integer.parseInt(str));
        String supStr = supplementZero(hexStr,8);
        return supStr;
    }

    /**
     * 经纬度转成十六进制后4个字节补位
     * @param hexStr
     * @return
     */
    private static String supplementZero(String hexStr,int length){
        if(hexStr == null || length<hexStr.length()){
            throw new RuntimeException();
        }
        int hexLength = hexStr.length();
        if(hexStr != null && hexStr.length() < length){
            for(int i = 0;i<length-hexLength;i++){
                hexStr = "0"+hexStr;
            }
        }
        return hexStr;
    }

}
