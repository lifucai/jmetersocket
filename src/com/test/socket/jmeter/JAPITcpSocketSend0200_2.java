package com.test.socket.jmeter;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.test.socket.util.Location;
import com.test.socket.util.NioConnect;
import org.apache.commons.codec.binary.Hex;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.session.IoSession;

import com.test.socket.util.CreateMsg;

/**
 * 报文格式按照分段位置传入，再拼装
 *
 * @author xiejie
 */
public class JAPITcpSocketSend0200_2 extends AbstractJavaSamplerClient {

    IoSession session;
    private SampleResult sr;

    public org.apache.log.Logger getLogger() {
        return super.getLogger();

    }

    /**
     * 定义java方法入参 params.addArgument("num1","");表示入参名字叫num1，默认值为空。 设置可用参数及的默认值；
     */
    public Arguments getDefaultParameters() {
        Arguments params = new Arguments();
        params.addArgument("ip", "xxxx");
        params.addArgument("port", "xxxx");
        params.addArgument("sampleLabel", "0200");
        params.addArgument("imei", "${imei}");
        params.addArgument("msg", "${msg}");
        params.addArgument("out", "false");
        params.addArgument("lng","${lng}");
        params.addArgument("lat","${lat}");
        return params;
    }

    /**
     * 初始化
     */
    public void setupTest(JavaSamplerContext arg0) {
        String ip = arg0.getParameter("ip");
        String port = arg0.getParameter("port");
        session = NioConnect.getSession(ip, port);
        if (session == null) {
            getLogger().error("线程setup时建立tcp链接失败！！！");
        }
    }

    /**
     * 运行
     */
    @Override
    public SampleResult runTest(JavaSamplerContext arg0) {
        String sampleLabel = arg0.getParameter("sampleLabel");
        String imei = arg0.getParameter("imei");
        String msg = arg0.getParameter("msg");
        String lng = arg0.getParameter("lng");
        String lat = arg0.getParameter("lat");

        boolean out = Boolean.parseBoolean(arg0.getParameter("out"));

        sr = new SampleResult();
        sr.setSampleLabel(sampleLabel);
        try {
            sr.sampleStart(); // 记录程序执行时间，以及执行结果
            // 发送数据
            String result = sendMsg(imei, msg, lng, lat, out);
            if (result != null && result.length() > 0) {
                sr.setResponseData(result, "UTF-8");
                sr.setDataType(SampleResult.TEXT);
                if ("code=0".equals(result.substring(0, 6))) {
                    sr.setSuccessful(false);
                } else {
                    sr.setSuccessful(true);
                }
            }
        } catch (Throwable e) {
            sr.setSuccessful(false);
            e.printStackTrace();
        } finally {
            sr.sampleEnd();
        }

        return sr;
    }

    /**
     * 结束
     */
    public void teardownTest(JavaSamplerContext arg0) {
        session.getService().dispose();
    }


