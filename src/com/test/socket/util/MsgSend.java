package com.test.socket.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.session.IoSession;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MsgSend {



    /**
     * 发送、接收报文
     *
     * @param imei
     * @param msg
     * @return
     */
    public static String sendTraceMsg(IoSession session, String imei, String msg, String lng, String lat, boolean out) {

        String respone = "";
        try {

            long startTime = System.currentTimeMillis();
            SimpleDateFormat msgformatter = new SimpleDateFormat("yy MM dd HH mm ss");
            Date currentTime = new Date();
            String imeiStr = formatting(imei);
            String latStr = formatting(lat);
            String lngStr = formatting(lng);

            //拼装报文
            String msg1 = CreateMsg.AssemblyMessage_0200(imeiStr, msg, msgformatter.format(currentTime), lngStr , latStr);
            //计算校验位及替换特殊字符
            byte[] msgByte = CreateMsg.edit7d7e(CreateMsg.transfer2Bytes(msg1));
            // 发送
            session.write(msgByte).awaitUninterruptibly();
            //打印发送报文
            String msgSend = CreateMsg.getString(CreateMsg.byteArrayToHexStr(msgByte), " ");
            long sendTime = System.currentTimeMillis() - startTime;
            if (out) {
                System.out.println("发送报文:" + msgSend);
                //getLogger().error(String.format("imei:%s, 报文内容:%s, 报文发送时间:%s ms", imeiStr, CreateMsg.byteArrayToHexStr(msgByte), sendTime));
            }

            // 接收
            ReadFuture readFuture = session.read();
            if (readFuture.awaitUninterruptibly(3, TimeUnit.SECONDS)) {
                byte[] repByte = (byte[]) readFuture.getMessage();
                char[] chArr = Hex.encodeHex(repByte);
                String str = "";
                for (char c : chArr) {
                    str += String.valueOf(c).toUpperCase();
                }

                for (int i = 0; i < str.length(); i++) {
                    if (i % 2 == 0) {
                        String tmp = str.substring(i, i + 2);
                        respone = respone + " " + tmp;
                    }
                }
                respone = respone.substring(1, respone.length());
                if (out) {
                    System.out.println("响应报文:" + respone);
                }

            } else {
                // 读超时
                respone = "code=0,TimeOut(3s)!";
                if (out) {
                    System.out.println("响应报文:" + respone);
                    long total_time = System.currentTimeMillis() - startTime;
                }
            }
        } catch (Exception e) {
        }

        return respone;
    }


    public static String formatting(String str) {
        StringBuilder sBuilder = new StringBuilder();
        for (int i = 0; i < str.length(); i += 2) {
            String tmp = str.substring(i, i + 2);
            sBuilder.append(tmp).append(" ");
        }
        return sBuilder.toString().trim();
    }
}
