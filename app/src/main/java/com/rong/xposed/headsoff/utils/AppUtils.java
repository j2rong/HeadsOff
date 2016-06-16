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
public class AppUtils {

	public static List<Map<String, Object>> getAppList(Context context, boolean bShowSystemApps) {
		PackageManager pm = context.getPackageManager();
		List<ApplicationInfo> apps = pm.getInstalledApplications(0);

		List<Map<String, Object>> retList = new ArrayList<>();
		if (apps == null) {
			return retList;
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
