package com.rong.xposed.headsoff.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HeadsOff
 * Created by Rong on 2016/2/25.
 */
public class AppUtils {

	public static List<Map<String, Object>> getAppList(Context context, boolean bShowSystemApps) {
		PackageManager pm = context.getPackageManager();
		List<ApplicationInfo> apps = pm.getInstalledApplications(0);

		List<Map<String, Object>> retList = new ArrayList<>();
		if (apps == null) {
			return retList;    //size = 0
		}

		for (ApplicationInfo info : apps) {
			if (bShowSystemApps ||
					(info.flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) == 0) {
				Map<String, Object> r = new HashMap<>();
				r.put(SettingValues.KEY_APP_NAME, pm.getApplicationLabel(info));
				r.put(SettingValues.KEY_PKG_NAME, info.packageName);
				r.put(SettingValues.KEY_APP_ICON, pm.getApplicationIcon(info));
				retList.add(r);
			}
		}

		//sort
		Collections.sort(retList, new Comparator<Map<String, Object>>() {

			private Collator c = Collator.getInstance(java.util.Locale.CHINA);

			@Override
			public int compare(Map<String, Object> lhs, Map<String, Object> rhs) {
				String s1 = (String) lhs.get(SettingValues.KEY_APP_NAME);
				String s2 = (String) rhs.get(SettingValues.KEY_APP_NAME);
				return c.compare(s1, s2);
			}
		});

		return retList;
	}

}
