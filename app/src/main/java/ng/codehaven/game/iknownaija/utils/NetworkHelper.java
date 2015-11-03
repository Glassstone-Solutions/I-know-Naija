/*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package ng.codehaven.game.iknownaija.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

/**
 * Generic reusable network methods.
 */
public class NetworkHelper {
    /**
     * @param context to use to check for network connectivity.
     * @return true if connected, false otherwise.
     */
    public static boolean isOnline(Context context) {
        NetworkInfo networkInfo = getNetworkInfo(context);
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * @param context to use to check for network connectivity.
     * @return true if WiFi, false otherwise.
     */
    public static boolean isWiFi(Context context){
        boolean wifiIsConnected = false;
        NetworkInfo networkInfo = getNetworkInfo(context);
        if (networkInfo.isConnected()){
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null && !TextUtils.isEmpty(wifiInfo.getSSID())){
                wifiIsConnected = true;
            }
        }
        return wifiIsConnected;
    }

    private static NetworkInfo getNetworkInfo(Context context){
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connMgr.getActiveNetworkInfo();
    }
}
