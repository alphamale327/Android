package com.sunbro.wi_light;

import java.util.ArrayList;
import com.google.gson.Gson;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity {
	final Context context = this;
	private Button addBtn;
	private Button modifyBtn;
	private String sharedPreferName = "myPrefer";
	private boolean modifyMain = true;
	private ListView devList;
	private MainAdapter adapter;
	private ModifyAdapter modifyAdapter;
	private ArrayList<Device> devArr;
	private SharedPreferences myPrefs;
	private Gson gson = new Gson();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		addBtn = (Button) findViewById(R.id.addBtn);
		modifyBtn = (Button) findViewById(R.id.MainModifyBtn);
		devList = (ListView) findViewById(R.id.devList);

		myPrefs = getSharedPreferences(sharedPreferName, MODE_PRIVATE);

		devArr = new ArrayList<Device>();
		for (int i = 0; myPrefs.contains(Integer.toString(i)); i++) {
			String json = myPrefs.getString(Integer.toString(i), "");
			Device obj = gson.fromJson(json, Device.class);
			devArr.add(obj);
		}

		adapter = new MainAdapter(devArr, devList , MainActivity.this, MainActivity.this);
		devList.setAdapter(adapter);
		addBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), AddActivity.class);
				startActivityForResult(intent, 0);
			}
		});

		modifyBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (modifyMain == true) {
					modifyAdapter = new ModifyAdapter(devArr,
							MainActivity.this, MainActivity.this, myPrefs);
					devList.setAdapter(modifyAdapter);
					modifyBtn.setText("Home");
					addBtn.setEnabled(false);
					modifyMain = false;
				} else {
					adapter = new MainAdapter(devArr, devList, MainActivity.this,
							MainActivity.this);
					devList.setAdapter(adapter);
					modifyBtn.setText("Modify");
					addBtn.setEnabled(true);
					modifyMain = true;
				}

				/*
				 * prefsEditor = myPrefs.edit(); prefsEditor.clear();
				 * prefsEditor.commit(); devArr.clear();
				 * adapter.notifyDataSetChanged();
				 */
			}
		});
	}

	@Override
	protected void onActivityResult(int requstCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requstCode, resultCode, data);
		/*
		 * if (resultCode == RESULT_OK) { devArr = new ArrayList<Device>(); for
		 * (int i = 0; myPrefs.contains(Integer.toString(i)); i++) { String json
		 * = myPrefs.getString(Integer.toString(i), ""); Device obj =
		 * gson.fromJson(json, Device.class); devArr.add(obj); } adapter = new
		 * CustomAdapter(devArr, MainActivity.this, MainActivity.this);
		 * devList.setAdapter(adapter); }
		 */
	}

	public void onResume() {
		super.onResume(); // Always call the superclass method first
		devArr = new ArrayList<Device>();
		for (int i = 0; myPrefs.contains(Integer.toString(i)); i++) {
			String json = myPrefs.getString(Integer.toString(i), "");
			Device obj = gson.fromJson(json, Device.class);
			devArr.add(obj);
		}
		adapter = new MainAdapter(devArr, devList, MainActivity.this, MainActivity.this);
		devList.setAdapter(adapter);
		modifyBtn.setText("Modify");
		addBtn.setEnabled(true);
		modifyMain = true;
	}

	public void onBackPressed() {
		// super.onBackPressed();
		if (modifyMain == false) {
			adapter = new MainAdapter(devArr, devList, MainActivity.this,
					MainActivity.this);
			devList.setAdapter(adapter);
			modifyBtn.setText("Modify");
			addBtn.setEnabled(true);
			modifyMain = true;
		} else {
			AlertDialog.Builder alert = new AlertDialog.Builder(
					MainActivity.this);
			alert.setMessage("Do you want to exit?");

			alert.setPositiveButton("YES",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

							finish();
						}

					});

			alert.setNegativeButton("NO",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Do nothing
							dialog.dismiss();
						}
					});
			alert.show();
		}
	}
}