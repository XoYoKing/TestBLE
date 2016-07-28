package com.wt.testble.bluetooth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.wt.testble.R;
import com.wt.testble.bluetooth.adapter.CommonAdapter;
import com.wt.testble.bluetooth.blelib.BleService;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

//import java.io.Serializable;

public class SingletonBLE //implements Serializable 
{
    private static final String TAG = SingletonBLE.class.getSimpleName();
	
	private static class SingletonHolder {
		// 单例对象实例
		static final SingletonBLE INSTANCE = new SingletonBLE();
	}

	public static SingletonBLE getInstance() {
		return SingletonHolder.INSTANCE;
	}

	// private的构造函数用于避免外界直接使用new来实例化对象
	private SingletonBLE() {
		Log.i(TAG, "private的构造函数用于避免外界直接使用new来实例化对象");
	}

	// readResolve方法应对单例对象被序列化时候
	//private Object readResolve() {
	//	return getInstance();
	//}
	
	//=此单例的变量和函数=//
	private static Handler g_handlerOUT=null;
	public static void setHandlerOUT(Handler theHandler)
	{
		g_handlerOUT=theHandler;		
	}
	public static final int MSG_BLE_LOG	=1000;
	public static final int MSG_BLE_WRITE	=1001;
	public static final int MSG_BLE_READ	=1002;
	public static final int MSG_BLE_TOAST	=1003;
	public static final int MSG_BLE_DEVICE_CHANGE			=1004;
	public static final int MSG_BLE_SCAN_FIND_DEVICE		=1005;
	public static final int MSG_BLE_ACTION_GATT_CONNECTED	=1006;
	public static final int MSG_BLE_ACTION_SCAN_FINISHED	=1007;
	
	public static void addLog(String text)
	{
		Log.i(TAG,text);
		if ( g_handlerOUT==null ) return;
        Message message=new Message();
        message.what=MSG_BLE_LOG;
        message.obj=text;
        g_handlerOUT.sendMessage(message);
	}
	public static void showToast(String text,int duration)
	{
		if ( g_handlerOUT==null ) return;
        Message message=new Message();
        message.what=MSG_BLE_TOAST;
        message.arg1=duration;
        message.obj=text;
        g_handlerOUT.sendMessage(message);
        addLog(text);
	}
	public static void sendMSG_OUT(int iMessageWhat)
	{
		if ( g_handlerOUT==null ) return;
        Message message=new Message();
        message.what=iMessageWhat;
        g_handlerOUT.sendMessage(message);
	}
	
	//Constant
    public static final int SERVICE_BIND = 1;
    public static final int SERVICE_SHOW = 2;
    public static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;

    //Member fields
    private static BleService mBleService=null;
    private static boolean mIsBind=false;
    private static List<Map<String, Object>> deviceList=new ArrayList<>();
    private static CommonAdapter<Map<String, Object>> deviceAdapter=null;
    private static ArrayAdapter<String> serviceAdapter=null;

    public static List<Map<String, Object>> getDeviceList() {
    	return deviceList;
    }
    /*/
    //Layout view
    private Button btn_scanBle;
    private ListView lstv_devList;
    private ListView lstv_showService;
    //*/

