package com.test.socket.jmeter;

import com.test.socket.util.Location;
import com.test.socket.util.MsgSend;
import com.test.socket.util.NioConnect;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.mina.core.session.IoSession;

import java.io.*;
import java.util.ArrayList;

/**
 * 发送轨迹点
 */
public class PositioningTraceSend {

    private static IoSession session;

    //解析机或者分路网关ip
    private static String ip = "172.22.41.227";
    private static String port = "3214";


    /**
     * 测试调试
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        /**
         * 限制数量
         */
        int limit = 5000;
        int  intervel = 10;
        String imei = "011234028888";
        String filePath = "/Users/xxxx.xxx/Desktop/test.csv";
        String msg_0200 = "7E 02 00 00 1A 06 00 00 00 36 00 00 07 00 00 00 00 00 00 00 03 02 6A F8 66 06 91 05 B8 27 22 03 E8 00 DA 19 04 19 11 20 42 58 7E";
        //建立连接
        session = NioConnect.getSession(ip, port);

        //获取轨迹坐标点
        ArrayList<Location> historyPointList = getPointInfo(filePath);

        //处理limit超过限制的情况
        if (limit > historyPointList.size()) {
            limit = historyPointList.size();
        }
        for (int i = 0; i < limit; i++) {
            if(i % 10 == 0) {
               System.out.println("------"+i+"--------");
            }
            Location point = historyPointList.get(i);
            System.out.println(point.getLng()+","+point.getLat());
            String response = MsgSend.sendTraceMsg(session, imei, msg_0200, point.getLng(), point.getLat(), true);
            Thread.sleep(intervel * 1000);
        }

        session.closeNow();
    }


    /**
     * 获取轨迹点数据
     *
     * @param filePath
     * @return
     */
    private static ArrayList<Location> getPointInfo(String filePath) throws Exception {
        ArrayList<Location> arrayList = new ArrayList<>();
        InputStreamReader inputReader = null;
        BufferedReader bf = null;
        File file = new File(filePath);
        try {

            inputReader = new InputStreamReader(new FileInputStream(file));
            bf = new BufferedReader(inputReader);

            int i = 1;
            String str = null;
            while ((str = bf.readLine()) != null) {
                String[] strArr = str.split(",");
                if (strArr != null && strArr.length == 2) {
                    Location locationObj = new Location();
                    locationObj.setLat(strArr[0]);
                    locationObj.setLng(strArr[1]);
                    arrayList.add(locationObj);
                    i++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.print("文件" + filePath + "读取失败～～～");
        } finally {
            bf.close();
            inputReader.close();
        }
        return arrayList;
    }
}