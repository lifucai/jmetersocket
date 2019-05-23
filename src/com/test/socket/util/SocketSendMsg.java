package com.test.socket.util;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.commons.codec.binary.Hex;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class SocketSendMsg {
	
	IoSession session;
	
	/**
	 * 创建连接
	 * @param ip
	 * @param port
	 */
	public void createSession(String ip, String port){
		NioSocketConnector connector = new NioSocketConnector();
		connector.setConnectTimeoutMillis(3000L);
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ByteArrayCodecFactory()));
		SocketSessionConfig cfg = connector.getSessionConfig();
		cfg.setUseReadOperation(true);
		session = connector.connect(new InetSocketAddress(ip, Integer.valueOf(port))).awaitUninterruptibly().getSession();
		while (true) {
			boolean connFlag = session.isConnected();
			if (connFlag) {
				break;
			}
		}
	}
	
	/**
	 * 关闭连接
	 */
	public void closeSession() {
		session.getService().dispose();
	}
	
	/**
	 * 发送报文
	 * @param msgByte
	 * @return
	 */
	public String sendMsg(byte[] msgByte) {
		String recMsg = null;
		// 发送
		session.write(msgByte).awaitUninterruptibly();
		// 接收
		ReadFuture readFuture = session.read();
		if (readFuture.awaitUninterruptibly(3, TimeUnit.SECONDS)) {
			byte[] recMsgByte = (byte[]) readFuture.getMessage();
			recMsg = recMsgManage(recMsgByte);
		} else {
			// 读超时
			System.err.println("应答超时(3s)");
		}
		return recMsg;
	}
	
	/**
	 * 将接收报文转换为字符串
	 * @param recMsgByte
	 * @return
	 */
	private String recMsgManage(byte[] recMsgByte) {
		String recMsg = null;
		char[] chArr = Hex.encodeHex(recMsgByte);
		String str = "";
		for (char c : chArr) {
			str += String.valueOf(c).toUpperCase();
		}
		recMsg = CreateMsg.getString(str, " ");
		return recMsg;
	}
	
	public String sendMsgManage_0200(String imei, String msg) {
		String recMsg = null;
		SimpleDateFormat msgformatter = new SimpleDateFormat("yy MM dd HH mm ss");
		Date currentTime = new Date();
		String dateTime = msgformatter.format(currentTime);
		//拼装报文
		String msg1 = CreateMsg.AssemblyMessage_0200(imei, msg, dateTime,"1111","1111");
		//计算校验位及替换特殊字符
		byte[] msgByte = CreateMsg.edit7d7e(CreateMsg.transfer2Bytes(msg1));
		//打印发送报文
		String msgSend = CreateMsg.getString(CreateMsg.byteArrayToHexStr(msgByte), " ");
		System.out.println("发送报文:" + msgSend);
		// 发送	
		recMsg = sendMsg(msgByte);
		System.out.println("响应报文:" + recMsg);
		
		return recMsg;
	}
	
	/**
	 * 根据传入的imei和msg进行拆分拼装，0500报文等
	 * @param imei
	 * @param msg
	 */
	public String sendMsgManage(String imei, String msg) {
		String recMsg = null;
		SimpleDateFormat msgformatter = new SimpleDateFormat("yy MM dd HH mm ss");
		Date currentTime = new Date();
		String dateTime = msgformatter.format(currentTime);
		//拼装报文
		String msg1 = CreateMsg.AssemblyMessage(imei, msg, dateTime);
		//计算校验位及替换特殊字符
		byte[] msgByte = CreateMsg.edit7d7e(CreateMsg.transfer2Bytes(msg1));
		//打印发送报文
		String msgSend = CreateMsg.getString(CreateMsg.byteArrayToHexStr(msgByte), " ");
		System.out.println("发送报文:" + msgSend);
		// 发送	
		recMsg = sendMsg(msgByte);
		System.out.println("响应报文:" + recMsg);
		
		return recMsg;
	}
	
	/**
	 * 根据传入的imei和periphera(外设类型)进行拆分拼装，0039报文
	 * @param imei
	 * @param periphera
	 * @return
	 */
	public String sendMsgManage_0039(String imei, String periphera) {
		String recMsg = null;
		//拼装报文
		String msg = CreateMsg.AssemblyMessage_0039(imei, periphera);
		//计算校验位及替换特殊字符
		byte[] msgByte = CreateMsg.edit7d7e(CreateMsg.transfer2Bytes(msg));
		//打印发送报文
		String msgSend = CreateMsg.getString(CreateMsg.byteArrayToHexStr(msgByte), " ");
		System.out.println("发送报文:" + msgSend);
		// 发送	
		recMsg = sendMsg(msgByte);
		System.out.println("响应报文:" + recMsg);
		
		return recMsg;
	}


}
