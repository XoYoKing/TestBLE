package com.wt.testble;

import com.wt.testble.bluetooth.SingletonBLE;
import com.wt.testble.bluetooth.blelib.BleService;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchingDeviceActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "SearchingDeviceActivity";
	
	private ImageView imageViewSearchScanStart1;
	private RadarImageView imageViewSearchScanStart2;
	private TextView textViewStartStopSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//SingletonGlobal.pushActivity(this);
		
		setContentView(R.layout.activity_searching);
		initHandlerMe();
	}

	@Override
	protected void onResume() {
		super.onResume();
		initHandlerMe();
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
	                Toast.makeText(SearchingDeviceActivity.this,text,duration).show();
					break;
				case SingletonBLE.MSG_BLE_DEVICE_CHANGE:
					Log.d(TAG, "onReceive-refreshDevices");
					break;
				case SingletonBLE.MSG_BLE_SCAN_FIND_DEVICE:
					Log.d(TAG, "onReceive-MSG_BLE_SCAN_FIND_DEVICE");
					//finish();
					break;
				case SingletonBLE.MSG_BLE_ACTION_SCAN_FINISHED:
					Log.d(TAG, "onReceive-MSG_BLE_ACTION_SCAN_FINISHED");
					finish();
					break;
				}
			}
		};
		SingletonBLE.setHandlerOUT(handlerMe);
    }
    
	private boolean m_bInScanDevice=false;
	public void setInScanDevice(boolean bInScanDevice) {
		m_bInScanDevice=bInScanDevice;
		if ( m_bInScanDevice )
		{
			textViewStartStopSearch.setText("取消搜索");
			imageViewSearchScanStart2.setVisibility(View.VISIBLE);
			SingletonBLE.scanLeDeviceStart(BleService.TIMEOUT_SCAN_PERIOD);
		}
		else
		{
			textViewStartStopSearch.setText("开始搜索");
			imageViewSearchScanStart2.setVisibility(View.INVISIBLE);
			SingletonBLE.scanLeDeviceStop();
		}
	}
	@Override
	public void initView() {
		setTitleBarTitle("");//R.string.about_title);
		imageViewSearchScanStart1 = (ImageView) findViewById(R.id.imageViewSearchScanStart1);
		imageViewSearchScanStart1.setOnClickListener(this);
		imageViewSearchScanStart2 = (RadarImageView) findViewById(R.id.imageViewSearchScanStart2);
		imageViewSearchScanStart2.setOnClickListener(this);
		textViewStartStopSearch = (TextView) findViewById(R.id.textViewStartStopSearch);
		textViewStartStopSearch.setOnClickListener(this);

		setInScanDevice(true);
		
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
	public void onClick(View arg0) {
		switch (arg0.getId()) {
        case R.id.imageViewSearchScanStart1:
        	//mBluetoothLEHelper.scanDevices();
            break;
        case R.id.imageViewSearchScanStart2:
        	//mBluetoothLEHelper.scanDevices();
            break;
        case R.id.textViewStartStopSearch:
    		//mBluetoothLEHelper.stopScan();
    		//finish();
        	if ( m_bInScanDevice )
        	{
        		//mBluetoothLEHelper.stopScan();
        		setInScanDevice(false);        		
        	}
        	else
        	{
        		//mBluetoothLEHelper.scanDevices();
        		setInScanDevice(true);        		        		
        	}
            break;
        default:
            break;
        }
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}