    /**
     * 发送、接收报文
     *
     * @param imei
     * @param msg
     * @return
     */
    public String sendMsg(String imei, String msg, String lng, String lat, boolean out) {

        String respone = "";
        try {

            long startTime = System.currentTimeMillis();
            SimpleDateFormat msgformatter = new SimpleDateFormat("yy MM dd HH mm ss");
            Date currentTime = new Date();
            String imeiStr = getImei(imei);
            String latStr = getImei(lat);
            String lngStr = getImei(lng);

            //拼装报文
            String msg1 = CreateMsg.AssemblyMessage_0200(imeiStr, msg, msgformatter.format(currentTime), lngStr , latStr);
            //计算校验位及替换特殊字符
            byte[] msgByte = CreateMsg.edit7d7e(CreateMsg.transfer2Bytes(msg1));
            // 发送
            session.write(msgByte).awaitUninterruptibly();
            //打印发送报文
            String msgSend = CreateMsg.getString(CreateMsg.byteArrayToHexStr(msgByte), " ");
            sr.setSamplerData("发送报文:" + msgSend);
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
                    //getLogger().error(String.format("响应报文超过3S, sim:%s",imei));
                    //System.out.println("响应报文:" + respone);

                }

            } else {
                // 读超时
                respone = "code=0,TimeOut(3s)!";
                if (out) {
                    //System.out.println("响应报文:" + respone);
                    long total_time = System.currentTimeMillis() - startTime;
                    getLogger().error(String.format("响应报文超过3S,响应时间为：%s ms, 报文内容:%s, imei:%s", total_time, CreateMsg.byteArrayToHexStr(msgByte), imei));
                }
            }
        } catch (Exception e) {
            getLogger().error(e.getMessage());
        }

        return respone;
    }

    private static String getImei(String imei) {
        StringBuilder sBuilder = new StringBuilder();
        for (int i = 0; i < imei.length(); i += 2) {
            String tmp = imei.substring(i, i + 2);
            sBuilder.append(tmp).append(" ");
        }
        return sBuilder.toString().trim();
    }

    /**
     * 测试调试
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        String location = "/Users/xxxxx/Desktop/test.csv";

        ArrayList<Location> arrayList = new ArrayList<>();
        try {
            File file = new File(location);
            InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file));
            BufferedReader bf = new BufferedReader(inputReader);

            int i = 1;
            String str = null;
            while ((str = bf.readLine()) != null) {
                System.out.println(str);
                if(i==100){
                    break;
                }
                String[] strArr = str.split(",");
                if(strArr != null && strArr.length ==2){
                    Location locationObj = new Location();
                    locationObj.setLng(strArr[0]);
                    locationObj.setLat(strArr[1]);
                    arrayList.add(locationObj);
                    i++;
                }
            }
            bf.close();
            inputReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        JavaSamplerContext arg0 = null;
        int limit =1;
        for (int i=0;i<10;i++) {
//            if(limit>=i){
//                break;
//            }

            Arguments arguments = new Arguments();
            /**
             * 分路网关
             */
//        arguments.addArgument("ip", "172.22.33.238"); //172.22.41.227
//        arguments.addArgument("port", "16531");    //3214

            /**
             * 定位解析机地址
             */
//        arguments.addArgument("ip", "172.22.39.242"); //172.22.41.227
//        arguments.addArgument("port", "12939");    //3214

            /**
             * great解析机
             */
            arguments.addArgument("ip", "xxxx"); //172.22.41.227
            arguments.addArgument("port", "xxxx");    //3214
            arguments.addArgument("sampleLabel", "0200");
//            String imei = "000072035754"; //00 00 60 03 93 32,000060039332
            String imei = "064651983081";
            arguments.addArgument("imei", imei);

            /**
             * 高级即使辅助系统报警
             */
//        String msg_0200 = "7E 02 00 00 00 06 00 00 00 00 07 00 00 00 00 00 00 00 00 00 00 01 B9 0D 6B 07 26 F1 4D 00 00 00 11 00 44 19 03 17 09 13 24 64 2F 00 00 AE 01 01 02 01 01 13 03 02 03 12 00 13 01 B9 0D 6B 07 26 F1 4D 20 19 03 17 09 13 24 00 12 00 06 00 00 00 00 07 19 03 17 09 13 24 01 01 00 A1 7E  ";

            /**
             * 驾驶员状态检测系统报警信息
             */
//        String msg_0200 = "7E 02 00 00 00 06 00 00 00 00 01 00 11 00 00 00 00 00 00 00 00 01 B9 0D 6B 07 26 F1 4D 00 00 00 11 00 44 19 04 13 09 13 14 65 2F 00 00 AE 01 01 02 01 01 00 00 00 00 12 00 13 02 2A 97 4E 07 EB CC 6A 19 04 13 09 13 24 00 12 00 06 00 00 00 00 01 18 06 13 16 32 12 01 01 00 40 7E";
            /**
             * 胎压检测系统报警
             */