    private static ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBleService = ((BleService.LocalBinder) service).getService();
            if (mBleService != null) mHandler.sendEmptyMessage(SERVICE_BIND);
            if (mBleService.initialize()) {
                if (mBleService.enableBluetooth(true)) {
                    verifyIfRequestPermission();
                    //showToast("Bluetooth was opened", Toast.LENGTH_SHORT);
                }
            } else {
                //showToast("not support Bluetooth", Toast.LENGTH_SHORT);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBleService = null;
            mIsBind = false;
        }
    };

    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SERVICE_BIND:
                    setBleServiceListener();
                    break;
                case SERVICE_SHOW:
                    if ( serviceAdapter!=null ) serviceAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private static void verifyIfRequestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            addLog( "onCreate: checkSelfPermission");
            if (ContextCompat.checkSelfPermission(g_theContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                addLog( "onCreate: Android 6.0 动态申请权限");

                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)g_theContext,
                        Manifest.permission.READ_CONTACTS)) {
                    addLog( "*********onCreate: shouldShowRequestPermissionRationale**********");
                    showToast("只有允许访问位置才能搜索到蓝牙设备", Toast.LENGTH_SHORT);
                } else {
                    ActivityCompat.requestPermissions((Activity)g_theContext,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_ACCESS_COARSE_LOCATION);
                }
            } else {
                showDialog(g_theContext.getResources().getString(R.string.scanning));
                scanLeDeviceStart(BleService.TIMEOUT_SCAN_PERIOD);
            }
        } else {
            showDialog(g_theContext.getResources().getString(R.string.scanning));
            scanLeDeviceStart(BleService.TIMEOUT_SCAN_PERIOD);
        }
    }
    
    private static void initView() {
    	/*/
        btn_scanBle = (Button) findViewById(R.id.btn_scanBle);
        lstv_devList = (ListView) findViewById(R.id.lstv_devList);
        lstv_showService = (ListView) findViewById(R.id.lstv_showService);
        TextView txtv = new TextView(this);
        txtv.setText("Services");
        lstv_showService.addHeaderView(txtv);
        lstv_showService.setVisibility(View.VISIBLE);
        btn_scanBle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBleService.isScanning()) {
                    verifyIfRequestPermission();
//                    mBleService.close();
                    deviceList.clear();
                    mBleService.scanLeDevice(true);
                }
            }
        });
        lstv_showService.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addLog( "position = " + position + ", id = " + id);
                String s = serviceList.get((int) id);
                Intent intent = new Intent(BleScanActivity.this, CharacteristicActivity.class);
                intent.putExtra("characteristic", characteristicList.get((int) id));
                startActivity(intent);
            }
        });
        //*/
    }

    private static void initAdapter() {
    	/*/
        //deviceList = new ArrayList<>();
        deviceAdapter = new CommonAdapter<Map<String, Object>>(
                this, R.layout.item_device, deviceList) {
            @Override
            public void convert(ViewHolder holder, final Map<String, Object> deviceMap) {
                holder.setText(R.id.txtv_name, deviceMap.get(FIELD_NAME).toString());
                holder.setText(R.id.txtv_address, deviceMap.get(FIELD_ADDRESS).toString());
                holder.setText(R.id.txtv_connState, ((boolean) deviceMap.get(FIELD_ISCONNECT)) ?
                        getResources().getString(R.string.state_connected) :
                        getResources().getString(R.string.state_disconnected));
                holder.setText(R.id.btn_connect, ((boolean) deviceMap.get(FIELD_ISCONNECT)) ?
                        getResources().getString(R.string.disconnected) :
                        getResources().getString(R.string.connected));
                holder.getView(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ((boolean) deviceMap.get(FIELD_ISCONNECT)) {
                            mBleService.disconnect();
                            showDialog(getString(R.string.disconnecting));
                        } else {
                            connDeviceAddress = (String) deviceMap.get(FIELD_ADDRESS);
                            connDeviceName = (String) deviceMap.get(FIELD_NAME);
                            HashMap<String, Object> connDevMap = new HashMap<String, Object>();
                            connDevMap.put(FIELD_NAME, connDeviceName);
                            connDevMap.put(FIELD_ADDRESS, connDeviceAddress);
                            connDevMap.put(FIELD_ISCONNECT, false);
                            deviceList.clear();
                            deviceList.add(connDevMap);
                            deviceAdapter.notifyDataSetChanged();
                            mBleService.connect(connDeviceAddress);
                            showDialog(getString(R.string.connecting));
                        }
                    }
                });
            }
        };
        lstv_devList.setAdapter(deviceAdapter);
        serviceList = new ArrayList<>();
        serviceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, serviceList);
        lstv_showService.setAdapter(serviceAdapter);
        //*/
    }

    private static List<BluetoothGattService> gattServiceList=null;
    private static List<String> serviceList=null;
    private static List<String[]> characteristicList=null;

    public static final String SERVICE_ID = "000018f0-0000-1000-8000-00805f9b34fb";
	public static final String READ_PRO = "00002af0-0000-1000-8000-00805f9b34fb";
	public static final String WRITE_PRO = "00002af1-0000-1000-8000-00805f9b34fb";
	public static final String NOTIFY_DESC = "00002902-0000-1000-8000-00805f9b34fb";
	
    private static void setBleServiceListener() {
        mBleService.setOnServicesDiscoveredListener(new BleService.OnServicesDiscoveredListener() {
            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    gattServiceList = gatt.getServices();
                    characteristicList = new ArrayList<>();
                    if ( serviceList!=null ) serviceList.clear();
                    for (BluetoothGattService service : gattServiceList) {
                    	/*/
                    	String serviceUuid = service.getUuid().toString();
                        if ( serviceList!=null ) serviceList.add(MyGattAttributes.lookup(serviceUuid, "Unknown") + "\n" + serviceUuid);
                        addLog( MyGattAttributes.lookup(serviceUuid, "Unknown") + "\n" + serviceUuid);

                        List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                        String[] charArra = new String[characteristics.size()];
                        for (int i = 0; i < characteristics.size(); i++) {
                            String charUuid = characteristics.get(i).getUuid().toString();
                            charArra[i] = MyGattAttributes.lookup(charUuid, "Unknown") + "\n" + charUuid;
                        }
                        if ( characteristicList!=null ) characteristicList.add(charArra);
                        //*/
                        mCurrentBluetoothGatt = gatt;
                        if ((service == null) || (service.getUuid() == null)) {
    						continue;
    					}
    					if (SERVICE_ID.equalsIgnoreCase(service
    							.getUuid().toString())) {
    						for (BluetoothGattCharacteristic iterable_element : service.getCharacteristics()) {
    							Log.d(TAG, iterable_element.getUuid()+"");
    						}
    						mReadChar = service.getCharacteristic(UUID
    								.fromString(READ_PRO));
    						mWriteChar = service.getCharacteristic(UUID
    								.fromString(WRITE_PRO));
    	                    mBleService.setCharacteristicNotification(mReadChar, true);
    					}
                    }
                    mHandler.sendEmptyMessage(SERVICE_SHOW);
                }
            }
        });
