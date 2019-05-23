package com.test.socket.jmeter;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Hex;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.test.socket.util.ByteArrayCodecFactory;
import com.test.socket.util.CreateMsg;

/**
 * 报文格式按照分段位置传入，再拼装
 * @author xiejie
 *
 */
public class JAPITcpSocketSend extends AbstractJavaSamplerClient {

	IoSession session;
	private SampleResult sr;

	/**
	 * 测试调试
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
//		String string = "";
//		String[] list = string.split(",");
//		for (String str : list) {
//			
//		}
		
		Arguments arguments = new Arguments();
		arguments.addArgument("ip", "xxxx");
		arguments.addArgument("port", "xxxx");
		arguments.addArgument("imei", "01 50 90 09 76 75"); //"01 50 80 00 00 01"
		
		//String msg_0500 = "7E 05 00 00 27 01 50 90 06 83 23 0A C2 1D 90 0B 01 EB 36 08 07 33 C3 11 00 AB 03 2C 01 37 17 12 14 15 29 13 03 2E 00 1C D3 B7 08 1B 02 03 3F 6A CA 64 52 B3 E8 7E";
		//String msg_0500 = "7E 05 00 00 27 01 50 90 09 76 75 00 01 1D 00 00 02 69 75 EF 07 65 C5 5C 00 00 00 00 00 00 18 09 13 13 31 25 03 0E 00 00 0F A9 0A 13 00 04 0D DF B4 FD 80 0B A0 7E";
		
		String msg_0500 = "7E 05 00 00 27 01 50 90 09 76 75 0A C2 1D 90 0B 02 69 75 EF 07 65 C5 5C 00 00 00 00 00 00 18 09 25 18 02 53 03 2E 00 1C D3 B7 08 1B 02 03 3F 6A CA 64 52 B3 07 7E";
		
		arguments.addArgument("msg", msg_0500);
		arguments.addArgument("msgType", "0500");
		arguments.addArgument("startImei", "15");
		arguments.addArgument("startDate", "90");
		
//		String msg_0002 = "7E 00 02 00 3B 01 50 90 10 79 69 00 0A 13 AD 2D 00 00 10 01 15 00 32 65 13 01 0F 00 00 00 22 00 E4 01 4A 05 01 15 24 27 27 27 27 29 28 24 25 2A 29 28 2F 27 27 26 21 28 28 2E 2B 01 27 02 01 00 0B 06 17 08 31 14 59 51 8B 7E";
//		arguments.addArgument("msg", msg_0002);
//		arguments.addArgument("msgType", "0002");
//		arguments.addArgument("startImei", "15");
//		arguments.addArgument("startDate", "198");
		
////		String msg_0707 = "7E 07 07 05 4A 01 50 90 11 74 65 2F D6 F1 BE 8B 00 0B 06 17 11 17 23 59 59 70 14 01 00 70 15 01 00 70 16 01 00 70 17 01 00 70 18 02 03 78 70 19 01 00 70 1A 01 00 70 1B 01 00 70 1C 01 00 70 1D 01 03 70 1E 01 00 70 1F 01 00 70 00 02 02 90 70 06 01 00 70 01 02 00 B8 70 22 02 01 6A 70 07 01 B1 70 32 01 66 70 04 01 13 70 03 01 01 70 23 01 00 70 24 01 00 70 25 02 00 D6 70 26 01 82 70 27 02 14 FC 70 28 02 10 0E 70 2A 01 00 70 2B 01 7D 01 70 2C 01 9D 70 05 04 00 0F 0E E6 70 02 04 00 00 B3 43 70 2F 01 00 70 30 01 00 70 34 01 01 70 35 01 00 70 08 02 00 00 70 39 02 00 1B 70 3C 02 00 00 70 3D 02 00 00 70 3E 02 03 FC 70 3F 02 03 FC 70 B5 01 01 70 B6 01 00 70 B7 01 64 CC 00 45 18 00 00 00 CC 00 01 C4 00 0B B4 00 13 80 B4 00 79 01 47 70 22 02 01 5E B4 00 31 23 01 00 A7 00 33 2A 01 3E 9D 00 1B B1 9D 00 00 95 00 0F 91 00 1E 1F 01 91 00 0A 14 4B 91 00 10 61 91 00 17 4E 91 00 11 11 41 01 02 91 00 13 87 91 00 1F B8 91 00 31 1F 02 91 00 0A 14 5C 91 00 10 62 91 00 19 38 91 00 37 03 01 00 95 00 13 77 95 00 1F B7 95 00 24 50 00 70 B7 01 64 59 7E";
//		String msg_0707 = "7E 07 07 05 4A 01 50 90 11 74 65 2F D6 F1 BE 8B 00 0B 06 17 11 17 23 59 59 70 14 01 00 70 15 01 00 70 16 01 00 70 17 01 00 70 18 02 03 78 70 19 01 00 70 1A 01 00 70 1B 01 00 70 1C 01 00 70 1D 01 03 70 1E 01 00 70 1F 01 00 70 00 02 02 90 70 06 01 00 70 01 02 00 B8 70 22 02 01 6A 70 07 01 B1 70 32 01 66 70 04 01 13 70 03 01 01 70 23 01 00 70 24 01 00 70 25 02 00 D6 70 26 01 82 70 27 02 14 FC 70 28 02 10 0E 70 2A 01 00 70 2B 01 7D 70 2C 01 9D 70 05 04 00 0F 0E E6 70 02 04 00 00 B3 43 70 2F 01 00 70 30 01 00 70 34 01 01 70 35 01 00 70 08 02 00 00 70 39 02 00 1B 70 3C 02 00 00 70 3D 02 00 00 70 3E 02 03 FC 70 3F 02 03 FC 70 B5 01 01 70 B6 01 00 70 B7 01 64 CC 00 45 18 00 00 00 CC 00 01 C4 00 0B B4 00 13 80 B4 00 79 01 47 70 22 02 01 5E B4 00 31 23 01 00 A7 00 33 2A 01 3E 9D 00 1B B1 9D 00 00 95 00 0F 91 00 1E 1F 01 91 00 0A 14 4B 91 00 10 61 91 00 17 4E 91 00 11 11 41 01 02 91 00 13 87 91 00 1F B8 91 00 31 1F 02 91 00 0A 14 5C 91 00 10 62 91 00 19 38 91 00 37 03 01 00 95 00 13 77 95 00 1F B7 95 00 24 50 00 70 B7 01 64 59 7E";
////		String msg_0707 = "7E 07 07 05 58 01 50 90 11 74 65 2F D7 F4 BE 8A 00 0B 06 17 11 18 00 00 03 70 14 01 00 70 15 01 00 70 16 01 00 70 17 01 00 70 18 02 03 78 70 19 01 00 70 1A 01 00 70 1B 01 00 70 1C 01 00 70 1D 01 03 70 1E 01 00 70 1F 01 00 70 00 02 04 1E 70 06 01 04 70 01 02 00 46 70 22 02 01 60 70 07 01 B1 70 32 01 66 70 04 01 10 70 03 01 00 70 23 01 00 70 24 01 00 70 25 02 00 D6 70 26 01 82 70 27 02 14 FC 70 28 02 10 0E 70 2A 01 0A 70 2B 01 83 70 2C 01 85 70 05 04 00 0F 0E E6 70 02 04 00 00 B3 43 70 2F 01 00 70 30 01 00 70 34 01 01 70 35 01 00 70 08 02 00 00 70 39 02 00 1B 70 3C 02 00 00 70 3D 02 00 00 70 3E 02 03 FC 70 3F 02 03 FC 70 B5 01 01 70 B6 01 00 70 B7 01 64 CC 00 15 04 CC 00 01 C4 00 0A B4 00 60 03 8C 70 06 01 12 B4 00 69 83 70 22 02 01 AE B4 00 31 23 01 00 A7 00 BB 2A 01 19 70 2B 01 95 70 2C 01 94 9D 00 00 95 00 0F 91 00 1E 1F 05 91 00 0A A0 91 70 06 01 4C 70 01 02 02 1F 91 00 1F BE 91 00 04 9F C0 70 2B 01 C7 70 2C 01 BC 91 00 31 18 06 91 00 1C 9C 22 01 50 C5 70 06 01 4A 91 00 10 D1 91 00 1F C6 91 00 04 9F B3 70 2B 01 D6 70 2C 01 CA 91 00 24 50 00 70 B7 01 64 A6 7E";
//		arguments.addArgument("msg", msg_0707);
//		arguments.addArgument("msgType", "0707");
//		arguments.addArgument("startImei", "15");
//		arguments.addArgument("startDate", "57");
		
		JavaSamplerContext arg0 = new JavaSamplerContext(arguments);
		JAPITcpSocketSend jmeterAPI = new JAPITcpSocketSend();
		jmeterAPI.setupTest(arg0);
		jmeterAPI.runTest(arg0);
		jmeterAPI.teardownTest(arg0);
	}

	/**
	 * 定义java方法入参 params.addArgument("num1","");表示入参名字叫num1，默认值为空。 设置可用参数及的默认值；
	 */
	public Arguments getDefaultParameters() {

		Arguments params = new Arguments();
		params.addArgument("ip", "xxxx");
		params.addArgument("port", "xxx");
		params.addArgument("msgType", "0500");
		params.addArgument("imei", "${imei}");
		params.addArgument("msg", "${msg}");
		params.addArgument("startImei", "15");
		params.addArgument("startDate", "");

		return params;
	}

