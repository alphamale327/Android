package com.sunbro.wi_light;

public class Device {
	private String SSID;
	private String pwd;
	private String LanIP;
	private String WanIP;
	private String SerialNum;
	private String Name;
	private boolean onoff;
	private boolean lanWanSwitch = true;

	public Device() {

	}

	public Device(String SSID, String pwd, String LanIP, String WanIP,
			String SerialNum, String Name) {
		this.SSID = SSID;
		this.pwd = pwd;
		this.LanIP = LanIP;
		this.WanIP = WanIP;
		this.SerialNum = SerialNum;
		this.Name = Name;
	}

	public String getSSID() {
		return SSID;
	}

	public void setSSID(String SSID) {
		this.SSID = SSID;
	}

	public String getpwd() {
		return pwd;
	}

	public void setpwd(String pwd) {
		this.pwd = pwd;
	}

	public String getLanIP() {
		return LanIP;
	}

	public void setLanIP(String LanIP) {
		this.LanIP = LanIP;
	}

	public String getWanIP() {
		return WanIP;
	}

	public void setWanIP(String WanIP) {
		this.WanIP = WanIP;
	}

	public String getSerialNum() {
		return SerialNum;
	}

	public void setSerialNum(String SerialNum) {
		this.SerialNum = SerialNum;
	}

	public String getName() {
		return Name;
	}

	public void setName(String Name) {
		this.Name = Name;
	}

	public boolean isOnoff() {
		return onoff;
	}

	public void setOnoff(boolean onoff) {
		this.onoff = onoff;
	}

	public boolean isLanWanSwitch() {
		return lanWanSwitch;
	}

	public void setLanWanSwitch(boolean lanWanSwitch) {
		this.lanWanSwitch = lanWanSwitch;
	}
}