//      //Ble扫描回调
//      mBleService.setOnLeScanListener(new BleService.OnLeScanListener() {
//          @Override
//          public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
//              //每当扫描到一个Ble设备时就会返回，（扫描结果重复的库中已处理）
//          }
//      });
//      //Ble连接回调
//      mBleService.setOnConnectListener(new BleService.OnConnectListener() {
//          @Override
//          public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//              if (newState == BluetoothProfile.STATE_DISCONNECTED) {
//                  //Ble连接已断开
//              } else if (newState == BluetoothProfile.STATE_CONNECTING) {
//                  //Ble正在连接
//              } else if (newState == BluetoothProfile.STATE_CONNECTED) {
//                  //Ble已连接
//              } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
//                  //Ble正在断开连接
//              }
//          }
//      });
//      //Ble服务发现回调
//      mBleService.setOnServicesDiscoveredListener(new BleService.OnServicesDiscoveredListener() {
//          @Override
//          public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//
//          }
//      });
        
//      //Ble数据回调
//      mBleService.setOnDataAvailableListener(new BleService.OnDataAvailableListener() {
//          @Override
//          public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//              //处理特性读取返回的数据
//          }
//
//          @Override
//          public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
//              //处理通知返回的数据
//          }
//      @Override
//      public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
//
//      }
//      });

      //Ble数据回调
      mBleService.setOnDataAvailableListener(new BleService.OnDataAvailableListener() {
          @Override
          public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
              //处理特性读取返回的数据
              addLog( "onCharacteristicRead: status = " + status);
          }

          @Override
          public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
              //处理通知返回的数据
				if (READ_PRO.equalsIgnoreCase(characteristic.getUuid().toString())) 
				{
					final String data = characteristic.getStringValue(0);
					addLog( "onCharacteristicChanged data =" + data);
				}
				else
				{
		              addLog( "onCharacteristicChanged uuid =" + characteristic.getUuid().toString());					
				}
          }
	      @Override
	      public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
	    	  //
              addLog( "onDescriptorRead: status = " + status);
	      }
      });
        
        mBleService.setOnReadRemoteRssiListener(new BleService.OnReadRemoteRssiListener() {
            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                addLog( "onReadRemoteRssi: rssi = " + rssi);
            }
        });
    }

    private void doOperation() {
//      mBleService.initialize();//Ble初始化操作
//      mBleService.enableBluetooth(boolean enable);//打开或关闭蓝牙
//      mBleService.scanLeDevice(boolean enable, long scanPeriod);//启动或停止扫描Ble设备
//      mBleService.connect(String address);//连接Ble
//      mBleService.disconnect();//取消连接
//      mBleService.getSupportedGattServices();//获取服务
//      mBleService.setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
//      boolean enabled);//设置通知
//      mBleService.readCharacteristic(BluetoothGattCharacteristic characteristic);//读取数据
//      mBleService.writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] value);//写入数据
//      mBleService.close();//关闭客户端
    }
    public static void connect(String strAddress) {
    	addLog( "connect=" + strAddress);
    	if ( mBleService!=null ) mBleService.connect(strAddress);
    }
    public static void disconnect() {
    	addLog( "disconnect-g_strCurConnectedAddress=" + g_strCurConnectedAddress);
    	if ( mBleService!=null ) mBleService.disconnect();
    }
    private static boolean g_bDel=false;
    public static void delDevice(int iPostion) {
    	Map<String, Object> deviceMap = getDeviceByIndex(iPostion);
    	if ( deviceMap==null )
    	{
    		return;
    	}
    	String strAddress=(String)deviceMap.get(SingletonBLE.FIELD_ADDRESS);
    	if ( getAutoConnectDeviceAddress().equalsIgnoreCase(strAddress) )
        {
    		g_bDel=true;
        	disconnect();    		
        }
    	else
    	{
    		deviceList.remove(iPostion);
    		sendMSG_OUT(SingletonBLE.MSG_BLE_DEVICE_CHANGE);
    	}
    }

    /**
     * 绑定服务
     */
    private static Context g_theContext=null;
    public static void doBindService(Context theContext) {
    	g_theContext=theContext;
    	
        Intent serviceIntent = new Intent(g_theContext, BleService.class);
        g_theContext.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        
        initView();
        initAdapter();
        g_theContext.registerReceiver(bleReceiver, makeIntentFilter());
    }

    /**
     * 解绑服务
     */
    public static void doUnBindService() {
        if (mIsBind) {
        	g_theContext.unbindService(serviceConnection);
            mBleService = null;
            mIsBind = false;
        }
        g_theContext.unregisterReceiver(bleReceiver);
        g_theContext=null;
    }

    public static Map<String, Object> getDeviceByIndex(int index) {
    	if ( deviceList==null )
    	{
    		return null;
    	}
    	int iCount=deviceList.size();
    	if ( index>=0 && index<iCount )
    	{
    		Map<String, Object> deviceMap = deviceList.get(index);
    		return deviceMap;
    	}
		return null;
    }
    public static Map<String, Object> getDeviceByAddress(String strAddress) {
    	if ( deviceList==null )
    	{
    		return null;
    	}
    	int iCount=deviceList.size();
    	for ( int index=0;index<iCount;index++ )
    	{
    		Map<String, Object> deviceMap = deviceList.get(index);
    		String strAddressTMP=(String)deviceMap.get(SingletonBLE.FIELD_ADDRESS);
			if ( strAddress.equalsIgnoreCase(strAddressTMP) )
			{
	    		return deviceMap;
			}
    	}
		return null;
    }
    public static int getDeviceIndexByAddress(String strAddress) {
    	if ( deviceList==null )
    	{
    		return -1;
    	}
    	int iCount=deviceList.size();
    	for ( int index=0;index<iCount;index++ )
    	{
    		Map<String, Object> deviceMap = deviceList.get(index);
    		String strAddressTMP=(String)deviceMap.get(SingletonBLE.FIELD_ADDRESS);
			if ( strAddress.equalsIgnoreCase(strAddressTMP) )
			{
	    		return index;
			}
    	}
		return -1;    	
    }
    
    public static final String FIELD_NAME = "name";
    public static final String FIELD_ADDRESS = "address";
    public static final String FIELD_ISCONNECT = "isConnect";
    
    private static String g_strCurConnectedAddress = "";
    private static BroadcastReceiver bleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	addLog("onReceive-Action= " + intent.getAction());
        	boolean bNeedSendMSG_BLE_DEVICE_CHANGE=true;
            if (intent.getAction().equals(BleService.ACTION_BLUETOOTH_DEVICE)) {
                String tmpDevName = intent.getStringExtra(FIELD_NAME);
                String tmpDevAddress = intent.getStringExtra(FIELD_ADDRESS);
                //addLog( "find device: name: " + tmpDevName + ", address: " + tmpDevAddress);
                addLog( "find device: address: " + tmpDevAddress);
                Map<String, Object> deviceMap=getDeviceByAddress(tmpDevAddress);
                if ( deviceMap==null )
                {
                    deviceMap = new HashMap<>();                	
                    deviceMap.put(FIELD_NAME, tmpDevName);
                    deviceMap.put(FIELD_ADDRESS, tmpDevAddress);
                    deviceMap.put(FIELD_ISCONNECT, false);
                    if ( deviceList!=null ) deviceList.add(deviceMap);
                    if ( deviceAdapter!=null ) deviceAdapter.notifyDataSetChanged();
                }
                sendMSG_OUT(SingletonBLE.MSG_BLE_SCAN_FIND_DEVICE);
                //处理自动连接
                checkAutoConnect(tmpDevAddress);
            } else if (intent.getAction().equals(BleService.ACTION_GATT_CONNECTED)) {
            	if ( intent.hasExtra(FIELD_ADDRESS) )
            	{
            		g_strCurConnectedAddress = intent.getStringExtra(FIELD_ADDRESS);
                    addLog( "connected device: address: " + g_strCurConnectedAddress);
                    int index=getDeviceIndexByAddress(g_strCurConnectedAddress);
                	if ( index>=0 )
                	{
                    	deviceList.get(index).put(FIELD_ISCONNECT, true);
                    	setAutoConnectDeviceAddress((String)deviceList.get(index).get(SingletonBLE.FIELD_ADDRESS));
                    	if ( deviceAdapter!=null ) deviceAdapter.notifyDataSetChanged();
                        dismissDialog();            		                		
                	}
                	else
                	{
                    	if ( !g_strCurConnectedAddress.isEmpty() )
                    	{
                        	Map<String, Object> deviceMap = new HashMap<>();                	
                            deviceMap.put(FIELD_NAME, g_strCurConnectedAddress);
                            deviceMap.put(FIELD_ADDRESS, g_strCurConnectedAddress);
                            deviceMap.put(FIELD_ISCONNECT, true);
                        	setAutoConnectDeviceAddress(g_strCurConnectedAddress);
                            if ( deviceList!=null ) deviceList.add(deviceMap);
                            if ( deviceAdapter!=null ) deviceAdapter.notifyDataSetChanged();
                    	}                		
                	}
                	if ( !g_strCurConnectedAddress.isEmpty() )
                	{
                        sendMSG_OUT(SingletonBLE.MSG_BLE_ACTION_GATT_CONNECTED);                		
                		requestDataHandler.postDelayed(requestDataTimer,g_lExecuteCommandTimeStep);
                	}
            	}
            } else if (intent.getAction().equals(BleService.ACTION_GATT_DISCONNECTED)) {
            	if ( intent.hasExtra(FIELD_ADDRESS) )
            	{
                    String tmpDevAddress = intent.getStringExtra(FIELD_ADDRESS);
                    addLog( "disconnected device: address: " + tmpDevAddress);
                    int index=getDeviceIndexByAddress(tmpDevAddress);
                    g_strCurConnectedAddress="";
                	if ( index>=0 )
                	{                		
                    	deviceList.get(index).put(FIELD_ISCONNECT, false);
                    	if ( g_bDel )
                    	{
                    		deviceList.remove(index);
                    		setAutoConnectDeviceAddress("");
                        }
                    	g_bDel=false;
                    	if ( serviceList!=null ) serviceList.clear();
                    	if ( characteristicList!=null ) characteristicList.clear();
                        if ( deviceAdapter!=null ) deviceAdapter.notifyDataSetChanged();
                        if ( serviceAdapter!=null ) serviceAdapter.notifyDataSetChanged();
                        dismissDialog();
                	}
            	}
            	mCurrentBluetoothGatt=null;
            	mReadChar = null;
    			mWriteChar = null;            	
            } else if (intent.getAction().equals(BleService.ACTION_SCAN_FINISHED)) {
                //btn_scanBle.setEnabled(true);
                dismissDialog();
                sendMSG_OUT(SingletonBLE.MSG_BLE_ACTION_SCAN_FINISHED);
                if ( deviceList==null || deviceList.size()<1 )
                {
                	g_handlerScanDevice.postDelayed(runnableScanDevice,5000);
                }
            }
            sendMSG_OUT(SingletonBLE.MSG_BLE_DEVICE_CHANGE);
        }
    };
    private static Handler g_handlerScanDevice = new Handler();
	private static Runnable runnableScanDevice = new Runnable() {
		@Override
		public void run() {
			scanLeDeviceStart(BleService.TIMEOUT_SCAN_PERIOD);
			//g_handlerScanDevice.postDelayed(runnableScanDevice, 10);
		}
	};
	
	//默认询问数据时间间隔
	public static final long g_lExecuteCommandTimeOut=1500;//ms
	public static final long g_lExecuteCommandTimeStep=100;//ms
	private static final long L_delayMillis_requestData=1000*10;
	private static Handler requestDataHandler=new Handler();
	
	private static boolean m_bRequestDataGetSnoreCount=true;
	private static long lLastTimeRequestData=System.currentTimeMillis();
	private static long lLastTimeConnected=System.currentTimeMillis();
	private static boolean m_bRequestDataRuning=false;
	private static Runnable requestDataTimer=new Runnable() {
		@Override
		public void run() 
		{			
			if ( m_bRequestDataRuning )
			{
				return;
			}
			m_bRequestDataRuning=true;
			long lDelayMillis=L_delayMillis_requestData;
			if ( ( System.currentTimeMillis()-lLastTimeConnected )<1000*30 )
			{
				try {
		    		//mBluetoothLEHelper.syncCommandCOUNT();
		    		//SM
					updateDevicetime(true);
					//GD2
					long lTimeJG=100+g_lExecuteCommandTimeStep;
					Thread.sleep(lTimeJG);
					executeCommand(BluetoothCommand.DATA_GET_SNORE_COUNT,true);
					//GD4
					lTimeJG+=100+g_lExecuteCommandTimeStep;
					Thread.sleep(lTimeJG);
					executeCommand(BluetoothCommand.DATA_GET_HEAD_COUNT,true);
					//
					//SingletonGlobal.sendMessageDataUpdate();
					//SM
					Thread.sleep(lTimeJG);
					updateDevicetime(true);
					//SingletonGlobal.g_iCountReGetSleepTimeOK=0;
			        //SingletonGlobal.g_iCountReGetHelpSleepMusicOK=0;
			        //SingletonGlobal.g_iCountReGetWakeMusicOK=0;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else
			{
				//if ( SingletonGlobal.inModeSync() )
				{
					//lDelayMillis=SingletonGlobal.g_lExecuteCommandTimeStep;
				}
				if ( ( System.currentTimeMillis()-lLastTimeRequestData )>g_lExecuteCommandTimeStep )
				{
					if ( g_strCurConnectedAddress.isEmpty() )
					{
						addLog("requestDataTimer-蓝牙未连接，无法操作...");					
					}
					else
					{
						if ( m_bRequestDataGetSnoreCount )
						{ 
							executeCommand(BluetoothCommand.DATA_GET_SNORE_COUNT,true);
						}
						else
						{
							executeCommand(BluetoothCommand.DATA_GET_HEAD_COUNT,true);						
						}
						m_bRequestDataGetSnoreCount=!m_bRequestDataGetSnoreCount;					
					}
				}				
			}				
			m_bRequestDataRuning=false;
			requestDataHandler.postDelayed(this, lDelayMillis);
		}
	};
	
	public static void updateDevicetime(boolean bFastDo) {
		String strYYMMDDHHMM=getCurrentDateTimeYYMMDDHHMM();
		executeCommand(BluetoothCommand.SET_TIME + BluetoothCommand.DATA_SPLIT +strYYMMDDHHMM,bFastDo);
	}	

    public static final String AUTO_CONNECT_DEVICE_ADDRESS = "AutoConnectDeviceAddress";
    private static String getAutoConnectDeviceAddress() {    
    	String strAddress=getSettingString(AUTO_CONNECT_DEVICE_ADDRESS);
        addLog( "getAutoConnectDeviceAddress=" + strAddress);
    	return strAddress;
    }
    private static void setAutoConnectDeviceAddress(String strAddress) {    
    	setSettingString(AUTO_CONNECT_DEVICE_ADDRESS,strAddress);
        addLog( "setAutoConnectDeviceAddress=" + strAddress);
    }
    public static void checkAutoConnect(String strAddress) {    
        addLog( "checkAutoConnect=" + strAddress);
        String strAutoConnectDeviceAddress=getAutoConnectDeviceAddress();
        if ( strAutoConnectDeviceAddress.isEmpty() )
        {
        	return;
        }
        if ( strAddress.isEmpty() )
        {
            if ( g_strCurConnectedAddress.isEmpty() )
            {
            	connect(strAutoConnectDeviceAddress);
            }        	        	
        }
        else
        {
            if ( g_strCurConnectedAddress.isEmpty() && strAutoConnectDeviceAddress.equalsIgnoreCase(strAddress) )
            {
            	connect(strAddress);
            }        	
        }
    }
    private static IntentFilter makeIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleService.ACTION_BLUETOOTH_DEVICE);
        intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BleService.ACTION_SCAN_FINISHED);
        return intentFilter;
    }

    private static ProgressDialog progressDialog=null;

    private static void showDialog(String message) {
    	/*/
        progressDialog = new ProgressDialog(g_theContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(message);
        progressDialog.show();
        //*/
    }

    private static void dismissDialog() {
        if (progressDialog == null) return;
        progressDialog.dismiss();
        progressDialog = null;
    }
    
    public static void scanLeDeviceStart(long lTIMEOUT_SCAN_PERIOD) {
    	mBleService.scanLeDevice(true,lTIMEOUT_SCAN_PERIOD);	
    }
    
	public static void scanLeDeviceStop() {
		if (mBleService.isScanning()) {
			mBleService.scanLeDevice(false,BleService.TIMEOUT_SCAN_PERIOD);
			return;
		}	
    }
	public static String getSettingString(String strKey) 
	{
		if ( g_theContext==null )
		{
			return "";
		}
		return getSettingString(g_theContext,strKey);		
	}
	public static String getSettingString(Context theContext,String strKey) 
	{
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(theContext);
		return settings.getString(strKey, "");
	}	
	public static boolean setSettingString(String strKey,String strValue) 
	{
		if ( g_theContext==null )
		{
			return false;
		}
		return setSettingString(g_theContext,strKey,strValue);		
	}
	public static boolean setSettingString(Context theContext,String strKey,String strValue) 
	{
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(theContext);
		SharedPreferences.Editor editor;
		editor = settings.edit();
		editor.putString(strKey, strValue);
		if (editor.commit()) {
			//showMSG(false, "setSettingString true:" + strKey + "=" + strValue);
			return true;
		}
		//showMSG(false, "setSettingString false:" + strKey + "=" + strValue);
		return false;
	}
	
	public static final String COMMAND_BEGIN = "#";
	public static final String COMMAND_END = "$";
	public static boolean executeCommand(String command,boolean bFastDo)
	{
		String strCommand = COMMAND_BEGIN + command
				+ COMMAND_END;
		return executeCommand(strCommand);
	}
	public static boolean executeCommand(String strCommand)
	{
		addLog("executeCommand = " + strCommand);
		byte[] b = strCommand.getBytes();
		boolean result = wirteData(b);
		if (!result) {
			return false;
		}
		return true;		
	}
	private static boolean isWriteable(){
		if ( g_strCurConnectedAddress.isEmpty() )
		{
			addLog("isWriteable-false-g_strCurConnectedAddress=null");
			return false;
		}
		if ( mCurrentBluetoothGatt==null )
		{
			addLog("isWriteable-false-mCurrentBluetoothGatt==null");
			return false;
		}
		if ( mWriteChar==null )
		{
			addLog("isWriteable-false-mWriteChar==null");
			return false;
		}
		return mCurrentBluetoothGatt != null &&
				mWriteChar != null;
	}
	
	private static BluetoothGatt mCurrentBluetoothGatt=null;
	private static BluetoothGattCharacteristic mReadChar=null;
	private static BluetoothGattCharacteristic mWriteChar=null;
	public static boolean wirteData(byte [] data) {
		if(isWriteable()){
			return mBleService.writeCharacteristic(mWriteChar, data);
		}
		return false;
	}
	
	// private static final String YYMMDDHHMM = "yyMMddhhmm";//12小时制
	private static final String YYMMDDHHMM = "yyMMddHHmm";// 24小时制
	public static String getCurrentDateTimeYYMMDDHHMM() {
		Calendar theCalendar = java.util.Calendar.getInstance();
		SimpleDateFormat theSimpleDateFormat = new java.text.SimpleDateFormat(
				YYMMDDHHMM);
		String strCurrentDate = theSimpleDateFormat.format(theCalendar
				.getTime());
		return strCurrentDate;
	}
}