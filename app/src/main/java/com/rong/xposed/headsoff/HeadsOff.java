package com.rong.xposed.headsoff;

import com.rong.xposed.headsoff.utils.SettingValues;
import com.rong.xposed.headsoff.x.SystemUIHook;
import com.rong.xposed.headsoff.x.XLog;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

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
public class HeadsOff implements IXposedHookLoadPackage, IXposedHookZygoteInit {

	private static final String PACKAGE_NAME_SYSTEMUI = "com.android.systemui";
	private static final String PACKAGE_NAME_HEADS_OFF = HeadsOff.class.getPackage().getName();
	private static final String LOG_TAG = "[HeadsOff]: ";

	public static XSharedPreferences prefs;

	public static void loadPrefs() {
		prefs = new XSharedPreferences(PACKAGE_NAME_HEADS_OFF, SettingValues.PREF_FILE);
		prefs.makeWorldReadable();
		boolean status = XLog.updateLogStatus(prefs);
		XLog.logAlways(LOG_TAG, "loadPrefs: log status = " + status);
	}

	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		loadPrefs();
	}

	@Override
	public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

		if (lpparam.packageName.equals(PACKAGE_NAME_SYSTEMUI)) {
			// in SystemUI
			SystemUIHook.initHooks(prefs, lpparam);
		}

	}

}
