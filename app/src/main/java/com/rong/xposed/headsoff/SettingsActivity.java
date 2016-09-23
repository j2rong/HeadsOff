package com.rong.xposed.headsoff;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.rong.xposed.headsoff.utils.AppUtils;
import com.rong.xposed.headsoff.utils.SettingValues;

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
public class SettingsActivity extends AppCompatPreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppUtils.updateConfiguration(this);
		setupActionBar();

		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new GeneralPreferenceFragment())
				.commit();
	}

	private void setupActionBar() {
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			if (!super.onMenuItemSelected(featureId, item)) {
				NavUtils.navigateUpFromSameTask(this);
			}
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onBackPressed() {
		NavUtils.navigateUpFromSameTask(this);
	}

	protected boolean isValidFragment(String fragmentName) {
		return PreferenceFragment.class.getName().equals(fragmentName)
				|| GeneralPreferenceFragment.class.getName().equals(fragmentName);
	}

	public static class GeneralPreferenceFragment extends PreferenceFragment {

		@Override
		@SuppressLint("WorldReadableFiles")
		@SuppressWarnings("deprecation")
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			SharedPreferences modPrefs = getActivity().getSharedPreferences(
					SettingValues.PREF_FILE, Context.MODE_WORLD_READABLE);
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			SharedPreferences.Editor editorPrefs = prefs.edit();

			editorPrefs.putBoolean(SettingValues.KEY_PREF_GLOBAL_ACTIVE,
					modPrefs.getBoolean(SettingValues.KEY_GLOBAL_ACTIVE, SettingValues.DEFAULT_GLOBAL_ACTIVE));
			editorPrefs.putBoolean(SettingValues.KEY_PREF_SHOW_SYS_APP,
					modPrefs.getBoolean(SettingValues.KEY_SHOW_SYS_APP, SettingValues.DEFAULT_SHOW_SYS_APP));
			editorPrefs.putBoolean(SettingValues.KEY_PREF_MOD_ACTIVE,
					modPrefs.getBoolean(SettingValues.KEY_MOD_ACTIVE, SettingValues.DEFAULT_MOD_ACTIVE));
			editorPrefs.putBoolean(SettingValues.KEY_PREF_ENABLE_LOG,
					modPrefs.getBoolean(SettingValues.KEY_ENABLE_LOG, BuildConfig.SHOW_LOG));
			editorPrefs.apply();

			addPreferencesFromResource(R.xml.pref_general);

			Preference prefVersion = findPreference(SettingValues.KEY_PREF_VERSION_NAME);
			if (prefVersion != null) {
				prefVersion.setSummary(BuildConfig.VERSION_NAME);
			}

		}


		public void openWebPage(String url) {
			Uri webPage = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
			if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
				startActivity(intent);
			}
		}

		@Override
		public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
			String key = preference.getKey();
			switch (key) {
				case SettingValues.KEY_PREF_DEVELOPER:
					openWebPage("https://github.com/j2rong");
					return true;
				case SettingValues.KEY_PREF_DOWNLOAD:
					openWebPage("https://github.com/j2rong/HeadsOff/releases");
					return true;
				case SettingValues.KEY_PREF_XPOSED:
					openWebPage("http://repo.xposed.info/");
					return true;
			}

			return super.onPreferenceTreeClick(preferenceScreen, preference);
		}


		@Override
		public void onPause() {
			super.onPause();
			applySettings();
		}

		@SuppressLint("WorldReadableFiles")
		@SuppressWarnings("deprecation")
		public void applySettings() {
			SharedPreferences modPrefs = getActivity().getSharedPreferences(
					SettingValues.PREF_FILE, Context.MODE_WORLD_READABLE);
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

			SharedPreferences.Editor editorModPrefs = modPrefs.edit();
			editorModPrefs.putBoolean(SettingValues.KEY_GLOBAL_ACTIVE,
					prefs.getBoolean(SettingValues.KEY_PREF_GLOBAL_ACTIVE, SettingValues.DEFAULT_GLOBAL_ACTIVE));
			editorModPrefs.putBoolean(SettingValues.KEY_SHOW_SYS_APP,
					prefs.getBoolean(SettingValues.KEY_PREF_SHOW_SYS_APP, SettingValues.DEFAULT_SHOW_SYS_APP));
			editorModPrefs.putBoolean(SettingValues.KEY_MOD_ACTIVE,
					prefs.getBoolean(SettingValues.KEY_PREF_MOD_ACTIVE, SettingValues.DEFAULT_MOD_ACTIVE));
			editorModPrefs.putBoolean(SettingValues.KEY_ENABLE_LOG,
					prefs.getBoolean(SettingValues.KEY_PREF_ENABLE_LOG, BuildConfig.SHOW_LOG));
			editorModPrefs.apply();
		}

	}


}
