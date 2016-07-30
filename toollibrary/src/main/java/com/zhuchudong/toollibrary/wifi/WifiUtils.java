package com.zhuchudong.toollibrary.wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * wifi热点 管理器
 *
 * @author sam
 */
public class WifiUtils {

    /* 获得已连接到本机热点的设备IP */
    public static ArrayList<String> getConnectedIP() {
        ArrayList<String> connectedIP = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    String ip = splitted[0];
                    connectedIP.add(ip);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connectedIP;
    }

    /* 获得热点帐号与密码 */
    public static WifiConfiguration getWifiAPInfo(Context context) {
        WifiConfiguration ap = null;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        try {
            Method localmethod = wifiManager.getClass().getMethod("getWifiApConfiguration", new Class[0]);
            ap = (WifiConfiguration) localmethod.invoke(wifiManager, new Object[0]);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ap;
    }

    /* 开启/关闭热点 */
    public static boolean setWifiApEnabled(Context context, boolean enabled) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (enabled) { // disable WiFi in any case
            // wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
            wifiManager.setWifiEnabled(false);
        }

        WifiConfiguration ap = null;

        try {
            Method localmethod = wifiManager.getClass().getMethod("getWifiApConfiguration", new Class[0]);
            ap = (WifiConfiguration) localmethod.invoke(wifiManager, new Object[0]);
            // 帐号或密码为空的状态下设置默认帐号密码
            if (ap.SSID == null || ap.preSharedKey == null) {
                ap.SSID = "default";
                ap.preSharedKey = "123456789";
            }

            // 热点的配置类
            WifiConfiguration apConfig = new WifiConfiguration();
            // 配置热点的名称(可以在名字后面加点随机数什么的)
            apConfig.SSID = ap.SSID;
            apConfig.preSharedKey = ap.preSharedKey;
            apConfig.allowedKeyManagement.set(4);

            Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            // 返回热点打开状态
            return (Boolean) method.invoke(wifiManager, apConfig, enabled);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String long2ip(long ip) {
        StringBuffer sb = new StringBuffer();
        sb.append(String.valueOf((int) (ip & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 8) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 16) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 24) & 0xff)));
        return sb.toString();
    }

    /**
     * 判断是否连接WIFI
     * @param context  上下文
     * @return  boolean
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }

}
