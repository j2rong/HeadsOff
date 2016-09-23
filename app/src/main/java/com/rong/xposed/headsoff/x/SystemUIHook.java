package com.rong.xposed.headsoff.x;

import android.app.Notification;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

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
public class SystemUIHook {


	public static void initHooks(XSharedPreferences prefs, XC_LoadPackage.LoadPackageParam lpparam) {
		ClassLoader classLoader = (lpparam == null) ? null : lpparam.classLoader;
		String packageName = (lpparam == null) ? null : lpparam.packageName;

		List<XHook> list = new ArrayList<>();
		list.add(new XHook_BaseStatusBar_shouldInterrupt(prefs, classLoader, packageName));

		try {
			for (XHook hook : list) {
				hook.initHook();
			}
		} catch (Throwable t) {
			XLog.logAlways("SystemUIHook", "initHook() failed.");
			XLog.logAlways(t);
		}
	}


	public static class XHook_BaseStatusBar_shouldInterrupt extends XHook {

		private static final String CLASS_NAME_StatusBarNotification = "android.service.notification.StatusBarNotification";
		private static final String CLASS_NAME_BaseStatusBar = "com.android.systemui.statusbar.BaseStatusBar";
		private static final String CLASS_NAME_Entry = "com.android.systemui.statusbar.NotificationData.Entry";

		private static final String HOOK_METHOD_shouldInterrupt = "shouldInterrupt";

		private static final String FIELD_NAME_pkg_OF_StatusBarNotification = "pkg";
		private static final String FIELD_NAME_notification_OF_Entry = "notification";
		private static final String FIELD_NAME_notification_OF_StatusBarNotification = "notification";

		private static final int PARAM_INDEX_sbn_L = 0;
		private static final int PARAM_INDEX_sbn_M = 1;
		private static final int PARAM_INDEX_entry_M = 0;

		protected static final String LOG_TAG = "SUI:s";

		public XHook_BaseStatusBar_shouldInterrupt(XSharedPreferences p, ClassLoader c, String pkg) {
			super(p, c, pkg);
		}

		private final int s = Build.VERSION.SDK_INT;

		@Override
		public void initHook() {
			Class<?> classStatusBarNotification =
					XposedHelpers.findClass(CLASS_NAME_StatusBarNotification, classLoader);

			XLog.log(LOG_TAG, String.format(Locale.US, "sdk = %d", s));
			if (s >= 21 && s <= 22) {
				findAndHookMethod(CLASS_NAME_BaseStatusBar, classLoader, HOOK_METHOD_shouldInterrupt,
						classStatusBarNotification, this);
			} else if (s == 23) {
				Class<?> classEntry = XposedHelpers.findClass(CLASS_NAME_Entry, classLoader);
				findAndHookMethod(CLASS_NAME_BaseStatusBar, classLoader, HOOK_METHOD_shouldInterrupt,
						classEntry, classStatusBarNotification, this);
			} else {
				XLog.logAlways(LOG_TAG, "Unsupported SDK version.");
			}
		}

		@Override
		protected void before(MethodHookParam param) throws Throwable {
		}

		@Override
		protected void after(MethodHookParam param) throws Throwable {
			boolean ret = (boolean) param.getResult();

			if (!ret) {
				return;
			}

			XUtil.reloadPrefs(prefs);

			if (!XUtil.isModEnabled(prefs)) {
				return;
			}

			if (XUtil.isHeadsOffAll(prefs)) {
				param.setResult(false);
				return;
			}


			Object n = null;

			if (s >= 21 && s <= 22) {
				n = param.args[PARAM_INDEX_sbn_L];    
			} else if (s == 23) {
				n = param.args[PARAM_INDEX_sbn_M];
			}

			if (n == null && s == 23) {
				XLog.log(LOG_TAG, "try to get p name here from 1");
				Object e = param.args[PARAM_INDEX_entry_M];
				if (e != null) {
					n = XposedHelpers.getObjectField(e, FIELD_NAME_notification_OF_Entry);
				}
			}

			if (n != null) {
				String p = (String) XposedHelpers.getObjectField(n, FIELD_NAME_pkg_OF_StatusBarNotification);

				if (p != null) {
					if (XUtil.hasWhiteList(prefs, p)) {
						Notification notification = (Notification) XposedHelpers.getObjectField(
								n, FIELD_NAME_notification_OF_StatusBarNotification);
						List<String> whiteList = XUtil.getWhiteList(prefs, p);

						if (whiteList != null) {
							for (String pattern : whiteList) {
								if (XUtil.match(notification, pattern)) {
									XLog.log(LOG_TAG, String.format(Locale.US, "match(pattern=%s)", pattern));
									return;
								}
							}
						}
					}

					boolean bShouldBlock = XUtil.shouldBlock(prefs, p);

					if (bShouldBlock) {
						XLog.log(LOG_TAG, String.format(Locale.US, "block package:%s (true)", p));
						param.setResult(false);
					}
				}

			} else {
				// should not goes here
				XLog.log(LOG_TAG, "getting sbn object error, n = null");
			}
		}


	}




}
