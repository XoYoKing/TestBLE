
package com.wt.testble;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ClassName: BaseActivity <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Date:2015-10-13
 * 
 * @author wenguan.chen
 * @version 0.1
 * @since MT 1.0
 */
public abstract class BaseActivity extends Activity {

	private String TAG = "BaseActivity";
	
    private ImageView mBackImgVi;
    protected TextView mTitleBarTitleTxt;
    public TextView mTitleBarRightTxt;
    public Handler mHandler = new Handler();
    private Toast mToast;

    // loading dialog
    private Dialog mLoadingDialog;
    private View mLoadingDialogView;
    private TextView mLoadingDialogTitleTxt;
    protected int m_iActivityOpenType = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate....");
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        if (hasTitleBar()) {
            mBackImgVi = (ImageView)findViewById(R.id.view_titlebar_imgViBack);
            mTitleBarTitleTxt = (TextView)findViewById(R.id.view_titlebar_txtTitle);
            mTitleBarRightTxt = (TextView)findViewById(R.id.view_titlebar_txtRightTitle);
            mBackImgVi.setOnClickListener(mBackClickListener);
        }
        initView();
    }

    private OnClickListener mBackClickListener = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            finishActivity();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume....");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy....");
    }

    @Override
    protected void onPause() {
    	super.onPause();
    	Log.d(TAG, "onPause....");
    }
    
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void finishActivity() {
        this.finish();
    }

    /**
     * setTitleBarTitle: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pString String
     * @since MT 1.0
     */
    public void setTitleBarTitle(String pString) {
        if (mTitleBarTitleTxt != null) {
            mTitleBarTitleTxt.setText(pString);
        }
    }

    /**
     * setTitleBarTitle: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pStrId int
     * @since MT 1.0
     */
    public void setTitleBarTitle(int pStrId) {
        if (mTitleBarTitleTxt != null) {
            mTitleBarTitleTxt.setText(pStrId);
        }
    }

    public abstract boolean hasTitleBar();

    public abstract boolean needBackPressedFadeAnim();

    public abstract void initView();

    /**
     * showToast: TODO<br/>
     * 
     * @author qingshan.wu
     * @param text
     * @since MT 1.0
     */
    public void showToast(String text) {
        mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        mToast.show();
    }

    /**
     * showToast: TODO<br/>
     * 
     * @author wenguan.chen
     * @param pText
     * @since MT 1.0
     */
    public void showToast(int pText) {
        mToast = Toast.makeText(this, pText, Toast.LENGTH_SHORT);
        mToast.show();
    }

    /**
     * initLoadingView: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    private void initLoadingView() {
    }

    /**
     * showDialog: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    public void showLoadingDialog(String pTitle) {
        initLoadingView();// for (BUGID/19759)
        mLoadingDialogTitleTxt.setText(pTitle);
        loadingDialogShow();
    }

    /**
     * showDialog: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    public void showLoadingDialog(int pTitle) {
        initLoadingView();// for (BUGID/19759)
        mLoadingDialogTitleTxt.setText(pTitle);
        loadingDialogShow();
    }

    /**
     * loadingDialogShow: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    private void loadingDialogShow() {
    }

    /**
     * hideDialog: TODO<br/>
     * 
     * @author wenguan.chen
     * @since MT 1.0
     */
    public void hideLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()
                && BaseActivity.this != null
                && !BaseActivity.this.isFinishing()) {
            mLoadingDialog.dismiss();
        }
    }
}
