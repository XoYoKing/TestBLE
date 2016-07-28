package com.wt.testble;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wt.testble.bluetooth.SingletonBLE;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyDeviceAdapter extends BaseAdapter {
	private static final String TAG = "MyDeviceAdapter";
	private Context mContext;
	private LayoutInflater mInflater = null;
	private SharedPreferences mPref;
	//private BluetoothLEHelper mBluetoothLEHelper;
	public Handler m_handlerOnItemClick = null;
	public MyDeviceAdapter(Context ctx,Handler handlerOnItemClick) {
		m_handlerOnItemClick=handlerOnItemClick;
		mContext = ctx;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPref = ctx.getSharedPreferences(Constant.SHARE_PREF_KEY,
				Activity.MODE_PRIVATE);
		//mBluetoothLEHelper = BluetoothLEHelper.getInstance(ctx);
	}

	@Override
	public int getCount() {
		return SingletonBLE.getDeviceList().size();
	}

	@Override
	public Map<String, Object> getItem(int position) {
		return SingletonBLE.getDeviceList().get(position);
	}

	@Override
	public long getItemId(int position) {
		if (SingletonBLE.getDeviceList().get(position) == null) {
			return 0;
		}
		return SingletonBLE.getDeviceList().get(position).hashCode();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater
					.inflate(R.layout.mydevice_item_layout, null);
		}
		final MyDeviceViewHolder holder = new MyDeviceViewHolder();
		holder.mInfoName = (TextView) convertView
				.findViewById(R.id.devices_text);
		holder.mDeviceState = (TextView) convertView
				.findViewById(R.id.devices_state);
		holder.mDeviceState.setVisibility(View.INVISIBLE);
		
		holder.mConnectedState = (TextView) convertView
				.findViewById(R.id.devices_connect_state);
		String strConnectedState = mContext.getResources().getString(R.string.mydevice_unconnected);
		Map<String, Object> deviceMap = SingletonBLE.getDeviceList().get(position);
		if (deviceMap != null) {
			if (deviceMap!= null) {
				String strDeviceName=(String)deviceMap.get(SingletonBLE.FIELD_NAME);
				String strAddress=(String)deviceMap.get(SingletonBLE.FIELD_ADDRESS);
				if ( strAddress.length()>2 )
				{
					strDeviceName="TUWAN"+"("+strAddress+")";				
				}				
				holder.mInfoName.setText(strDeviceName);
				boolean bConnected=(boolean)deviceMap.get(SingletonBLE.FIELD_ISCONNECT);
				if ( bConnected )
				{
					strConnectedState = mContext.getResources().getString(R.string.mydevice_connected);					
				}
			}
		}
		holder.mConnectedState.setText(strConnectedState);
		/////////////////////
		holder.mDeviceState.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// do something
				Message msg = new Message(); 
				msg.what=MyDeviceActivity.MSG_CLICK_RIGHT;
				msg.arg1=position;
				msg.obj=v;
				if (m_handlerOnItemClick!=null) m_handlerOnItemClick.sendMessage(msg);
			}
		});
		/////////////////////		
		return convertView;
	}
}

class MyDeviceViewHolder {
	TextView mInfoName;
	TextView mDeviceState;
	TextView mConnectedState;
}
