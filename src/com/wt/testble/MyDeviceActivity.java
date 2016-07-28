package com.wt.testble;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wt.testble.bluetooth.BluetoothCommand;
import com.wt.testble.bluetooth.SingletonBLE;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Handler.Callback;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyDeviceActivity extends BaseActivity implements
		OnItemClickListener, OnClickListener, OnItemLongClickListener {
	private static final String TAG = "MyDeviceActivity";
	
	private Button buttonAddDevice=null;
	private Button buttonQuXiao=null;
	private Button buttonDelDevice=null;
	private Button buttonSyncDevice=null;
	
	private ListView mMydeviceLv;
	private MyDeviceAdapter mMyDeviceAdapter;
	
	private static boolean m_bUpdateDevicetime=false;
	private static int m_iCountMax=10;
	private void updateDevicetime(int iCountMax) {
		if ( iCountMax<0 )
		{
			return;			
		}
		m_iCountMax=iCountMax;
		if ( m_bUpdateDevicetime )
		{
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				m_bUpdateDevicetime=true;
				int iCount = 0;
				while ( iCount < m_iCountMax ) 
				{
					Log.d(TAG, "updateDevicetime---------"+iCount);
					try {
						Thread.sleep(1000);
						SingletonBLE.updateDevicetime(true);
						Thread.sleep(1000);
						iCount++;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				m_bUpdateDevicetime=false;
			}
		}).start();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//SingletonGlobal.pushActivity(this);
		
		setContentView(R.layout.activity_my_device);
		setTitleBarTitle("我的设备");
		initHandlerMe();
		
//		refreshStart();
	}

	private Handler handlerMe=null;
    protected void initHandlerMe()
    {
        if ( handlerMe!=null )
        {
        	SingletonBLE.setHandlerOUT(handlerMe);
        	return;        	
        }
        handlerMe = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				switch (msg.what) {
				case SingletonBLE.MSG_BLE_TOAST:
					String text=(String)msg.obj;
					int duration=msg.arg1;					
	                Toast.makeText(MyDeviceActivity.this,text,duration).show();
					break;
				case SingletonBLE.MSG_BLE_DEVICE_CHANGE:
					Log.d(TAG, "onReceive-refreshDevices");
					refreshDevices(false);
					break;
				case SingletonBLE.MSG_BLE_ACTION_GATT_CONNECTED:
					Log.d(TAG, "onReceive-refreshDevices");
					refreshDevices(false);
					updateDevicetime(5);
					break;
				}
			}
		};
		SingletonBLE.setHandlerOUT(handlerMe);
    }
    
	@Override
	public boolean hasTitleBar() {
		return true;
	}

	@Override
	public boolean needBackPressedFadeAnim() {
		return true;
	}

	@Override
	public void initView() {
		mMydeviceLv = (ListView) findViewById(R.id.act_my_device_lv);
		mMyDeviceAdapter = new MyDeviceAdapter(this,m_handlerOnItemClick);
		mMydeviceLv.setAdapter(mMyDeviceAdapter);
		mMydeviceLv.setOnItemClickListener(this);
		mMydeviceLv.setOnItemLongClickListener(this);
		mTitleBarRightTxt.setText("    ");//R.string.mydevice_add);
		mTitleBarRightTxt.setOnClickListener(this);
		mTitleBarRightTxt.setVisibility(View.VISIBLE);
		
		buttonAddDevice=(Button) findViewById(R.id.buttonAddDevice);
		buttonAddDevice.setOnClickListener(this);
		buttonQuXiao=(Button) findViewById(R.id.buttonQuXiao);
		buttonQuXiao.setOnClickListener(this);
		buttonDelDevice=(Button) findViewById(R.id.buttonDelDevice);
		buttonDelDevice.setOnClickListener(this);
		buttonSyncDevice=(Button) findViewById(R.id.buttonSyncDevice);
		buttonSyncDevice.setOnClickListener(this);
		
		refreshDevices(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshDevices(true);
		updateDevicetime(3);
		initHandlerMe();
	}
	
	private synchronized void refreshDevices(boolean bChangePosition) {
		Log.d(TAG, "refreshDevices");
		mMyDeviceAdapter.notifyDataSetChanged();
		if ( bChangePosition ) setSelectPosition(-1,null);
	}

	private long g_lLastTimeOnItemClick=-1;
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long arg3) {
		Log.i(TAG, "onItemClick-view.getId()="+view.getId());
		if ( System.currentTimeMillis()-g_lLastTimeOnItemClick<1000*3 )
		{
			return;						
		}
		g_lLastTimeOnItemClick=System.currentTimeMillis();

		connectDevice( view,  position);		
		setSelectPosition(position,view);
	}
	public boolean isClickDeviceConnected(int position) {
		Map<String, Object> deviceMap = mMyDeviceAdapter.getItem(position);
		if (deviceMap == null)
		{
			Log.d(TAG,"isClickDeviceConnected-m_mydeviceClick==null");
			return false;			
		}
		boolean bConnected=(boolean)deviceMap.get(SingletonBLE.FIELD_ISCONNECT);
		return bConnected;
	}

	public boolean inNewDevicesList(BluetoothDevice device,List<BluetoothDevice> list) {
		if (device == null)
		{
			return false;			
		}
		Log.d(TAG, "BluetoothDevice-device="+device);
		for (BluetoothDevice var : list) {
			if (var != null) 
			{
				Log.d(TAG, "BluetoothDevice-var="+var);
				if ( var.getAddress().equalsIgnoreCase(device.getAddress()) )
				{
					return true;
				}
			}
		}
		return false;			
	}
	
	protected void connectDevice(View view, int position)
	{
		if (view == null)
		{
			Log.d(TAG,"connectDevice-m_viewClick==null");
			return;			
		}
		//判断当前设备连接状态
		TextView state = (TextView) view
				.findViewById(R.id.devices_connect_state);
		if ( isClickDeviceConnected(position)==false )
		{
			if (state != null) {
				String strConnectedState = this.getResources().getString(R.string.mydevice_isconnecting);
				state.setText(strConnectedState);
			}			
			Map<String, Object> deviceMap = mMyDeviceAdapter.getItem(position);
			if (deviceMap != null) {
				String strAddress=(String)deviceMap.get(SingletonBLE.FIELD_ADDRESS);
				if (strAddress!= null) {
					SingletonBLE.connect(strAddress);
				}
			}
		}
		else
		{
			if (state != null) {
				String strConnectedState = this.getResources().getString(R.string.mydevice_connected);
				state.setText(strConnectedState);
			}
		}
	}
			
	protected void showDialog(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message);
		builder.create().show();
	}

	@Override
	public void onClick(View arg0) {
		// mBluetoothHelper.startToScaning();
		Log.d(TAG, "to scaning");
		switch (arg0.getId()) {
        case R.id.view_titlebar_txtRightTitle:
    		//toActivity(SearchingDeviceActivity.class);
            break;
        case R.id.buttonAddDevice:
    		toActivity(SearchingDeviceActivity.class);
            break;
        case R.id.buttonQuXiao:
        	setSelectPosition(-1,null);
            break;
        case R.id.buttonDelDevice:
        	if ( m_iSelectPosition>=0 )
        	{
    			SingletonBLE.delDevice(m_iSelectPosition);
    			refreshDevices(true);        		
        	}
            break;
        case R.id.buttonSyncDevice:
        	if ( m_iSelectPosition>=0 && m_viewSelect!=null )
	        {
        		connectDevice( m_viewSelect,  m_iSelectPosition);        		
	        }
            break;
        default:
            break;
        }
	}
	
	private View m_viewSelect=null;
	private int m_iSelectPosition=-1;
	private void setSelectPosition(int iSelectPosition,View theView) {
		int iCount=mMydeviceLv.getCount();
		m_iSelectPosition=iSelectPosition;
		m_viewSelect=theView;
		if ( iCount<1 || m_iSelectPosition<0 || m_iSelectPosition>iCount-1 )
		{
			m_iSelectPosition=-1;
			m_viewSelect=null;
		}
		if ( m_iSelectPosition<0 )
		{
			buttonAddDevice.setVisibility(View.VISIBLE);
			buttonQuXiao.setVisibility(View.INVISIBLE);
			buttonDelDevice.setVisibility(View.INVISIBLE);
			//buttonSyncDevice.setVisibility(View.INVISIBLE);
		}
		else
		{
			buttonAddDevice.setVisibility(View.INVISIBLE);
			buttonQuXiao.setVisibility(View.VISIBLE);
			buttonDelDevice.setVisibility(View.VISIBLE);
			//buttonSyncDevice.setVisibility(View.VISIBLE);
		}
		buttonSyncDevice.setVisibility(View.INVISIBLE);
	}
	
	private void toActivity(Class<?> pActivity) {
        startActivity(new Intent(this, pActivity));
    }

	public static final int MSG_CLICK_LEFT=1001;
	public static final int MSG_CLICK_RIGHT=1002;
	private Handler m_handlerOnItemClick = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_CLICK_LEFT:
            	{
            		//int position=msg.arg1;  
            		//onItemClickLeft(position);
            	}
                break;
            case MSG_CLICK_RIGHT:
        	{
        		int position=msg.arg1;  
        		onItemClickRight(position,(View)msg.obj);
        	}
            break;
            default:
                break;
            }
        }
    };
    
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int position, long arg3) {
		return onItemClickRight(position,arg1);
	}

	private boolean onItemClickRight(int position,View theView) {
		Log.i(TAG, "onItemClickRight-position="+position);
		
		setSelectPosition(position,theView);
		
//		MyBluetoothDevice device = mMyDeviceAdapter.getItem(m_iSelectPosition);
//		if(!mBluetoothLEHelper.isSavedDevice(device.getDeviceName())){
//			return true;
//		}
		
		//mContinueDelConfirmDialog.show();
		return true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		updateDevicetime(3);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	//test code
	private Thread mRefreshThread;
	private void refreshStart() {
		mRefreshThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						
						Thread.sleep(5000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		mRefreshThread.start();
	}
}
