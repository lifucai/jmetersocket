package com.test.socket.jmeter;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import com.test.socket.util.SocketSendMsg;

/**
 * 报文格式按照分段位置传入，再拼装
 * @author xiejie
 *
 */
public class JAPITcpSocketSend0200 extends AbstractJavaSamplerClient {

	private SocketSendMsg ssm;
	private SampleResult sr;

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
		return params;
	}

	/**
	 * 初始化
	 */
	public void setupTest(JavaSamplerContext arg0) {
		String ip = arg0.getParameter("ip");
		String port = arg0.getParameter("port");
		
		ssm = new SocketSendMsg();
		ssm.createSession(ip, port);
	}

	/**
	 * 运行
	 */
	@Override
	public SampleResult runTest(JavaSamplerContext arg0) {
		String sampleLabel = arg0.getParameter("sampleLabel");
		String imei = arg0.getParameter("imei");
		String msg = arg0.getParameter("msg");
		
		sr = new SampleResult();
		sr.setSampleLabel(sampleLabel);
		try {
			sr.sampleStart(); // 记录程序执行时间，以及执行结果
			// 发送数据
			String result = ssm.sendMsgManage_0200(imei, msg);
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
		ssm.closeSession();
	}
	
	/**
	 * 测试调试
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Arguments arguments = new Arguments();
		arguments.addArgument("ip", "Xxxx");
		arguments.addArgument("port", "xxxx");
		arguments.addArgument("sampleLabel", "0200");
		arguments.addArgument("imei", "00 00 60 03 93 32");
		String msg_0200 = "7E 02 00 00 28 00 00 60 03 93 32 00 34 00 00 00 00 00 0C 00 03 02 62 21 EB 06 EF 44 C9 00 00 00 00 00 00 18 01 18 18 35 11 01 04 00 01 20 10 30 01 10 31 01 03 06 7E";
		arguments.addArgument("msg", msg_0200);
		
		JavaSamplerContext arg0 = new JavaSamplerContext(arguments);
		JAPITcpSocketSend0200 jmeterAPI = new JAPITcpSocketSend0200();
		jmeterAPI.setupTest(arg0);
		jmeterAPI.runTest(arg0);
		jmeterAPI.teardownTest(arg0);
	}
	
}