	/**
	 * 初始化
	 */
	public void setupTest(JavaSamplerContext arg0) {

		String ip = arg0.getParameter("ip");
		String port = arg0.getParameter("port");

		NioSocketConnector connector = new NioSocketConnector();
		connector.setConnectTimeoutMillis(3000L);
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ByteArrayCodecFactory()));
		SocketSessionConfig cfg = connector.getSessionConfig();
		cfg.setUseReadOperation(true);
		session = connector.connect(new InetSocketAddress(ip, Integer.valueOf(port))).awaitUninterruptibly()
				.getSession();
		while (true) {
			boolean connFlag = session.isConnected();
			if (connFlag) {
				break;
			}
		}
	}

	/**
	 * 运行
	 */
	@Override
	public SampleResult runTest(JavaSamplerContext arg0) {

		String msgType = arg0.getParameter("msgType");
		String imei = arg0.getParameter("imei");
		String msg = arg0.getParameter("msg");
		String startImei = arg0.getParameter("startImei");
		String startDate = arg0.getParameter("startDate");
		
		sr = new SampleResult();
		sr.setSampleLabel(msgType);
		try {
			sr.sampleStart(); // 记录程序执行时间，以及执行结果
			// 发送数据
			String result = sendMsg(imei, msg, Integer.valueOf(startImei), Integer.valueOf(startDate));
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
	 * @param imei
	 * @param msg
	 * @return
	 */
	public String sendMsg(String imei, String msg, int startImei, int startDate) {
		String respone = "";
		SimpleDateFormat msgformatter = new SimpleDateFormat("yy MM dd HH mm ss");
		Date currentTime = new Date();
		//拼装报文
		String msg1 = CreateMsg.AssemblyMessage(imei, msg, msgformatter.format(currentTime), startImei, startDate);
		//计算校验位及替换特殊字符
		byte[] msgByte = CreateMsg.edit7d7e(CreateMsg.transfer2Bytes(msg1));
		// 发送
		session.write(msgByte).awaitUninterruptibly();
		//打印发送报文
		String msgSend = CreateMsg.getString(CreateMsg.byteArrayToHexStr(msgByte), " ");
		sr.setSamplerData("发送报文:" + msgSend);
				
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
			//System.out.println("响应报文:" + respone);
		} else {
			// 读超时
			respone = "code=0,TimeOut(3s)!";
		}

		return respone;
	}

}
