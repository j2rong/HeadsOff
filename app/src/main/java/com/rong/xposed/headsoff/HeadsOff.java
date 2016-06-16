package com.rong.xposed.headsoff;

import android.app.Notification;
import android.os.Build;
import android.os.Bundle;

import com.rong.xposed.headsoff.utils.SettingValues;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
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
	private static final String PACKAGE_NAME_HEADSOFF = HeadsOff.class.getPackage().getName();
	private static final String LOG_TAG = "[" + PACKAGE_NAME_HEADSOFF + "]: ";

	private static final String CLASS_NAME_StatusBarNotification = "android.service.notification.StatusBarNotification";

	private static final String CLASS_NAME_BaseStatusBar = "com.android.systemui.statusbar.BaseStatusBar";
	private static final String CLASS_NAME_Entry = "com.android.systemui.statusbar.NotificationData.Entry";

	private static final String FIELD_NAME_pkg_OF_StatusBarNotification = "pkg";
	private static final String FIELD_NAME_notification_OF_Entry = "notification";
	private static final String FIELD_NAME_notification_OF_StatusBarNotification = "notification";

	private static final String HOOK_METHOD_shouldInterrupt = "shouldInterrupt";

	public static XSharedPreferences prefs;
	private static boolean bEnableLog;

	public static void loadPrefs() {
		prefs = new XSharedPreferences(PACKAGE_NAME_HEADSOFF, SettingValues.PREF_FILE);
		prefs.makeWorldReadable();
		bEnableLog = prefs.getBoolean(SettingValues.KEY_ENABLE_LOG, BuildConfig.SHOW_LOG);
		logAlways("loadPrefs: log status = " + bEnableLog);
	}

	protected static void log(String t) {
		if (bEnableLog) {
			XposedBridge.log(LOG_TAG + t);
		}
	}

	protected static void logAlways(String t) {
		XposedBridge.log(LOG_TAG + t);
	}

	protected static void logAlways(Throwable t) {
		XposedBridge.log(t);
	}

	protected void reloadPrefs() {
		if (prefs.hasFileChanged()) {
			prefs.reload();
			bEnableLog = prefs.getBoolean(SettingValues.KEY_ENABLE_LOG, BuildConfig.SHOW_LOG);
		} else {
			log("reloadPrefs: pref file has not changed");
		}
	}

	
	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		loadPrefs();
	}


	protected boolean isModEnabled() {
		return prefs.getBoolean(SettingValues.KEY_MOD_ACTIVE, SettingValues.DEFAULT_MOD_ACTIVE);
	}

	
	protected boolean isHeadsOffAll() {
		return prefs.getBoolean(SettingValues.KEY_GLOBAL_ACTIVE, SettingValues.DEFAULT_GLOBAL_ACTIVE);
	}

	
	protected boolean shouldBlock(String pkg) {
		String prefKey = pkg + SettingValues.KEY_ACTIVE_SUFFIX;
		return prefs.getBoolean(prefKey, SettingValues.DEFAULT_PACKAGE_ACTIVE);
	}

	protected boolean hasWhiteList(String pkg) {
		String prefKey = pkg + SettingValues.KEY_SUFFIX_WHITELIST_COUNT;
		return prefs.getInt(prefKey, SettingValues.DEFAULT_WHITELIST_COUNT) != 0;
	}

	protected List<String> getWhiteList(String pkg) {
		String prefKey = pkg + SettingValues.KEY_SUFFIX_WHITELIST;
		Set<String> set = prefs.getStringSet(prefKey, null);
		return (set != null) ? new ArrayList<>(set) : null;
	}

	protected boolean match(Notification n, String pattern) {
		try {
			Pattern regex = Pattern.compile(pattern);
			Bundle extras = n.extras;

			List<String> texts = new ArrayList<>();

			CharSequence seq = extras.getCharSequence(Notification.EXTRA_TITLE);	
			if (seq != null)	texts.add(seq.toString());
			seq = extras.getCharSequence(Notification.EXTRA_TEXT);					
			if (seq != null)	texts.add(seq.toString());
			seq = extras.getCharSequence(Notification.EXTRA_SUB_TEXT);				
			if (seq != null)	texts.add(seq.toString());
			seq = extras.getCharSequence(Notification.EXTRA_INFO_TEXT);				
			if (seq != null)	texts.add(seq.toString());

			for (String str : texts) {
				Matcher matcher = regex.matcher(str);
				if (matcher.find())
					return true;
			}

			return false;
		} catch (Exception e) {
			log(String.format(Locale.US, "error match pattern=%s, n=%s, exception=%s",
					pattern, n.toString(), e.getMessage()));
			return false;
		}
	}

	@Override
	public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
		if (lpparam.packageName.equals(PACKAGE_NAME_SYSTEMUI)) {
			
			try {
				final int s = Build.VERSION.SDK_INT;

				XC_MethodHook m = new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param) throws Throwable {
						boolean ret = (boolean) param.getResult();
						if (!ret) {
							log("test false, no need to continue");
							return;
						}

						reloadPrefs();
						log(String.format(Locale.US, "version = %d", s));

						if (!isModEnabled()) {
							log("HeadsOff not enabled, do nothing");
							return;
						}

						if (isHeadsOffAll()) {
							log("Global heads-off mode!!");
							param.setResult(false);
							return;
						}


						Object n = null;

						if (s >= 21 && s <= 22) {
							n = param.args[0];  
						} else if (s == 23) {
							n = param.args[1]; 
						}

						if (n == null && s == 23) {
							log("try to get p name here from 1");
							Object e = param.args[0];
							if (e != null) {
								n = XposedHelpers.getObjectField(e, FIELD_NAME_notification_OF_Entry);
							}
						}

						if (n != null) {
							String p = (String) XposedHelpers.getObjectField(
									n, FIELD_NAME_pkg_OF_StatusBarNotification);

							if (p != null) {
								if (hasWhiteList(p)) {
									Notification notification = (Notification) XposedHelpers.getObjectField(
											n, FIELD_NAME_notification_OF_StatusBarNotification);
									List<String> whiteList = getWhiteList(p);
									if (whiteList != null) {
										for (String pattern : whiteList) {
											if (match(notification, pattern)) {
												log(String.format(Locale.US, "match(pattern=%s)", pattern));
												return;
											}
										}
									}
								}

								boolean bShouldBlock = shouldBlock(p);

								if (bShouldBlock) {
									log(String.format(Locale.US, "shouldBlock package:%s (true)", p));
									param.setResult(false);
								}
							}

						} else {
							log("getting sbn object error, n = null");
						}
					}
				};

				Class<?> classStatusBarNotification =
						XposedHelpers.findClass(CLASS_NAME_StatusBarNotification, lpparam.classLoader);

				if (s >= 21 && s <= 22) {
					//lollipop 5, 5.1
					findAndHookMethod(CLASS_NAME_BaseStatusBar, lpparam.classLoader, HOOK_METHOD_shouldInterrupt,
							classStatusBarNotification, m);
				} else if (s == 23) {
					Class<?> classEntry = XposedHelpers.findClass(CLASS_NAME_Entry, lpparam.classLoader);
					findAndHookMethod(CLASS_NAME_BaseStatusBar, lpparam.classLoader, HOOK_METHOD_shouldInterrupt,
							classEntry, classStatusBarNotification, m);
				} else {
					logAlways("Unsupported SDK version.");
				}

			} catch (Throwable t) {
				logAlways(t);
			}

		}

	}


}
