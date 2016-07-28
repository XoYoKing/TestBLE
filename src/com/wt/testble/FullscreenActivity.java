package com.wt.testble;

import java.util.ArrayList;

import com.wt.testble.bluetooth.SingletonBLE;
import com.wt.testble.util.SystemUiHider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity implements OnClickListener {
    private void toActivity(Class<?> pActivity) {
        startActivity(new Intent(this, pActivity));
    }
    
    @Override
	public void onClick(View pView) {
		switch (pView.getId()) {
		case R.id.buttonGD1:
			//SingletonBLE.addLog("buttonGD1");
			SingletonBLE.executeCommand("GD1 01",true);
			break;
		case R.id.buttonGD2:
			//SingletonBLE.addLog("buttonGD2");
			SingletonBLE.executeCommand("GD2",true);
			break;
		case R.id.buttonGD3:
			//SingletonBLE.addLog("buttonGD3");
			SingletonBLE.executeCommand("GD3 01",true);
			break;
		case R.id.buttonGD4:
			//SingletonBLE.addLog("buttonGD4");
			SingletonBLE.executeCommand("GD4",true);
			break;
		case R.id.buttonTM1:
			//SingletonBLE.addLog("buttonTM1");
			SingletonBLE.executeCommand("TS",true);
			break;
		case R.id.buttonTM2:
			//SingletonBLE.addLog("buttonTM2");
			SingletonBLE.executeCommand("SM 1607060202",true);
			break;
		case R.id.buttonSA1:
			//SingletonBLE.addLog("buttonSA1");
			SingletonBLE.executeCommand("SA105",true);
			break;
		case R.id.buttonSA2:
			//SingletonBLE.addLog("buttonSA2");
			SingletonBLE.executeCommand("SA107",true);
			break;
		case R.id.buttonMyDevice:
			//SingletonBLE.addLog("buttonMyDevice");
        	toActivity(MyDeviceActivity.class);
			break;
		default:
			break;
		}
	}
    
    private TextView textViewLog;
    private ScrollView scrollViewLog;
    
    private ArrayList<String> m_asLogArrayList=new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        
        SingletonBLE.doBindService(this);
        initHandlerMe();
        
        findViewById(R.id.buttonGD1).setOnClickListener(this);         
        findViewById(R.id.buttonGD2).setOnClickListener(this);         
        findViewById(R.id.buttonGD3).setOnClickListener(this);         
        findViewById(R.id.buttonGD4).setOnClickListener(this);         
        findViewById(R.id.buttonTM1).setOnClickListener(this);         
        findViewById(R.id.buttonTM2).setOnClickListener(this);         
        findViewById(R.id.buttonSA1).setOnClickListener(this);         
        findViewById(R.id.buttonSA2).setOnClickListener(this);         
        findViewById(R.id.buttonMyDevice).setOnClickListener(this);         
        
        textViewLog=(TextView)findViewById(R.id.textViewLog);
        scrollViewLog=(ScrollView)findViewById(R.id.scrollViewLog);
    }
    
    //Debugging
    private static final String TAG = FullscreenActivity.class.getSimpleName();
    
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
					{
						String text=(String)msg.obj;
						int duration=msg.arg1;					
		                Toast.makeText(FullscreenActivity.this,text,duration).show();					
					}
					break;
				case SingletonBLE.MSG_BLE_LOG:
					{
						String text=(String)msg.obj;
						if ( text!=null )
						{
							m_asLogArrayList.add(text);
							while ( m_asLogArrayList.size()>1000 )
							{
								m_asLogArrayList.remove(0);
							}
							String strLog="";
							for ( String strTemp : m_asLogArrayList )
							{
								strLog+=strTemp+"\n";
							}
							textViewLog.setText(strLog);
							scrollViewLog.fullScroll(ScrollView.FOCUS_DOWN);//滚动到底部
						}						
					}
					break;
				}
			}
		};
		SingletonBLE.setHandlerOUT(handlerMe);
    }
  

    @Override
    protected void onStart() {
        super.onStart();
        initHandlerMe();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        initHandlerMe();
        SingletonBLE.checkAutoConnect("");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SingletonBLE.doUnBindService();
    }

    @Override
    public void onBackPressed() {
    	SingletonBLE.scanLeDeviceStop();
        super.onBackPressed();
    }


}
