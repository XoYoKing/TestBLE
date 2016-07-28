package com.wt.testble;

import android.bluetooth.BluetoothDevice;

public class MyBluetoothDevice {
	private String deviceName;

	public MyBluetoothDevice(String name, BluetoothDevice device){
		this.deviceName = name;
		this.device = device;
	}
	
	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public BluetoothDevice getDevice() {
		return device;
	}

	public void setDevice(BluetoothDevice device) {
		this.device = device;
	}

	private BluetoothDevice device;
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof MyBluetoothDevice) {
			MyBluetoothDevice other = (MyBluetoothDevice) o;
			return other.deviceName.equals(this.deviceName);
		}
		return false;
	}
}
