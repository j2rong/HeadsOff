package com.rong.xposed.headsoff.x;

import com.rong.xposed.headsoff.BuildConfig;
import com.rong.xposed.headsoff.utils.SettingValues;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;

/**
   Copyright (c) 2016-2017, j2Rong     
     
   Licensed under the Apache License, Version 2.0 (the "License");     
   you may not use this file except in compliance with the License.     
   You may obtain a copy of the License at     
     
       http://www.apache.org/licenses/LICENSE-2.0     
     
   Unless required by applicable law or agreed to in writing, software     
   distributed under the License is distributed on an "AS IS" BASIS,     
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.     
   See the License for the specific language governing permissions and     
   limitations under the License.   
*/
public class XLog {

	private static boolean bEnableLog = BuildConfig.SHOW_LOG;

	public static boolean updateLogStatus(XSharedPreferences p) {
		bEnableLog = p.getBoolean(SettingValues.KEY_ENABLE_LOG, BuildConfig.SHOW_LOG);
		return bEnableLog;
	}

	public static void log(String tag, String str) {
		if (bEnableLog) {
			XposedBridge.log(tag + str);
		}
	}

	public static void logAlways(String tag, String str) {
		XposedBridge.log(tag + str);
	}

	public static void logAlways(Throwable t) {
		XposedBridge.log(t);
	}
}
