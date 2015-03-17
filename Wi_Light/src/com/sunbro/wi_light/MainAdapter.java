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

import android.R.color;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

public class MainAdapter extends BaseAdapter {
	private ProgressDialog progDlg;
	private Socket client;
	private ServerSocket server;
	private String msg;
	private String recievingMsg;
	private int port = 55555;
	private String name;
	private String ip;
	private String serialNum;
	private Exception exceptionToBeThrown;

	private List<Device> devs;
	private ListView devList;
	private LayoutInflater myInflater;
	private Activity parentActivity;
	private TextView nameTextView;
	private Button onBtn;
	private Button offBtn;
	private Switch LanWanSwitch;
	private int positionInt;

	public MainAdapter(List<Device> devs, ListView devList, Context context,
			Activity parentActivity) {
		this.devs = devs;
		this.devList = devList;
		this.myInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.parentActivity = parentActivity;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return devs.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return devs.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	private void AlertToMain(String str) {
		if (progDlg != null && progDlg.isShowing()) {
			progDlg.dismiss();
		}
		AlertDialog.Builder alert = new AlertDialog.Builder(parentActivity);
		alert.setMessage(str);
		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		final AlertDialog dlg = alert.create();

		dlg.show();
	}

	@SuppressLint({ "InflateParams", "ClickableViewAccessibility" })
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {		
		if (convertView == null) {
			convertView = myInflater.inflate(R.layout.list_view_row, null);
		}
		final RelativeLayout rlayout = (RelativeLayout) convertView
				.findViewById(R.id.listRelativeLayout);
		if(devs.get(position).isOnoff() == true){
			rlayout.setBackgroundColor(0xffFFFFCC);
		}else{
			rlayout.setBackgroundColor(color.transparent);
		}
		
		nameTextView = (TextView) convertView
				.findViewById(R.id.NameListTextView);
		nameTextView.setText(devs.get(position).getName());

		onBtn = (Button) convertView.findViewById(R.id.onBtn);
		offBtn = (Button) convertView.findViewById(R.id.offBtn);
		LanWanSwitch = (Switch) convertView.findViewById(R.id.LanWanSwitch);
		
		if(devs.get(position).isLanWanSwitch()){
			LanWanSwitch.setChecked(true);
		}else{
			LanWanSwitch.setChecked(false);
		}
		
		LanWanSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked) {
					devs.get(position).setLanWanSwitch(true);
					LanWanSwitch.setChecked(true);
				} else {
					devs.get(position).setLanWanSwitch(false);
					LanWanSwitch.setChecked(false);
				}
			}
		});

		nameTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				positionInt = position;
				progDlg = ProgressDialog.show(parentActivity, "Progressing",
						"Please wait.");

				// t = new Timer();
				// t.schedule(new TimerTask() {
				// public void run() {
				// t.cancel();
				// AlertToMain("Network connection lost.");
				// }
				// }, 10000);

				msg = "MainCmd:state";
				String currentIP = getLocalIpv4Address();
				if (LanWanSwitch.isChecked() == true) {
					currentIP = getLocalIpv4Address();
					if (currentIP.startsWith("192.")) {
						name = devs.get(position).getName();
						ip = devs.get(position).getLanIP();
						serialNum = devs.get(position).getSerialNum();
						SendMessage senMessageTask = new SendMessage();
						senMessageTask.execute();
					} else {
						AlertToMain("Your phone is not in a LAN yet.\n\nPlease set the switch to the proper position.\nOr check your network.");
						return;
					}
				} else {
					if (!currentIP.startsWith("192.")) {
						name = devs.get(position).getName();
						ip = devs.get(position).getWanIP();
						if (ip.equals("")) {
							AlertToMain("Please set the WAN IP for your device first.");
							return;
						}
						serialNum = devs.get(position).getSerialNum();
						SendMessage senMessageTask = new SendMessage();
						senMessageTask.execute();
					} else {
						AlertToMain("Your phone is not in a WAN yet.\n\nPlease set the switch to the proper position.\nOr check your network.");
						return;
					}
				}
			}
		});

		onBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				positionInt = position;
				msg = "MainCmd:ON";
				String currentIP = getLocalIpv4Address();
				if (LanWanSwitch.isChecked() == true) {
					currentIP = getLocalIpv4Address();
					if (currentIP.startsWith("192.")) {
						ip = devs.get(position).getLanIP();
						serialNum = devs.get(position).getSerialNum();
						SendMessage senMessageTask = new SendMessage();
						senMessageTask.execute();
						devs.get(position).setOnoff(true);
						//rlayout.setBackgroundColor(0xffFFFFCC);
					} else {
						AlertToMain("Your phone is not in a LAN yet.\n\nPlease set the switch to the proper position.\nOr check your network.");
						return;
					}
				} else {
					if (!currentIP.startsWith("192.")) {
						ip = devs.get(position).getWanIP();
						if (ip.equals("")) {
							AlertToMain("Please set the WAN IP for your device first.");
							return;
						}
						serialNum = devs.get(position).getSerialNum();
						SendMessage senMessageTask = new SendMessage();
						senMessageTask.execute();
						devs.get(position).setOnoff(true);
						//rlayout.setBackgroundColor(0xffFFFFCC);
					} else {
						AlertToMain("Your phone is not in a WAN yet.\n\nPlease set the switch to the proper position.\nOr check your network.");
						return;
					}
				}

			}
		});

		offBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				positionInt = position;
				msg = "MainCmd:OFF";
				String currentIP = getLocalIpv4Address();
				if (LanWanSwitch.isChecked() == true) {
					if (currentIP.startsWith("192.")) {
						ip = devs.get(position).getLanIP();
						serialNum = devs.get(position).getSerialNum();
						SendMessage senMessageTask = new SendMessage();
						senMessageTask.execute();
						devs.get(position).setOnoff(false);
						//rlayout.setBackgroundColor(Color.TRANSPARENT);
					} else {
						AlertToMain("Your phone is not in a LAN yet.\n\nPlease set the switch to the proper position,\nOr check your network.");
						return;
					}
				} else {
					if (!currentIP.startsWith("192.")) {
						ip = devs.get(position).getWanIP();
						if (ip.equals("")) {
							AlertToMain("Please set the WAN IP for your device first.");
							return;
						}
						serialNum = devs.get(position).getSerialNum();
						SendMessage senMessageTask = new SendMessage();
						senMessageTask.execute();
						devs.get(position).setOnoff(false);
						//rlayout.setBackgroundColor(Color.TRANSPARENT);
					} else {
						AlertToMain("Your phone is not in a WAN yet.\n\nPlease set the switch to the proper position,\nOr check your network.");
						return;
					}
				}

			}
		});
		// convertView.setOnTouchListener(this);
		return convertView;
	}

	/*
	 * @Override public boolean onTouch(View v, MotionEvent event) { // TODO
	 * Auto-generated method stub float currentX = event.getX();
	 * 
	 * RelativeLayout layout =
	 * (RelativeLayout)v.findViewById(R.id.listRelativeLayout);
	 * switch(event.getAction()){ case MotionEvent.ACTION_DOWN: this.mLastX =
	 * currentX; break; //case MotionEvent.ACTION_MOVE: // break; case
	 * MotionEvent.ACTION_MOVE: if(currentX + sensitivity> this.mLastX){
	 * layout.setBackgroundColor(0xffF1DF96); msg = "ON"; } if(currentX -
	 * sensitivity < this.mLastX ){ layout.setBackgroundColor(Color.GRAY); msg =
	 * "OFF"; } ip = devs.get(v.getId()).getIP(); serialNum =
	 * devs.get(v.getId()).getSerialNum(); SendMessage senMessageTask = new
	 * SendMessage(); senMessageTask.execute(); break; } return true; }
	 */

	public class SendMessage extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
				String currentIP = getLocalIpv4Address();
				client = new Socket(ip, port); // connect to the server
				PrintWriter out = new PrintWriter(client.getOutputStream(),
						true);
				out.write("SerialNum:" + serialNum + ",DST_IP:" + currentIP
						+ "," + msg);
				out.flush();
				out.close();
				client.close();

				if (msg.equals("MainCmd:state")) {
					server = new ServerSocket(port);
					server.setSoTimeout(10000);
					BufferedReader in = new BufferedReader(
							new InputStreamReader(server.accept()
									.getInputStream()));
					String resposeFromServer = null;
					resposeFromServer = in.readLine();

					in.close();
					server.close();
					msg = "";
					recievingMsg = resposeFromServer;
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
			if (progDlg != null && progDlg.isShowing()) {
				progDlg.dismiss();
			}
			if (exceptionToBeThrown == null) {
				if (recievingMsg != null) {
					if (recievingMsg.equals("on")) {
						devs.get(positionInt).setOnoff(true);
						AlertToMain(name + " is currently ON.");
					} else if (recievingMsg.equals("off")) {
						devs.get(positionInt).setOnoff(false);
						AlertToMain(name + " is currently OFF.");
					}
					recievingMsg = "";
				}				
				MainAdapter adapter = new MainAdapter(devs, devList ,parentActivity, parentActivity);
				devList.setAdapter(adapter);
			} else {
				exceptionToBeThrown = null;
				AlertToMain("System Exception Error.\n\nPlease check your network, and device setting.");
			}
		}
	}

	public static String getLocalIpv4Address() {
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
