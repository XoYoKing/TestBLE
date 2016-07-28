package com.wt.testble;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;
/**
 * 2015/12/06 22:49
 * @author ITjianghuxiaoxiong
 * http://blog.csdn.net/itjianghuxiaoxiong
 */
//http://blog.csdn.net/itjianghuxiaoxiong/article/details/50207009

@SuppressLint("DrawAllocation")
public class RadarImageView extends ImageView {
	private int w, h;// 获取控件宽高
	private Matrix matrix;
	private int degrees;
	private Handler mHandler = new Handler();
	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			degrees++;
			matrix.postRotate(degrees, iOffsetX+(left+right) / 2, iOffsetY+(top+bottom) / 2);
			RadarImageView.this.invalidate();// 重绘
			mHandler.postDelayed(mRunnable, 10);
		}
	};

	public RadarImageView(Context context) {
		this(context, null);
	}

	public RadarImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RadarImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}


	/**
	 * 初始化
	 */
	private void init() {
		if(!isInEditMode()){
			//setBackgroundResource(R.drawable.radar_bg);
			//setBackgroundResource(R.drawable.search);
		}
		matrix = new Matrix();
		mHandler.postDelayed(mRunnable,500);
	}

	private static int left = 0;
	private static int top = 0;
	private static int right = 0;
	private static int bottom = 0;
	private static int iOffsetX = 0;
	private static int iOffsetY = 0;
	protected void sizeCalc() 
	{
		left = this.getLeft();
		bottom = this.getBottom();
		right = this.getRight();
		top = this.getTop();
		final int[] location = new int[2];  
		getLocationOnScreen(location); 
		iOffsetX=location[0];
		iOffsetY=location[1];
	}
	@Override
	protected void onDraw(Canvas canvas) {
		sizeCalc();
		
		canvas.setMatrix(matrix);
		canvas.translate(iOffsetX,iOffsetY);
		super.onDraw(canvas);
		matrix.reset();
	}
}