//        String msg_0200 = "7E 02 00 00 00 06 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 01 B9 0D 6B 07 26 F1 4D 00 00 00 11 00 44 19 03 17 09 13 24 66 32 00 00 AE 01 01 12 00 13 02 2A 97 4E 07 EB CC 6A 19 04 13 09 13 24 00 00 00 06 00 00 00 00 01 19 04 13 09 13 24 01 01 00 01 01 00 01 00 01 00 01 00 01 67 7E";
            /**
             * 盲区检测系统报警
             */
//        String msg_0200 = "7E 02 00 00 00 06 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 01 B9 0D 6B 07 26 F1 4D 00 00 00 11 00 44 19 04 13 09 13 11 67 29 00 00 AE 01 01 12 00 13 02 2A 97 4E 07 EB CC 6A 19 04 13 09 13 11 00 00 00 06 00 00 00 00 01 19 04 13 09 13 11 01 01 00 4B 7E";

            /**
             * 808连续报警，报警开始，第1位
             */
//        String msg_0200 ="7E 02 00 00 00 06 00 00 00 00 01 00 00 00 00 00 02 00 00 00 00 02 2A 97 4E 07 EB CC 6A 00 00 00 11 00 44 19 04 11 09 13 24 DB 7E";

            /**
             * 808连续报警，报警结束，第1位
             */
//        String msg_0200 = "7E 02 00 00 00 06 00 00 00 00 01 00 00 00 00 00 02 00 00 00 00 01 B9 0D 6B 07 26 F1 4D 00 00 00 11 00 44 19 04 11 09 13 24 23 7E ";

            /**
             * 808瞬时报警,报警为第0位有报警
             */
//        String msg_0200 = "7E 02 00 00 00 06 00 00 00 00 01 00 00 00 00 00 02 00 00 00 00 01 B9 0D 6B 07 26 F1 4D 00 00 00 11 00 44 19 04 11 09 13 24 23 7E";

            /**
             * 808瞬时报警,报警位上第0位无报警
             */
//        String msg_0200 ="7E 02 00 00 00 06 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 01 B9 0D 6B 07 26 F1 4D 00 00 00 11 00 44 19 04 11 09 13 24 21 7E";

//          String msg_0200 = "7E 02 00 00 27 00 00 60 03 93 32 00 34 00 10 01 36 00 0C 00 03 02 62 21 EB 06 EF 44 C9 00 00 00 00 00 00 19 04 09 18 35 11 11 01 00 12 06 01 00 00 00 12 00 0A 7E";
            String msg_0200 = "7E 02 00 00 00 01 25 10 73 08 69 00 00 00 00 00 08 00 00 00 00 01 CF C2 4F 06 33 F1 62 00 00 00 11 00 44 19 04 28 20 40 24 ED 7E";
//            String msg_0200 = "7E 02 00 00 00 01 11 11 11 11 11 00 00 00 00 00 00 00 00 00 02 01 B9 0D 40 07 26 F1 6E 00 00 00 11 00 44 19 04 13 17 14 04 07 7E";

//        String msg_0200 = "7E 02 00 00 00 01 11 11 11 11 11 00 00 00 00 00 00 00 00 00 02 01 B9 08 2E 07 26 E1 74 00 00 00 11 00 44 19 04 13 17 14 04 66 7E";
//            String msg_0200 = "7E 02 00 00 1A 06 00 00 00 36 00 00 07 00 00 00 00 00 00 00 03 02 6A F8 66 06 91 05 B8 27 22 00 00 00 DA 19 04 19 11 20 42 58 7E";
            arguments.addArgument("msg", msg_0200);
            arguments.addArgument("out", "true");

            System.out.println("lng:"+arrayList.get(i).getLng()+"lat:"+arrayList.get(i).getLat());
            JAPITcpSocketSend0200_2 jmeterAPI = new JAPITcpSocketSend0200_2();
            arguments.addArgument("lng",arrayList.get(i).getLng());
            arguments.addArgument("lat",arrayList.get(i).getLat());
            arg0 = new JavaSamplerContext(arguments);
            jmeterAPI.setupTest(arg0);
            jmeterAPI.runTest(arg0);
            Thread.sleep(1000);
            jmeterAPI.teardownTest(arg0);
        }
    }
}
