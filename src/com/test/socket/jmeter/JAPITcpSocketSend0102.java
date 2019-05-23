package com.test.socket.jmeter;

import com.test.socket.util.CreateMsg;
import com.test.socket.util.NioConnect;
import org.apache.commons.codec.binary.Hex;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.session.IoSession;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class JAPITcpSocketSend0102 extends AbstractJavaSamplerClient {


    private IoSession session;
    private SampleResult sr;

    public org.apache.log.Logger getLogger(){
        return super.getLogger();

    }

    /**
     * 定义java方法入参 params.addArgument("num1","");表示入参名字叫num1，默认值为空。 设置可用参数及的默认值；
     */
    public Arguments getDefaultParameters() {
        Arguments params = new Arguments();
        params.addArgument("ip", "xxxx");
        params.addArgument("port", "xxx");
        params.addArgument("sampleLabel", "0200");
        params.addArgument("imei", "${imei}");
        params.addArgument("msg", "${msg}");
        return params;
    }

    /**
     * 初始化
     */
    public void setupTest(JavaSamplerContext arg0) {
        String ip = arg0.getParameter("ip");
        String port = arg0.getParameter("port");
        session = NioConnect.getSession(ip, port);
    }

    @Override
    public SampleResult runTest(JavaSamplerContext arg0) {
        String sampleLabel = arg0.getParameter("sampleLabel");
        String imei = arg0.getParameter("imei");
        String msg = arg0.getParameter("msg");
        boolean out = Boolean.parseBoolean(arg0.getParameter("out"));

        sr = new SampleResult();
        sr.setSampleLabel(sampleLabel);
        try {
            sr.sampleStart(); // 记录程序执行时间，以及执行结果
            // 发送数据
            String result = sendMsg(imei, msg, out);
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
        session.closeNow();
    }


    /**
     * 发送、接收报文
     *
     * @param imei
     * @param msg
     * @return
     */
    public String sendMsg(String imei, String msg, boolean out) {
        String respone = "";
        SimpleDateFormat msgformatter = new SimpleDateFormat("yy MM dd HH mm ss");
        Date currentTime = new Date();
        String imeiStr = getImei(imei);
        //System.out.println("imeiStr:" + imeiStr);
        //拼装报文
        String msg1 = CreateMsg.AssemblyMessage_0102(imeiStr, msg);
        //计算校验位及替换特殊字符
        byte[] msgByte = CreateMsg.edit7d7e(CreateMsg.transfer2Bytes(msg1));
        // 发送
        session.write(msgByte).awaitUninterruptibly();
        //打印发送报文
        String msgSend = CreateMsg.getString(CreateMsg.byteArrayToHexStr(msgByte), " ");
        sr.setSamplerData("发送报文:" + msgSend);
        if (out) {
            SimpleDateFormat msgformatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currentTime1 = new Date();
            String request_text = String.format("报文发送时间:%s发送报文:%s",msgformatter1.format(currentTime1),msgSend);
            System.out.println(request_text);
        }

        // 接收
        ReadFuture readFuture = session.read();
        if (readFuture.awaitUninterruptibly(10, TimeUnit.SECONDS)) {
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

            }
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

    public static void main(String[] args) throws InterruptedException {
        Arguments arguments = new Arguments();
        arguments.addArgument("ip", "xxxxx"); //172.22.34.228
        arguments.addArgument("port", "xxxx");    //2946
        arguments.addArgument("sampleLabel", "0102");
        String imei = "060000000011"; //00 00 60 03 93 32,000060039332
        arguments.addArgument("imei", imei);
        String msg_102 = "7E 01 02 00 00 00 01 80 00 20 06 00 00 12 34 56 78 90 3C 7E";
        arguments.addArgument("msg", msg_102 );
        arguments.addArgument("out", "true");

        JavaSamplerContext arg0 = new JavaSamplerContext(arguments);
        JAPITcpSocketSend0102 jmeterAPI = new JAPITcpSocketSend0102();
        jmeterAPI.setupTest(arg0);
        for(int i =0;i<20;i++){
            jmeterAPI.runTest(arg0);
            Thread.sleep(500);
        }

        jmeterAPI.teardownTest(arg0);
    }

}
