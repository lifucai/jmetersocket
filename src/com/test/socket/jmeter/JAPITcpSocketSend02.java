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
 * 报文格式按照分段好的几段传入，再拼装
 * @author xiejie
 *
 */
public class JAPITcpSocketSend02 extends AbstractJavaSamplerClient {

	IoSession session;
	private SampleResult sr;

	/**
	 * 测试调试
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Arguments arguments = new Arguments();
		arguments.addArgument("ip", "1xxxx");
		arguments.addArgument("port", "xxx");
		
		arguments.addArgument("msgType", "0500");
		arguments.addArgument("imei", "015080000001");

		arguments.addArgument("msg01", "05000027");
		arguments.addArgument("msg02", "0AC21D900B01EB36080733C31100AB032C0137");
		arguments.addArgument("msg03", "032E001CD3B7081B02033F6ACA6452B3");
		
		JavaSamplerContext arg0 = new JavaSamplerContext(arguments);
		JAPITcpSocketSend02 jmeterAPI = new JAPITcpSocketSend02();
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
		params.addArgument("port", "xxxxx");
		params.addArgument("msgType", "0500");
		params.addArgument("imei", "${imei}");
		params.addArgument("msg01", "${msg01}");
		params.addArgument("msg02", "${msg02}");
		params.addArgument("msg03", "${msg03}");

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
		String msg01 = arg0.getParameter("msg01");
		String msg02 = arg0.getParameter("msg02");
		String msg03 = arg0.getParameter("msg03");
		
		sr = new SampleResult();
		sr.setSampleLabel(msgType);
		try {
			sr.sampleStart(); // 记录程序执行时间，以及执行结果
			// 发送数据
			String result = sendMsg(imei, msg01, msg02, msg03);
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
	public String sendMsg(String imei, String msg01, String msg02, String msg03) {
		String respone = "";
		SimpleDateFormat msgformatter = new SimpleDateFormat("yyMMddHHmmss");
		Date currentTime = new Date();
		//拼装报文
		String msg1 = CreateMsg.AssemblyMessage(imei, msgformatter.format(currentTime), msg01, msg02, msg03);
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
