package com.test.socket.util;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
//
public class TcpSocketSend extends AbstractJavaSamplerClient{
	IoSession session;
	 // Sock end------------------------------------------------  
	List<String> list_7d7e = new ArrayList<String>();
	       
	 // 测试结果  
	 private SampleResult sr;  
	 int count=1;    
	 /** 
	  * 初始化 
	  */  
	 public void setupTest(JavaSamplerContext arg0) {
	        System.out.println(Thread.currentThread().getName()+"setupTest");
	        NioSocketConnector connector = new NioSocketConnector();
			connector.setConnectTimeoutMillis(3000L);
	       connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ByteArrayCodecFactory()));
	       SocketSessionConfig cfg = connector.getSessionConfig();
	       cfg.setUseReadOperation(true);
	       session = connector.connect(new InetSocketAddress(arg0.getParameter("ip"),Integer.valueOf(arg0.getParameter("port")))).awaitUninterruptibly().getSession();
	       while(true){
	    	   boolean connFlag =session.isConnected();
	    	   if(connFlag){
	    		   break;
	    	   }
	       }
     }  
	     
	 /** 
	  * 设置请求的参数 
	  */  
	 public Arguments getDefaultParameters() {
		 	System.out.println("getDefaultParameters in");
	        Arguments params = new Arguments();  
	        params.addArgument("ip", "xxxx");
	        params.addArgument("port", "xxxx");
	        params.addArgument("imei", "${deviceimei}");
	        params.addArgument("msg", "${message}");
	        return params;  
	    }  
	     
	 /** 
	  * 运行过程 
	  */  
	 public SampleResult runTest(JavaSamplerContext arg0) {
		//System.out.println(Thread.currentThread().getName());
	    String ip = arg0.getParameter("ip");    
	    String port = arg0.getParameter("port");
	    String imei = arg0.getParameter("imei");
	    String msg = arg0.getParameter("msg");
	        sr = new SampleResult();    
	        try{    
	            sr.sampleStart(); //记录程序执行时间，以及执行结果    
	            //发送数据    
	            System.out.println(Thread.currentThread().getName()+"||"+"begin");
	            sendMsg(ip, Integer.parseInt(port),imei,msg);
	            System.out.println(Thread.currentThread().getName()+"||"+"sendMsg end");
	        }catch(Throwable e){
	            sr.setSuccessful(false);    
	        }finally{
	            sr.sampleEnd();    
	        }
	        
	        return sr;   
	 }  
	 
	 /** 
	  * 发送消息 
	  * @param ip 
	  * @param port 
	  * @param msg 
	  * @throws Exception 
	  */  
	 private void sendMsg(String ip, int port,String imei,String msg) throws Exception{
		 System.out.println(Thread.currentThread().getName()+"||"+"sendMsg in");
		 System.out.println(Thread.currentThread().getName()+"||"+"aaaaa");
		 SimpleDateFormat recformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 System.out.println(Thread.currentThread().getName()+"||"+"bbbbb");
		 SimpleDateFormat msgformatter = new SimpleDateFormat("yy MM dd HH mm ss");
		 System.out.println(Thread.currentThread().getName()+"||"+"ccccc");
		Date currentTime= new Date();
		System.out.println(Thread.currentThread().getName()+"||"+"ddddd");
	 	String dateString = recformatter.format(currentTime);
	 	System.out.println(Thread.currentThread().getName()+"||"+"fffff");
	 	String msg1=AssemblyMessage(imei,msg,msgformatter.format(currentTime));
		 // 发送
	 	System.out.println(Thread.currentThread().getName()+"111111111111111111");
         session.write(edit7d7e(transfer2Bytes(msg1))).awaitUninterruptibly();
         System.out.println(Thread.currentThread().getName()+"222222222222222222");
         sr.setSamplerData(dateString+"||"+msg1);
         // 接收
         ReadFuture readFuture = session.read();
         if (readFuture.awaitUninterruptibly(3, TimeUnit.SECONDS)) {
             byte[] repByte = (byte[]) readFuture.getMessage();
             char[] chArr = Hex.encodeHex(repByte);
             String str="";
     		for (char c : chArr) {
     			str+=String.valueOf(c).toUpperCase();
     		}
     		String respone="";
     		for(int i=0;i<str.length();i++){
     			if(i%2==0){
     				String tmp=str.substring(i,i+2);
     				respone=respone+" "+tmp;
     			}
     		}
     		respone=respone.substring(1,respone.length());
     		System.out.println(Thread.currentThread().getName()+"||"+"count="+count);
     		System.out.println(Thread.currentThread().getName()+"||"+"响应："+respone);
     		sr.setSuccessful(true); 
     		count++;
         } else {
             // 读超时
        	 sr.setSuccessful(false); 
       	     System.out.println(Thread.currentThread().getName()+"||"+"应答超时");
         }
	 }
	 
	     
	 /** 
	  * 结束 
	  */  
	 public void teardownTest(JavaSamplerContext arg0) {
		 System.out.println("teardownTest");
         session.getService().dispose();
	 }  
	     
	 
	 /*public static void main(String[] args) throws Exception {
		 sendMsg("172.22.3.236",16531,"01 50 80 00 00 00","7E 05 00 00 27 01 50 80 00 00 03 00 01 1D 00 03 02 62 F3 86 06 EE F5 1C 02 07 00 5C 00 B3 15 06 12 07 27 31 02 81 00 00 33 2C 0A 1E 00 1E 3E 03 32 42 18 43 95 7E");
	}*/
	 
	 private String AssemblyMessage(String imei,String msg,String datetime){
		 String s1=msg.substring(0,15);
		 String s2=msg.substring(32,msg.length());
		 String s3=s1+imei+s2;
		 String s4=s3.substring(0,90);
		 String s5=s3.substring(107,s3.length());
		 String msg1=s4+datetime+s5;
		 return msg1;
	 }
	 
	 
	 private static byte[] transfer2Bytes(String input){
			System.out.println(input);
			String[] arrs = input.split(" ");
			byte[] bytes = new byte[arrs.length];
			for (int i = 0; i < arrs.length; i++) {
				bytes[i] = (byte)Integer.parseInt(arrs[i],16);
			}
			setNewFlag(bytes);
			return bytes;
		}
		
		private static void setNewFlag(byte[] bs) {
			byte[] da = new byte[bs.length - 15];
			System.arraycopy(bs, 13, da, 0, da.length);
			//System.arraycopy(bs, 13, da, 0, da.length);0902 校验2次
			//ctbox校验位
			//bs[bs.length - 4] = setNewFlagSub(da);
			//然后gnns校验位
			int idx = bs.length - 2;  
			bs[idx] = bs[1];
			for (int j=2;j<idx;j++) bs[idx] ^= bs[j];
		}
		
		public byte[] edit7d7e(byte[] b){
			b=subBytes(b,1,b.length-2);
			for(int i=0;i<b.length;i++){
				if("125".equals(String.valueOf(b[i]))){
					String str=String.valueOf(i)+","+"7d";
					list_7d7e.add(str);
				}
				if("126".equals(String.valueOf(b[i]))){
					String str=String.valueOf(i)+","+"7e";
					list_7d7e.add(str);
				}
			}
			for(int i=0;i<list_7d7e.size();i++){
				String str[]=list_7d7e.get(i).split(",");
				if(str[1].equals("7d")){
					byte head[]=cutOutByte(b,Integer.valueOf(str[0]));
					int left=(b.length-head.length);
					byte mid[]={(byte)Integer.parseInt("7d",16),(byte)Integer.parseInt("01",16)};
					byte tail[]=subBytes(b,Integer.valueOf(str[0])+1,left-1);
					b=getMergeBytes(head,mid);
					b=getMergeBytes(b,tail);
					if(i==list_7d7e.size()-1)
						break;
					for(int j=(i+1);j<list_7d7e.size();j++){
						String next_str[]=list_7d7e.get(j).split(",");
						int new_index=Integer.valueOf(next_str[0])+1;
						list_7d7e.set(j, new_index+","+next_str[1]);
					}
					
				}
				if(str[1].equals("7e")){
					byte head[]=cutOutByte(b,Integer.valueOf(str[0]));
					int left=(b.length-head.length);
					byte mid[]={(byte)Integer.parseInt("7d",16),(byte)Integer.parseInt("02",16)};
					byte tail[]=subBytes(b,Integer.valueOf(str[0])+1,left-1);
					b=getMergeBytes(head,mid);
					b=getMergeBytes(b,tail);
					if(i==list_7d7e.size()-1)
						break;
					for(int j=(i+1);j<list_7d7e.size();j++){
						String next_str[]=list_7d7e.get(j).split(",");
						int new_index=Integer.valueOf(next_str[0])+1;
						list_7d7e.set(j, new_index+","+next_str[1]);
					}
				}
			}
			byte tmp[]={(byte)Integer.parseInt("7e",16)};
			b=getMergeBytes(tmp,b);
			b=getMergeBytes(b,tmp);
			list_7d7e.clear();
			return b;
		}
		
		public static byte[] getMergeBytes(byte[] pByteA, byte[] pByteB){
			int aCount = pByteA.length;
			int bCount = pByteB.length;
			byte[] b = new byte[aCount + bCount];
			for(int i=0;i<aCount;i++){
				b[i] = pByteA[i];
			}
			for(int i=0;i<bCount;i++){
				b[aCount + i] = pByteB[i];
			}
			return b;
		}
		
		public static byte[] subBytes(byte[] src, int begin, int count) {
	        byte[] bs = new byte[count];
	        for (int i=begin; i<begin+count; i++) bs[i-begin] = src[i];
	        return bs;
	    }
		
		public static byte[] cutOutByte(byte[] b,int j){
			if(b.length==0 || j==0){
				return null;
			}
			byte[] bjq = new byte[j];
			for(int i = 0; i<j;i++){
				bjq[i]=b[i];
			}
			return bjq;
		}
	
}
