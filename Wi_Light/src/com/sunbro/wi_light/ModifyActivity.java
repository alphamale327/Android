package com.sunbro.wi_light;

import com.google.gson.Gson;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class ModifyActivity extends Activity {
	private EditText WanIptextField;
	private EditText NametextField;
	private String SSIDStr;
	private String pwdStr;
	private String LanIPStr;
	private String WanIPStr;
	private String SerialStr;
	private String NameStr;
	private Button ConfirmBtn;
	private Device device;
	private String sharedPreferName = "myPrefer";
	private SharedPreferences myPrefs;

	private boolean finishBool = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify);

		myPrefs = getSharedPreferences(sharedPreferName, MODE_PRIVATE);
		Gson gson = new Gson();
		final String position = Integer.toString(getIntent().getExtras()
				.getInt("position"));
		String json = myPrefs.getString(position, "");
		Device dev = gson.fromJson(json, Device.class);

		// SSIDtextField = (EditText) findViewById(R.id.ModifySSIDeditText);
		// pwdtextField = (EditText) findViewById(R.id.ModifypwdeditText);
		WanIptextField = (EditText) findViewById(R.id.ModifyWanIPeditText);
		// SerialNumtextField = (EditText)
		// findViewById(R.id.ModifySerialNumEditText);
		NametextField = (EditText) findViewById(R.id.ModifyNameEditText);
		ConfirmBtn = (Button) findViewById(R.id.ModifyConfirmBtn);

//		SSIDtextField.setText(dev.getSSID());
//		pwdtextField.setText(dev.getpwd());
		WanIptextField.setText(dev.getWanIP());
//		SerialNumtextField.setText(dev.getSerialNum());
		NametextField.setText(dev.getName());

		SSIDStr = dev.getSSID();
		pwdStr = dev.getpwd();
		SerialStr = dev.getSerialNum();
		LanIPStr = dev.getLanIP();
		// oldSSIDStr = dev.getSSID();
		// oldpwdStr = dev.getpwd();

		ConfirmBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// SSIDStr = SSIDtextField.getText().toString();
				// pwdStr = pwdtextField.getText().toString();
				WanIPStr = WanIptextField.getText().toString();
				// SerialStr = SerialNumtextField.getText().toString();
				NameStr = NametextField.getText().toString();
				
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				// if (SSIDStr.length() == 0) {
				// modifyAlert("Please enter\nthe SSID of your Wi-Fi network.");
				// SSIDtextField.requestFocus();
				// imm.showSoftInput(SSIDtextField,
				// InputMethodManager.SHOW_IMPLICIT);
				// return;
				// }

				// if (pwdStr.length() == 0) {
				// modifyAlert("Please enter\nthe Password of your Wi-Fi network.");
				// pwdtextField.requestFocus();
				// imm.showSoftInput(pwdtextField,
				// InputMethodManager.SHOW_IMPLICIT);
				// return;
				// }

				// if (WanIPStr.length() == 0) {
				// modifyAlert("Please enter\nthe WAN IP address of the device.");
				// WanIptextField.requestFocus();
				// imm.showSoftInput(WanIptextField,
				// InputMethodManager.SHOW_IMPLICIT);
				// return;
				// }
				// if (SerialStr.length() == 0) {
				// modifyAlert("Please enter\nthe Serial Number of the device.");
				// SerialNumtextField.requestFocus();
				// imm.showSoftInput(SerialNumtextField,
				// InputMethodManager.SHOW_IMPLICIT);
				// return;
				// }
				if (NameStr.length() == 0) {
					modifyAlert("Please enter\na Name of the device.");
					NametextField.requestFocus();
					imm.showSoftInput(NametextField,
							InputMethodManager.SHOW_IMPLICIT);
					return;
				}

				// SSIDtextField.setText("");
				// pwdtextField.setText("");
				// WanIptextField.setText("");
				// SerialNumtextField.setText("");
				// NametextField.setText("");

				device = new Device(SSIDStr, pwdStr, LanIPStr, WanIPStr,
						SerialStr, NameStr);
				Editor prefsEditor = myPrefs.edit();
				Gson gson = new Gson();
				String json = gson.toJson(device);
				prefsEditor.remove(position);
				prefsEditor.commit();
				prefsEditor.putString(position, json);
				prefsEditor.commit();
				// Context context = getParent();
				// Intent intent = new Intent(context, MainActivity.class);
				// setResult(RESULT_OK, intent);

				/*
				 * ArrayList<Device> devArr = new ArrayList<Device>(); for (int
				 * i = 0; myPrefs.contains(Integer.toString(i)); i++) { json =
				 * myPrefs.getString(Integer.toString(i), ""); Device obj =
				 * gson.fromJson(json, Device.class); devArr.add(obj); }
				 * 
				 * ModifyAdapter modifyAdapter = new ModifyAdapter(devArr,
				 * ModifyActivity.this, ModifyActivity.this, myPrefs);
				 * 
				 * ListView devList = (ListView) findViewById(R.id.devList);
				 * devList.setAdapter(modifyAdapter);
				 */
				finish();

			}
		});
	}

	private void modifyAlert(String str) {
		AlertDialog.Builder alert = new AlertDialog.Builder(ModifyActivity.this);
		alert.setMessage(str);

		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

				if (finishBool == true) {
					finish();
				} else {
					dialog.dismiss();
				}
			}

		});
		alert.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.modify, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
