package com.sunbro.wi_light;

import java.util.List;

import com.google.gson.Gson;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class ModifyAdapter extends BaseAdapter {
	private List<Device> devs;
	private LayoutInflater myInflater;
	private Activity parentActivity;
	private TextView nameTextView;
	private ImageButton modifyBtn;
	private ImageButton deleteBtn;
	private SharedPreferences myPrefs;
	private Context context;

	public ModifyAdapter(List<Device> devs, Context context,
			Activity parentActivity, SharedPreferences myPrefs) {
		this.devs = devs;
		this.context = context;
		this.myInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.parentActivity = parentActivity;
		this.myPrefs = myPrefs;
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

	private void deleteAlert(String str, final int position) {
		AlertDialog.Builder alert = new AlertDialog.Builder(parentActivity);
		alert.setMessage(str);

		alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// Do nothing but close the dialog
				devs.remove(position);
			    Editor prefsEditor = myPrefs.edit();
			    prefsEditor.clear();
			    prefsEditor.commit();
			    			   			
				for (int i = 0; i < devs.size(); i++) {
					Gson gson = new Gson();
					String json = gson.toJson(devs.get(i));
					prefsEditor.putString(Integer.toString(i), json);
					prefsEditor.commit();
				}
				notifyDataSetChanged();
				dialog.dismiss();
			}

		});

		alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		AlertDialog alertDialog = alert.create();
		alertDialog.show();
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = myInflater.inflate(R.layout.modify_row, null);
		}
		nameTextView = (TextView) convertView
				.findViewById(R.id.ModifyNameListTextView);
		nameTextView.setText(devs.get(position).getName());
		modifyBtn = (ImageButton) convertView.findViewById(R.id.modifyBtn);
		deleteBtn = (ImageButton) convertView.findViewById(R.id.deleteBtn);

		modifyBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(parentActivity, ModifyActivity.class);
				intent.putExtra("position",position);				
				context.startActivity(intent);
			}
		});
		

		deleteBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				deleteAlert("Are you sure you want to delete this device?", position);				
			}
		});

		return convertView;
	}
}
