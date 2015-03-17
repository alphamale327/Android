package com.sunbro.wi_light;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import org.apache.http.conn.util.InetAddressUtils;
import com.google.gson.Gson;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class AddActivity extends Activity {
	private EditText SSIDtextField;
	private EditText pwdtextField;
	private EditText WanIptextField;
	private EditText SerialNumtextField;
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

	private ProgressDialog progDlg;
	private Socket client;
	private ServerSocket server;

	private String msg;
	private String recievingMsg;
	private int port = 55555;
	private String ip = "192.168.4.1";
	private Exception exceptionToBeThrown;
	private boolean finishBool = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);

		SSIDtextField = (EditText) findViewById(R.id.SSIDeditText);
		pwdtextField = (EditText) findViewById(R.id.pwdeditText);
		WanIptextField = (EditText) findViewById(R.id.WanIPeditText);
		SerialNumtextField = (EditText) findViewById(R.id.SerialNumEditText);
		NametextField = (EditText) findViewById(R.id.NameEditText);
		ConfirmBtn = (Button) findViewById(R.id.ConfirmBtn);
		ConfirmBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ConfirmBtn.setEnabled(false);
				progDlg = ProgressDialog.show(AddActivity.this, "Progressing",
						"Please wait.");
				//progDlg.setMax(4);
				// t = new Timer();
				// t.schedule(new TimerTask() {
				// public void run() {
				// t.cancel();
				// addAlert("Network connection lost.");
				// }
				// }, 20000);

				// mHandler.sendEmptyMessageAtTime(MSG_DISMISS_DIALOG,
				// TIME_OUT);

				// SSIDtextField.setText("lmnip");
				// pwdtextField.setText("lkss67142978");
				// WanIptextField.setText("4444");
				// SerialNumtextField.setText("proto1");
				// NametextField.setText("test");

				SSIDStr = SSIDtextField.getText().toString();
				pwdStr = pwdtextField.getText().toString();
				WanIPStr = WanIptextField.getText().toString();
				SerialStr = SerialNumtextField.getText().toString();
				NameStr = NametextField.getText().toString();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

				if (SerialStr.length() == 0) {
					addAlert("Please enter\nthe Serial Number of the device.");
					SerialNumtextField.requestFocus();
					imm.showSoftInput(SerialNumtextField,
							InputMethodManager.SHOW_IMPLICIT);
					return;
				}

				if (SSIDStr.length() == 0) {
					addAlert("Please enter\nthe SSID of your Wi-Fi network.");
					SSIDtextField.requestFocus();
					imm.showSoftInput(SSIDtextField,
							InputMethodManager.SHOW_IMPLICIT);
					return;
				}

				// if (pwdStr.length() == 0) {
				// addAlert("Please enter\nthe Password of your Wi-Fi network.");
				// pwdtextField.requestFocus();
				// imm.showSoftInput(pwdtextField,
				// InputMethodManager.SHOW_IMPLICIT);
				// return;
				// }

				if (NameStr.length() == 0) {
					addAlert("Please enter\na Name of the device.");
					NametextField.requestFocus();
					imm.showSoftInput(NametextField,
							InputMethodManager.SHOW_IMPLICIT);
					return;
				}
				/*
				 * SSIDtextField.setText(""); pwdtextField.setText("");
				 * WanIptextField.setText(""); SerialNumtextField.setText("");
				 * NametextField.setText("");
				 */
				msg = "SSID:" + SSIDStr + ",PWD:" + pwdStr;
				SendMessage senMessageTask = new SendMessage();
				senMessageTask.execute();

			}
		});
		addAlert("Please connect your phone to the device's SSID starting with \"SunBro_\" first.");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (server != null) {
			try {
				server.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// private Handler mHandler = new Handler() {
	// public void handleMessage(android.os.Message msg) {
	// switch (msg.what) {
	// case MSG_DISMISS_DIALOG:
	// if (progDlg != null && progDlg.isShowing()) {
	// addAlert("Connection Fail.\nPlease your network setting and device.");
	// }
	// break;
	//
	// default:
	// break;
	// }
	// }
	// };

	private void addAlert(String str) {
		AlertDialog.Builder alert = new AlertDialog.Builder(AddActivity.this);
		alert.setMessage(str);
		if (progDlg != null && progDlg.isShowing()) {
			progDlg.dismiss();
		}

		ConfirmBtn.setEnabled(true);
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

	private class SendMessage extends AsyncTask<Void, Integer, Void> {
		// @Override
		// protected void onProgressUpdate(Integer[] values) {
		// switch (values[0]) {
		// case 1:
		// progDlg.setMessage("Please wait.\nConnecting...");
		// break;
		// case 2:
		// progDlg.setMessage("Please wait.\nSending Data...");
		// break;
		// case 3:
		// progDlg.setMessage("Please wait.\nRecieving Data...");
		// break;
		// case 4:
		// progDlg.setMessage("Please wait.\nDone!");
		// break;
		// }
		// }

		@Override
		protected Void doInBackground(Void... params) {
			try {
				WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				String currentSSID = wifiInfo.getSSID();
				String currentIP = getLocalIpv4Address();
				if (currentSSID.contains("SunBro")) {
					// publishProgress(1);
					client = new Socket(ip, port); // connect to the server

					// publishProgress(2);
					PrintWriter out = new PrintWriter(client.getOutputStream(),
							true);
					out.write("WifiSetup-" + "SerialNum:" + SerialStr
							+ ",DST_IP:" + currentIP + "," + msg);
					out.flush();
					out.close();
					client.close();

					// publishProgress(3);
					server = new ServerSocket(port);
					server.setSoTimeout(20000); // confirmed 20000
					BufferedReader in = new BufferedReader(
							new InputStreamReader(server.accept()
									.getInputStream()));
					String resposeFromServer = null;
					resposeFromServer = in.readLine();

					// publishProgress(4);
					in.close();
					server.close();

					recievingMsg = resposeFromServer;

				} else {
					recievingMsg = "Please connect your phone to the device's SSID starting with \"SunBro_\".";
				}
			} catch (UnknownHostException e) {
				// e.printStackTrace();
				exceptionToBeThrown = e;

			} catch (IOException e) {
				// e.printStackTrace();
				exceptionToBeThrown = e;
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			if (exceptionToBeThrown == null) {
				if (recievingMsg.startsWith("192.168.")) {
					LanIPStr = recievingMsg;
					SharedPreferences myPrefs = getSharedPreferences(
							sharedPreferName, MODE_PRIVATE);
					device = new Device(SSIDStr, pwdStr, LanIPStr, WanIPStr,
							SerialStr, NameStr);
					Editor prefsEditor = myPrefs.edit();
					Gson gson = new Gson();
					String json = gson.toJson(device);
					int i = 0;

					while (myPrefs.contains(Integer.toString(i))) {
						i++;
					}

					prefsEditor.putString(Integer.toString(i), json);
					prefsEditor.commit(); // setResult(RESULT_OK, intent);
					finishBool = true;
					addAlert("Device added!");
				} else {
					addAlert(recievingMsg);
				}
			} else {
				addAlert("System Exception Error.\n\nPlease check your network, and device setting.");
				exceptionToBeThrown = null;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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

	private static String getLocalIpv4Address() {
		try {
			String ipv4;
			List<NetworkInterface> nilist = Collections.list(NetworkInterface
					.getNetworkInterfaces());
			if (nilist.size() > 0) {
				for (NetworkInterface ni : nilist) {
					List<InetAddress> ialist = Collections.list(ni
							.getInetAddresses());
					if (ialist.size() > 0) {
						for (InetAddress address : ialist) {
							if (!address.isLoopbackAddress()
									&& InetAddressUtils
											.isIPv4Address(ipv4 = address
													.getHostAddress())) {
								return ipv4;
							}
						}
					}

				}
			}

		} catch (SocketException ex) {

		}
		return "";
	}
}
