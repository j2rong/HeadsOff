package com.rong.xposed.headsoff.x;

import android.app.Notification;
import android.os.Bundle;

import com.rong.xposed.headsoff.utils.SettingValues;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.robv.android.xposed.XSharedPreferences;

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
public class XUtil {

	protected static final String LOG_TAG = "XU";

	public static void reloadPrefs(XSharedPreferences p) {
		if (p.hasFileChanged()) {
			p.reload();
			XLog.updateLogStatus(p);
		} else {
			XLog.log(LOG_TAG, "reloadPrefs: pref file has not changed");
		}
	}

	public static boolean isModEnabled(XSharedPreferences p) {
		return p.getBoolean(SettingValues.KEY_MOD_ACTIVE, SettingValues.DEFAULT_MOD_ACTIVE);
	}


	public static boolean isHeadsOffAll(XSharedPreferences p) {
		return p.getBoolean(SettingValues.KEY_GLOBAL_ACTIVE, SettingValues.DEFAULT_GLOBAL_ACTIVE);
	}


	public static boolean shouldBlock(XSharedPreferences p, String pkg) {
		String prefKey = pkg + SettingValues.KEY_ACTIVE_SUFFIX;
		return p.getBoolean(prefKey, SettingValues.DEFAULT_PACKAGE_ACTIVE);
	}

	public static boolean hasWhiteList(XSharedPreferences p, String pkg) {
		String prefKey = pkg + SettingValues.KEY_SUFFIX_WHITELIST_COUNT;
		return p.getInt(prefKey, SettingValues.DEFAULT_WHITELIST_COUNT) != 0;
	}

	public static List<String> getWhiteList(XSharedPreferences p, String pkg) {
		String prefKey = pkg + SettingValues.KEY_SUFFIX_WHITELIST;
		Set<String> set = p.getStringSet(prefKey, null);
		return (set != null) ? new ArrayList<>(set) : null;
	}

	public static boolean match(Notification n, String pattern) {
		try {
			Pattern regex = Pattern.compile(pattern);
			Bundle extras = n.extras;

			List<String> texts = new ArrayList<>();

			CharSequence seq = extras.getCharSequence(Notification.EXTRA_TITLE);	// contentTitle
			if (seq != null)	texts.add(seq.toString());
			seq = extras.getCharSequence(Notification.EXTRA_TEXT);					// contentText
			if (seq != null)	texts.add(seq.toString());
			seq = extras.getCharSequence(Notification.EXTRA_SUB_TEXT);				// subText
			if (seq != null)	texts.add(seq.toString());
			seq = extras.getCharSequence(Notification.EXTRA_INFO_TEXT);				// contentInfo
			if (seq != null)	texts.add(seq.toString());

			for (String str : texts) {
				Matcher matcher = regex.matcher(str);
				if (matcher.find())
					return true;
			}

			return false;
		} catch (Exception e) {
			XLog.log(LOG_TAG, String.format(Locale.US, "error match pattern=%s, n=%s, exception=%s",
					pattern, n.toString(), e.getMessage()));
			return false;
		}
	}

}
