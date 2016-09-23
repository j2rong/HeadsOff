package com.rong.xposed.headsoff;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import com.rong.xposed.headsoff.adapter.PackageListAdapter;
import com.rong.xposed.headsoff.adapter.PackageListItemDivider;
import com.rong.xposed.headsoff.utils.AppUtils;
import com.rong.xposed.headsoff.utils.SettingValues;
import com.rong.xposed.headsoff.x.XUtil;

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
public class MainActivity extends AppCompatActivity implements
		SwipeRefreshLayout.OnRefreshListener {

	private List<Map<String, Object>> mPackageList = null;
	private SwipeRefreshLayout mSwipeRefreshLayout = null;
	private PackageListAdapter mAdapter = null;

	private boolean bShowSystemApps = false;

	@Override
	@SuppressLint("WorldReadableFiles")
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppUtils.updateConfiguration(this);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		prefsReload();

		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorSchemeResources(
				R.color.swipe_refresh_progress_color_1,
				R.color.swipe_refresh_progress_color_2,
				R.color.swipe_refresh_progress_color_3);

		RecyclerView list = (RecyclerView) findViewById(R.id.app_list);
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
		list.setLayoutManager(layoutManager);
		list.setItemAnimator(new DefaultItemAnimator());
		list.addItemDecoration(new PackageListItemDivider(this));

		mAdapter = new PackageListAdapter(this, null);
		list.setAdapter(mAdapter);

		final View root = getWindow().getDecorView().findViewById(android.R.id.content);
		root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				mSwipeRefreshLayout.setRefreshing(true);
				onRefresh();
			}
		});
	}

	@SuppressLint("WorldReadableFiles")
	@SuppressWarnings("deprecation")
	private void prefsReload() {
		SharedPreferences prefs = getSharedPreferences(SettingValues.PREF_FILE, Context.MODE_WORLD_READABLE);
		bShowSystemApps = prefs.getBoolean(SettingValues.KEY_SHOW_SYS_APP, SettingValues.DEFAULT_SHOW_SYS_APP);
	}

	@Override
	public void onRefresh() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				prefsReload();
				mPackageList = AppUtils.getAppList(MainActivity.this, bShowSystemApps);
				mAdapter.setItemList(mPackageList);

				runOnUiThread(new Runnable() {
					public void run() {
						mAdapter.notifyDataSetChanged();
						mSwipeRefreshLayout.setRefreshing(false);
					}
				});
			}
		}).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.action_main_settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
