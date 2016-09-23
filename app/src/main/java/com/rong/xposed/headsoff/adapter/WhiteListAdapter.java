package com.rong.xposed.headsoff.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rong.xposed.headsoff.R;
import com.rong.xposed.headsoff.utils.SettingValues;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class WhiteListAdapter extends RecyclerView.Adapter<WhiteListAdapter.ViewHolder> {

	private List<String> regexRuleList = null;
	private Context mContext = null;
	private final String packageName;
	private SharedPreferences prefs = null;
	private OnRegexRemoveListener listener = null;

	@SuppressLint("WorldReadableFiles")
	@SuppressWarnings("deprecation")
	public WhiteListAdapter(Context context, String pkg, List<String> list) {
		mContext = context;
		regexRuleList = list;
		packageName = pkg;
		prefs = context.getSharedPreferences(SettingValues.PREF_FILE, Context.MODE_WORLD_READABLE);
	}

	@Override
	public WhiteListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(mContext).inflate(R.layout.list_item_regex_rules, parent, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(WhiteListAdapter.ViewHolder holder, int position) {
		String regex = getItem(position);

		if (!TextUtils.isEmpty(regex)) {
			holder.txtRule.setText(regex);
		}
	}

	@Override
	public int getItemCount() {
		return (regexRuleList == null) ? 0 : regexRuleList.size();
	}

	public String getItem(int position) {
		if (regexRuleList == null || regexRuleList.size() == 0)
			return null;
		else
			return regexRuleList.get(position);
	}

	public void setItemList(List<String> list) {
		regexRuleList = list;
	}

	protected void onDelete(int position) {
		SharedPreferences.Editor editor = prefs.edit();
		String key;
		int newCount = getItemCount() - 1;


		key = packageName + SettingValues.KEY_SUFFIX_WHITELIST_COUNT;
		editor.putInt(key, newCount);
		editor.apply();


		Set<String> newList = new HashSet<>();
		for (int i = 0; i <= newCount; i++) {
			if (i == position)
				continue;
			newList.add(regexRuleList.get(i));
		}

		key = packageName + SettingValues.KEY_SUFFIX_WHITELIST;
		editor.putStringSet(key, newList);
		editor.apply();

		regexRuleList.remove(position);
		notifyItemRemoved(position);
		if (listener != null)
			listener.onRegexRemove(position);
	}

	public void setOnRegexRemoveListener(OnRegexRemoveListener l) {
		listener = l;
	}

	public interface OnRegexRemoveListener {
		void onRegexRemove(int position);
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		protected TextView txtRule;
		protected AppCompatImageButton btnDelete;

		public ViewHolder(View itemView) {
			super(itemView);

			txtRule = (TextView) itemView.findViewById(R.id.txt_rule);
			btnDelete = (AppCompatImageButton) itemView.findViewById(R.id.btn_delete);
			btnDelete.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			onDelete(getAdapterPosition());
		}
	}
}
