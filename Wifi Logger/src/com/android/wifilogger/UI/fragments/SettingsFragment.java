package com.android.wifilogger.UI.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockFragment;
import com.android.wifilogger.MyApplication;
import com.android.wifilogger.R;

public class SettingsFragment extends SherlockFragment {

	private static LayoutInflater inflater = null;
	String[] setupOptions;
	boolean[] setupOptionsValues;
	boolean wepSel;
	boolean wpaSel;
	boolean wpa2Sel;
	boolean essSel;
	boolean wpsSel;
	long radius;

	View textEntryView;
	EditText programName;
	int selectedOption;
	SharedPreferences settings;

	/** Called when the activity is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View content = inflater.inflate(R.layout.setup_specialsettings,
				container, false);
		// setContentView(R.layout.setup_specialsettings);

		settings = MyApplication.getPrivateSharedPreferences();

		wpaSel = settings.getBoolean(MyApplication.PREFS_SHOW_WPA, true);
		wpa2Sel = settings.getBoolean(MyApplication.PREFS_SHOW_WPA2, true);
		wepSel = settings.getBoolean(MyApplication.PREFS_SHOW_WEP, true);
		essSel = settings.getBoolean(MyApplication.PREFS_SHOW_ESS, true);
		wpsSel = settings.getBoolean(MyApplication.PREFS_SHOW_WPS, true);
		radius = settings.getLong(MyApplication.PREFS_RADIUS, 50);

		setupOptions = getResources().getStringArray(
				R.array.setup_specialsettingsoptions);

		setupOptionsValues = new boolean[5];
		setupOptionsValues[0] = wepSel;
		setupOptionsValues[1] = wpaSel;
		setupOptionsValues[2] = wpa2Sel;
		setupOptionsValues[3] = essSel;
		setupOptionsValues[4] = wpsSel;
		
		CompoundButton switchService;
		
		int version = android.os.Build.VERSION.SDK_INT;
		if (version >= 14) {
			switchService = (Switch) content
					.findViewById(R.id.switch_service);
		} else {
			switchService = (ToggleButton) content
					.findViewById(R.id.switch_service);
		}
		
		switchService.setChecked(MyApplication.isMyServiceRunning());

		switchService
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked)
							MyApplication.startService();
						else
							MyApplication.stopService();
					}
				});

		SeekBar radiusBar = (SeekBar) content.findViewById(R.id.seekbar_radius);

		final TextView radiusText = (TextView) content
				.findViewById(R.id.text_radius);

		radiusBar.setProgress((int) radius);
		radiusText.setText(String.valueOf(radius) + " km");

		radiusBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				radiusText.setText(String.valueOf(progress) + " km");
				radius = progress;
				SharedPreferences.Editor editor = settings.edit();
				editor.putLong(MyApplication.PREFS_RADIUS, radius);
				editor.commit();
			}
		});

		ListView list = (ListView) content
				.findViewById(R.id.setup_specialsettings_list);
		SetupAdapter listAdapter = new SetupAdapter(setupOptions);
		list.setAdapter(listAdapter);
		return content;
	}

	/*
	 * @Override public void
	 * onPrepareOptionsMenu(com.actionbarsherlock.view.Menu menu) { try {
	 * MenuItem refreshItem = menu.findItem(R.id.menu_refresh_btn);
	 * refreshItem.setEnabled(false); refreshItem.setVisible(false); } catch
	 * (Exception e) { Log.e("refresh item", "not found"); }
	 * Log.e("optionsMenü Settings", "Done"); super.onPrepareOptionsMenu(menu);
	 * };
	 */

	/* Extend BaseAdapter to display a custom formatted List Item View */
	public static class ViewHolder {
		public TextView p_title;
		public CheckBox p_checkbox;
	}

	public class SetupAdapter extends BaseAdapter {
		private String[] titles;

		public SetupAdapter(String[] pTitles) {
			titles = pTitles;
			inflater = MyApplication.getLayoutInflater();

		}

		public int getCount() {
			return titles.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View vi = convertView;
			ViewHolder holder;
			if (convertView == null) {
				vi = inflater
						.inflate(R.layout.setup_specialsettings_item, null);
				holder = new ViewHolder();
				holder.p_title = (TextView) vi
						.findViewById(R.id.setup_specialsettings_title);
				holder.p_checkbox = (CheckBox) vi
						.findViewById(R.id.setup_specialsettings_checkbox);
				vi.setTag(holder);

				holder.p_checkbox.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {

						int psn = Integer.parseInt(v.getTag().toString());
						SharedPreferences.Editor editor = settings.edit();
						if (psn == 0) {
							if (((CheckBox) v).isChecked()) {
								wepSel = true;
							} else {
								wepSel = false;
							}
							editor.putBoolean(MyApplication.PREFS_SHOW_WEP,
									wepSel);
						} else if (psn == 1) {
							if (((CheckBox) v).isChecked()) {
								wpaSel = true;
							} else {
								wpaSel = false;
							}
							editor.putBoolean(MyApplication.PREFS_SHOW_WPA,
									wpaSel);

						} else if (psn == 2) {
							if (((CheckBox) v).isChecked()) {
								wpa2Sel = true;
							} else {
								wpa2Sel = false;
							}
							editor.putBoolean(MyApplication.PREFS_SHOW_WPA2,
									wpa2Sel);

						} else if (psn == 3) {
							if (((CheckBox) v).isChecked()) {
								essSel = true;
							} else {
								essSel = false;
							}
							editor.putBoolean(MyApplication.PREFS_SHOW_ESS,
									essSel);

						} else if (psn == 4) {
							if (((CheckBox) v).isChecked()) {
								wpsSel = true;
							} else {
								wpsSel = false;
							}
							editor.putBoolean(MyApplication.PREFS_SHOW_WPS,
									wpsSel);

						}

						editor.apply();
					}
				});
			} else {
				holder = (ViewHolder) vi.getTag();
			}

			holder.p_title.setText(titles[position]);
			holder.p_checkbox.setChecked(setupOptionsValues[position]);

			holder.p_title.setTag(position);
			holder.p_checkbox.setTag(position);

			vi.requestLayout();

			return vi;
		}
	}
}
