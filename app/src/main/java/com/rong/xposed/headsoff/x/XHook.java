package com.rong.xposed.headsoff.x;

import de.robv.android.xposed.XC_MethodHook;
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
public abstract class XHook extends XC_MethodHook {

	protected XSharedPreferences prefs;
	protected ClassLoader classLoader;
	protected String packageName;

	public XHook(XSharedPreferences p, ClassLoader c, String pkg) {
		prefs = p;
		classLoader = c;
		packageName = pkg;
	}

	public abstract void initHook();
	protected abstract void before(MethodHookParam param) throws Throwable;
	protected abstract void after(MethodHookParam param) throws Throwable;

	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		before(param);
	}

	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		after(param);
	}

}
