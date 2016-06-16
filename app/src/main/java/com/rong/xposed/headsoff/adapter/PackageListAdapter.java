package com.rong.xposed.headsoff.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rong.xposed.headsoff.PerAppWhiteList;
import com.rong.xposed.headsoff.R;
import com.rong.xposed.headsoff.utils.SettingValues;

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
public class PackageListAdapter extends RecyclerView.Adapter<PackageListAdapter.ViewHolder> {

	private Context mContext;
	private List<Map<String, Object>> mItemList = null;
	protected SharedPreferences prefs = null;

	private int colorHighlight = 0;
	private int colorDefault = 0;

	@SuppressLint("WorldReadableFiles")
	@SuppressWarnings("deprecation")
	public PackageListAdapter(Context context, List<Map<String, Object>> list) {
		mContext = context;
		mItemList = list;
		prefs = context.getSharedPreferences(SettingValues.PREF_FILE, Context.MODE_WORLD_READABLE);
		initColors();
	}

	private void initColors() {
		colorHighlight = ContextCompat.getColor(mContext, R.color.colorHighlightAppNameTextColor);
		colorDefault = ContextCompat.getColor(mContext, R.color.colorDefaultAppNameTextColor);
	}

	@Override
	public PackageListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(mContext).inflate(R.layout.list_item_package, parent, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(PackageListAdapter.ViewHolder holder, int position) {
		Map<String, Object> itemData = getItem(position);

		if (itemData != null) {
			holder.itemAppIcon.setImageDrawable((Drawable) (itemData.get(SettingValues.KEY_APP_ICON)));
			holder.itemAppName.setText((String) itemData.get(SettingValues.KEY_APP_NAME));
			String packageName = (String) itemData.get(SettingValues.KEY_PKG_NAME);
			holder.itemAppPackageName.setText(packageName);
			holder.itemSwitch.setChecked(isActive(packageName, SettingValues.DEFAULT_PACKAGE_ACTIVE));

			setupColor(holder, packageName);
		}
	}

	protected void setupColor(PackageListAdapter.ViewHolder holder, String packageName) {
		String key = packageName + SettingValues.KEY_SUFFIX_WHITELIST_COUNT;
		int count = prefs.getInt(key, SettingValues.DEFAULT_WHITELIST_COUNT);

		if (count != 0) {
			holder.itemAppName.setTextColor(colorHighlight);
			holder.itemAppPackageName.setTextColor(colorHighlight);
		} else {
			holder.itemAppName.setTextColor(colorDefault);
			holder.itemAppPackageName.setTextColor(colorDefault);
		}
	}

	@Override
	public int getItemCount() {
		return (mItemList == null) ? 0 : mItemList.size();
	}

	public Map<String, Object> getItem(int position) {
		if (mItemList == null || mItemList.size() == 0)
			return null;
		else
			return mItemList.get(position);
	}

	protected boolean isActive(String packageName, boolean defValue) {
		if (prefs != null) {
			return prefs.getBoolean(packageName + SettingValues.KEY_ACTIVE_SUFFIX, defValue);
		}
		return defValue;
	}

	public void setItemList(List<Map<String, Object>> list) {
		mItemList = list;
	}

	protected String getPackageName(int position) {
		Map<String, Object> itemData = getItem(position);
		if (itemData != null) {
			return (String) itemData.get(SettingValues.KEY_PKG_NAME);
		} else {
			return null;
		}
	}

	protected void openPerAppWhiteList(int position) {
		Intent intent = new Intent(mContext, PerAppWhiteList.class);
		Bundle param = new Bundle();

		param.putString(SettingValues.KEY_PKG_NAME, getPackageName(position));
		intent.putExtras(param);
		mContext.startActivity(intent);
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements
			CompoundButton.OnCheckedChangeListener, View.OnClickListener {

		public ImageView itemAppIcon;
		public TextView itemAppName;
		public TextView itemAppPackageName;
		public SwitchCompat itemSwitch;
		protected LinearLayout layoutLeft;

		public ViewHolder(View itemView) {
			super(itemView);

			itemAppIcon = (ImageView) itemView.findViewById(R.id.item_app_icon);
			itemAppName = (TextView) itemView.findViewById(R.id.item_app_name);
			itemAppPackageName = (TextView) itemView.findViewById(R.id.item_package_name);
			itemSwitch = (SwitchCompat) itemView.findViewById(R.id.item_switch);
			itemSwitch.setOnCheckedChangeListener(this);

			layoutLeft = (LinearLayout) itemView.findViewById(R.id.layout_left);
			layoutLeft.setOnClickListener(this);
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (prefs != null) {
				String pkgName = (String) itemAppPackageName.getText();
				SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean(pkgName + SettingValues.KEY_ACTIVE_SUFFIX, isChecked);
				editor.apply();
			}
		}

		@Override
		public void onClick(View v) {
			openPerAppWhiteList(getAdapterPosition());
		}
	}

}
