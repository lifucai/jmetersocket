package com.test.socket.util;

public class SocketSendMsgTest {


	/// great"0200"测试 阿里云测试环境
	private static String ip = "xxxxx";	//xxx  xxxx
	private static String port = "xxxx"; //xxxx
	
	private static SocketSendMsg ssm;
	
	public static void main(String[] args) {
		ssm = new SocketSendMsg();
		ssm.createSession(ip, port);
//		testSendMsg_0500();
//		testSendMsg_0039();
//		testSendMsg_0200();
		testSendMsg_0200_great();
		ssm.closeSession();
	}
	
	public static void testSendMsg_0500(){
		String imei = "01 50 80 00 00 01";
		String msg = "7E 05 00 00 27 01 50 90 06 83 23 0A C2 1D 90 0B 01 EB 36 08 07 33 C3 11 00 AB 03 2C 01 37 17 12 14 15 29 13 03 2E 00 1C D3 B7 08 1B 02 03 3F 6A CA 64 52 B3 E8 7E";
		//String msg = "7E 09 02 04 3D 01 50 80 00 00 01 A3 6E F0 2C FF 18 01 24 15 26 03 01 02 D7 02 6A BC 0A 07 30 E7 9A  2A 58 44 31 2C 35 32 32 4A 2C 30 34 2C 31 38 35 39 2C 30 32 30 30 2C 30 35 30 30 2C 31 38 39 39 2C 31 30 30 30 2C 35 61 23 E8 7E";
		ssm.sendMsgManage(imei, msg);
	}
	
	public static void testSendMsg_0039(){
		//String msg = "7E 00 39 00 36 01 50 90 00 00 08 1A B5 02 00 0B 06 18 07 09 17 55 12 FF 00 01 05 01 0A 08 56 6F 69 63 65 42 6F 78 01 10 05 52 53 34 38 35 01 0B 04 FF FF FF FF 01 0C 04 04 0B 00 00 00 BE 04 01 04 01 05 0A 7E";
		String imei = "01 50 90 00 00 08";
		String periphera = "SmartEye";
		ssm.sendMsgManage_0039(imei, periphera);
	}
	
	public static void testSendMsg_0200(){
		//106000060039332
		String imei = "00 00 60 03 93 32";
		String msg = "7E 02 00 00 28 00 00 60 03 93 32 00 34 00 00 00 00 00 0C 00 03 02 62 21 EB 06 EF 44 C9 00 00 00 00 00 00 18 01 18 18 35 11 01 04 00 01 20 10 30 01 10 31 01 03 06 7E";
		ssm.sendMsgManage_0200(imei, msg);
	}
	
	public static void testSendMsg_0200_great(){
		//106000060039332
		String imei = "06 48 91 20 04 57";
		String msg = "7E 02 00 00 44 06 48 91 20 04 57 3E E1 00 08 00 00 00 0C 00 03 01 D2 10 90 06 33 D8 65 02 70 00 00 00 00 19 03 15 14 15 54 01 7E";
		ssm.sendMsgManage_0200(imei, msg);
	}
	
	

}
