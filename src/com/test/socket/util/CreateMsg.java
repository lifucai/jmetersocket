package com.test.socket.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreateMsg {

    /**
     * 通用报文，格式：s1 + imei + s2 + datetime + s3;
     *
     * @param imei
     * @param datetime
     * @param msg01
     * @param msg02
     * @param msg03
     * @return
     */
    public static String AssemblyMessage(String imei, String datetime, String msg01, String msg02, String msg03) {
        String msg1 = "7E" + msg01 + imei + msg02 + datetime + msg03 + "007E";
        String msg2 = "";
        for (int i = 0; i < msg1.length(); i++) {
            if (i % 2 == 0) {
                String tmp = msg1.substring(i, i + 2);
                msg2 = msg2 + " " + tmp;
            }
        }
        msg2 = msg2.substring(1, msg2.length());
        //System.out.println("原始报文:" + msg2);
        return msg2;
    }

    /**
     * 通用报文，格式：s1 + imei + s2 + datetime + s3;
     *
     * @param imei
     * @param msg
     * @param datetime
     * @return
     */
    public static String AssemblyMessage(String imei, String msg, String datetime, int startImei, int startDate) {
        // imei(01 50 80 00 00 01)、date(18 01 25 16 17 58)都是17位长度
        String s1 = msg.substring(0, startImei);
        String s2 = msg.substring(startImei + 17, startDate);
        String s3 = msg.substring(startDate + 17, msg.length());
        String msg1 = s1 + imei + s2 + datetime + s3;
        //System.out.println("原始报文:" + msg1);
        return msg1;
    }

    /**
     * 0200报文_有报警
     *
     * @param imei
     * @param msg
     * @param dateTime
     * @return
     */
    public static String AssemblyMessage_0200(String imei, String msg, String dateTime, String lng, String lat) {
        String msg1 = "";
        //System.out.println("原始报文:" + msg);
        String s1 = msg.substring(0, 15);
        String s2 = msg.substring(32, 63);
        String s3 = msg.substring(87, 105);
        String s4 = msg.substring(122, msg.length());
        msg1 = msg1 + s1 + imei + s2 + lat + " " + lng + " " + s3 + dateTime + s4;
        //System.out.println("替换后报文" + msg1);
        return msg1;

    }

    /**
     * 0102报文
     *
     * @param imei
     * @param msg
     * @return
     */
    public static String AssemblyMessage_0102(String imei, String msg) {
        String msg1 = "";
        if (msg != null && msg.length() > 32) {
            String s1 = msg.substring(0, 15);
            String s2 = msg.substring(32, msg.length());
            msg1 = s1 + imei + s2;
        }
        return msg1;
    }

//    public static void main(String args[]) {
//        String msg = "7E 02 00 00 00 01 11 11 11 11 11 00 00 00 00 00 00 00 00 00 02 01 B9 57 35 07 26 68 65 00 00 00 11 00 44 19 04 15 10 30 41 DA 7E";
//        String imei = "01 11 11 11 11 11";
//        String dateTime = "19 04 15 10 30 41";
//        String lat = "01 B9 57 35";
//        String lng = "07 26 68 65";
//
//        AssemblyMessage_0200(imei, msg, dateTime, lng, lat);
//    }

    /**
     * 0500报文
     *
     * @param imei
     * @param msg
     * @param dateTime
     * @return
     */
    public static String AssemblyMessage(String imei, String msg, String dateTime) {
        String s1 = msg.substring(0, 15);
        String s2 = msg.substring(32, 90);
        String s3 = msg.substring(107, msg.length());
        String msg1 = s1 + imei + s2 + dateTime + s3;
        //System.out.println("原始报文:" + msg1);
        return msg1;
    }


    /**
     * 0039报文
     *
     * @param imei
     * @param periphera
     * @return
     */
    public static String AssemblyMessage_0039(String imei, String periphera) {

        SimpleDateFormat msgformatter = new SimpleDateFormat("yy MM dd HH mm ss");
        Date currentTime = new Date();
        String dateTime = msgformatter.format(currentTime);

        //原始报文
        //7E 00 39 00 36 01 50 90 00 00 08 1A B5 02 00 0B 06 18 07 09 17 55 12 FF 00 01 05 01 0A 08 56 6F 69 63 65 42 6F 78 01 10 05 52 53 34 38 35 01 0B 04 FF FF FF FF 01 0C 04 04 0B 00 00 00 BE 04 01 04 01 05 0A 7E
        String s1 = "7E 00 39 00 36 ";
        String s2 = " 1A B5 02 00 0B 06 ";
        String s3 = " FF 00 01 05 01 0A ";
        String s4 = "08 56 6F 69 63 65 42 6F 78";
        String s5 = " 01 10 05 52 53 34 38 35 01 0B 04 FF FF FF FF 01 0C 04 04 0B 00 00 00 BE 04 01 04 01 05 0A 7E";

//		SmartEye 千里眼,		  53 6D 61 72 74 45 79 65		08 53 6D 61 72 74 45 79 65
//		DID 打卡，             44 49 44						03 44 49 44
//		OilSensor 油感，       4F 69 6C 53 65 6E 73 6F 72		09 4F 69 6C 53 65 6E 73 6F 72
//		BlueCAN 蓝牙can，      42 6C 75 65 43 41 4E			07 42 6C 75 65 43 41 4E
//		VoiceBox 语音播报盒     56 6F 69 63 65 42 6F 78		08 56 6F 69 63 65 42 6F 78
        //String periphera = "VoiceBox";
        switch (periphera) {
            case "SmartEye":
                s1 = "7E 00 39 00 36 ";
                s4 = "08 53 6D 61 72 74 45 79 65";
                break;
            case "DID":
                s1 = "7E 00 39 00 31 ";
                s4 = "03 44 49 44";
                break;
            case "OilSensor":
                s1 = "7E 00 39 00 37 ";
                s4 = "09 4F 69 6C 53 65 6E 73 6F 72";
                break;
            case "BlueCAN":
                s1 = "7E 00 39 00 35 ";
                s4 = "07 42 6C 75 65 43 41 4E";
                break;
            case "VoiceBox":
                s1 = "7E 00 39 00 36 ";
                s4 = "08 56 6F 69 63 65 42 6F 78";
                break;

            default:
                break;
        }

        String msg1 = s1 + imei + s2 + dateTime + s3 + s4 + s5;
        //2个外设
        //String msg1 = "7E 00 39 00 60 01 50 90 00 00 08 6D 5F 02 00 0B 06 18 10 30 10 26 07 FF 00 02 05 01 0A 09 4F 69 6C 53 65 6E 73 6F 72 01 10 05 52 53 34 38 35 01 0B 04 FF FF FF FF 01 0C 04 04 0B 00 00 00 BE 04 01 04 02 04 05 01 0A 08 33 47 2D 56 69 64 65 6F 01 10 05 52 53 34 38 35 01 0B 04 11 00 51 21 01 0C 04 03 05 00 00 00 BE 04 00 00 00 00 27 7E";
        return msg1;
    }

    /**
     * 转为byte数组
     *
     * @param input
     * @return
     */
    public static byte[] transfer2Bytes(String input) {
        String[] arrs = input.split(" ");
        byte[] bytes = new byte[arrs.length];
        for (int i = 0; i < arrs.length; i++) {
            bytes[i] = (byte) Integer.parseInt(arrs[i], 16);
        }
        setNewFlag(bytes);
        return bytes;
    }

    /**
     * 计算并修改校验位
     *
     * @param bs
     */
    private static void setNewFlag(byte[] bs) {
        // byte[] da = new byte[bs.length - 15];
        // System.arraycopy(bs, 13, da, 0, da.length);
        // System.arraycopy(bs, 13, da, 0, da.length);0902 校验2次
        // ctbox校验位
        // bs[bs.length - 4] = setNewFlagSub(da);
        // 然后gnns校验位
        int idx = bs.length - 2;
        bs[idx] = bs[1];
        for (int j = 2; j < idx; j++) {
            bs[idx] ^= bs[j];
        }
        //System.out.println("校验位:" + bs[idx] + "||" + Integer.toHexString(bs[idx]));
    }

    /**
     * 修改7d7e
     *
     * @param b
     * @return
     */
    public static byte[] edit7d7e(byte[] b) {
        List<String> list_7d7e = new ArrayList<String>();
        b = subBytes(b, 1, b.length - 2);
        for (int i = 0; i < b.length; i++) {
            if ("125".equals(String.valueOf(b[i]))) {
                String str = String.valueOf(i) + "," + "7d";
                list_7d7e.add(str);
            }
            if ("126".equals(String.valueOf(b[i]))) {
                String str = String.valueOf(i) + "," + "7e";
                list_7d7e.add(str);
            }
        }
        for (int i = 0; i < list_7d7e.size(); i++) {
            String str[] = list_7d7e.get(i).split(",");
            if (str[1].equals("7d")) {
                byte head[] = cutOutByte(b, Integer.valueOf(str[0]));
                int left = (b.length - head.length);
                byte mid[] = {(byte) Integer.parseInt("7d", 16), (byte) Integer.parseInt("01", 16)};
                byte tail[] = subBytes(b, Integer.valueOf(str[0]) + 1, left - 1);
                b = getMergeBytes(head, mid);
                b = getMergeBytes(b, tail);
                if (i == list_7d7e.size() - 1)
                    break;
                for (int j = (i + 1); j < list_7d7e.size(); j++) {
                    String next_str[] = list_7d7e.get(j).split(",");
                    int new_index = Integer.valueOf(next_str[0]) + 1;
                    list_7d7e.set(j, new_index + "," + next_str[1]);
                }

            }
            if (str[1].equals("7e")) {
                byte head[] = cutOutByte(b, Integer.valueOf(str[0]));
                int left = (b.length - head.length);
                byte mid[] = {(byte) Integer.parseInt("7d", 16), (byte) Integer.parseInt("02", 16)};
                byte tail[] = subBytes(b, Integer.valueOf(str[0]) + 1, left - 1);
                b = getMergeBytes(head, mid);
                b = getMergeBytes(b, tail);
                if (i == list_7d7e.size() - 1)
                    break;
                for (int j = (i + 1); j < list_7d7e.size(); j++) {
                    String next_str[] = list_7d7e.get(j).split(",");
                    int new_index = Integer.valueOf(next_str[0]) + 1;
                    list_7d7e.set(j, new_index + "," + next_str[1]);
                }
            }
        }
        byte tmp[] = {(byte) Integer.parseInt("7e", 16)};
        b = getMergeBytes(tmp, b);
        b = getMergeBytes(b, tmp);
        list_7d7e.clear();
        return b;
    }

    public static byte[] getMergeBytes(byte[] pByteA, byte[] pByteB) {
        int aCount = pByteA.length;
        int bCount = pByteB.length;
        byte[] b = new byte[aCount + bCount];
        for (int i = 0; i < aCount; i++) {
            b[i] = pByteA[i];
        }
        for (int i = 0; i < bCount; i++) {
            b[aCount + i] = pByteB[i];
        }
        return b;
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        for (int i = begin; i < begin + count; i++)
            bs[i - begin] = src[i];
        return bs;
    }

    public static byte[] cutOutByte(byte[] b, int j) {
        if (b.length == 0 || j == 0) {
            return null;
        }
        byte[] bjq = new byte[j];
        for (int i = 0; i < j; i++) {
            bjq[i] = b[i];
        }
        return bjq;
    }

    /**
     * 将byte[]转换为16进制字符串
     *
     * @param byteArray
     * @return
     */
    public static String byteArrayToHexStr(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[byteArray.length * 2];
        for (int i = 0; i < byteArray.length; i++) {
            int v = byteArray[i] & 0xFF;
            hexChars[i * 2] = hexArray[v >> 4];
            hexChars[i * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * 字符串按2位长度拆分，中间添加分隔符
     *
     * @param fromStr
     * @param splitStr
     * @return
     */
    public static String getString(String fromStr, String splitStr) {
        String toStr = "";
        for (int i = 0; i < fromStr.length(); i++) {
            if (i % 2 == 0) {
                String tmp = fromStr.substring(i, i + 2);
                toStr = toStr + splitStr + tmp;
            }
        }
        toStr = toStr.substring(1, toStr.length());
        return toStr;
    }

}
