package com.healthguard.app;

import java.util.Timer;
import java.util.TimerTask;

import com.healthguard.app.utils.AppConsts;

import umich.framjack.core.FramingEngine;
import umich.framjack.core.OnByteSentListener;
import umich.framjack.core.OnBytesAvailableListener;
import umich.framjack.core.SerialDecoder;
import umich.framjack.core.FramingEngine.IncomingPacketListener;
import umich.framjack.core.FramingEngine.OutgoingByteListener;
import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HealthManagerActivity extends Activity{
	
	public final static String TAG = "HealthManager";
	
	private SerialDecoder _serialDecoder;
	private FramingEngine _framer;
	
	private boolean _nextFlag = false;
	
	private int _pendingTransmitBytes=1;
	private byte[] _sendByteBuff;
	private short[] _RxAcShortBuff;
	private short[] _RxDcShortBuff;
	private short systolicValue;
	private short diastolicValue;
	
	private short[] _RxWeightShortBuff;
	private short[] _RxTempShortBuff;
	private float refVol = (float) 3.2;
	
	private short iHeartNum=0;
	
	private ImageButton bloodPressureFold;
	private ImageButton temperatureFold;
	private ImageButton weightFold;
	
	private ImageButton bloodPressureMeasureStart;
	private ImageButton bloodPressureSave;
	private ImageButton bodytemperatureSave;
	private ImageButton bodyweightSave;
	
	private ImageView titleBgBloodpressure;
	private ImageView titleBgTemperature;
	private ImageView titleBgWeight;
	
	private TextView bloodPressureHigh;
	private TextView bloodPressureLow;
	private TextView heartBeatNumber;
	private TextView bodyTemperature;
	private TextView bodyWeight;
	
	private LinearLayout bloodpressure_detail;
	private LinearLayout temperature_detail;
	private LinearLayout weight_detail;
	
	private short iHeartNumFirst;
	private short iHeartNumSecond;
	private boolean iStartHeartTimerFlag = true;	
	private short iHeartEdgeFlag=0;	
	
	
	
	private Timer mTimer = new Timer(); 
	private HeartTimerTask mHeartTimerTask;
	
	class HeartTimerTask extends TimerTask{
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			iHeartNumSecond = iHeartNum;
			Message msg = Message.obtain();
			msg.what = AppConsts.MSG_HEARTBEAT;
			msg.arg1 = (iHeartNumSecond - iHeartNumFirst)*2;
			mHandler.sendMessage(msg);
		}
	};
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what == AppConsts.MSG_PRESSSURE){
				bloodPressureHigh.setText(msg.arg1+"");
				bloodPressureLow.setText(msg.arg2+"");
			}
			else if(msg.what == AppConsts.MSG_HEARTBEAT){
				Log.i("", "heart beat number = " + msg.arg1);
				heartBeatNumber.setText(msg.arg1+"次/分钟");
				//timeTask.cancel();
			}
			else if(msg.what == AppConsts.MSG_TEMPERATURE){
				
				float vol = (float) 0.0;
				float Rvalue= (float) 0.0;
				vol = (float) ((((float)msg.arg1*2.5)/(float)4096) - (refVol/11));
				Rvalue = (110000*vol)/refVol;
				
				double temperatrue,temp;
				
				temp = (double) (((float)(0.00025316) * (Math.log(Rvalue/50000))) + (1/298.15));//0.00025316=1/3950
				temp = 1/temp;
				
				temperatrue = (float) (temp - 273.15);
				bodyTemperature.setText(temperatrue +"摄氏度");

			}
			else if(msg.what == AppConsts.MSG_WEIGHT){
				bodyWeight.setText(msg.arg1+"KG");

			}
			
		}
		
	};
	

	//测试一下是否收到
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.health);
		initView();
		initButtonClick();
        
		_framer = new FramingEngine();
		_serialDecoder = new SerialDecoder();
		
		_sendByteBuff = new byte[8];
		
		_RxAcShortBuff = new short[10];
		_RxDcShortBuff = new short[10];
		_RxWeightShortBuff = new short[10];
		_RxTempShortBuff = new short[10];

		
		_sendByteBuff[0]=0x01;
		_sendByteBuff[1]=0x7c;
		_sendByteBuff[2]=0x23;
		_sendByteBuff[3]=0x54;
		_sendByteBuff[4]=0x65;
		_sendByteBuff[5]=0x76;
		_sendByteBuff[6]=0x27;
		_sendByteBuff[7]=0x18;
		
		
		_serialDecoder.registerBytesAvailableListener(_bytesAvailableListener);
		_serialDecoder.registerByteSentListener(_byteSentListener);
		_framer.registerIncomingPacketListener(_incomingPacketListener);
		_framer.registerOutgoingByteListener(_outgoingByteListener);	

		
	}
	
	private void initView(){
		bloodPressureFold =  (ImageButton)findViewById(R.id.bloodpressure_arrow);
		temperatureFold = (ImageButton)findViewById(R.id.temperature_arrow);
		weightFold = (ImageButton)findViewById(R.id.weight_arrow);
		
		bloodPressureMeasureStart = (ImageButton)findViewById(R.id.start_blood_test);
		bloodPressureSave = (ImageButton)findViewById(R.id.save_blood_test_result);
		bodytemperatureSave = (ImageButton)findViewById(R.id.save_temperature_record);
		bodyweightSave = (ImageButton)findViewById(R.id.save_weight_record);
		
		titleBgBloodpressure = (ImageView)findViewById(R.id.title_bg_bloodpressure);
		titleBgTemperature = (ImageView)findViewById(R.id.title_bg_temperature);
		titleBgWeight = (ImageView)findViewById(R.id.title_bg_weight);
		
		bloodPressureHigh = (TextView)findViewById(R.id.high);
		bloodPressureLow = (TextView)findViewById(R.id.low);
		heartBeatNumber = (TextView)findViewById(R.id.heartnum);
		bodyTemperature = (TextView)findViewById(R.id.temperature);
		bodyWeight = (TextView)findViewById(R.id.bodyWeight);
		
		bloodpressure_detail = (LinearLayout)findViewById(R.id.bloodpressure_detail);
		temperature_detail = (LinearLayout)findViewById(R.id.temperature_detail);
		weight_detail = (LinearLayout)findViewById(R.id.weight_detail);
	}

	private void initButtonClick(){
		
		titleBgBloodpressure.setOnClickListener(bloodPressureClick);
		bloodPressureFold.setOnClickListener(bloodPressureClick);
		
		titleBgTemperature.setOnClickListener(temperatureClick);
		temperatureFold.setOnClickListener(temperatureClick);
		
		titleBgWeight.setOnClickListener(weightClick);
		weightFold.setOnClickListener(weightClick);
		
		bloodPressureSave.setOnClickListener(bloodPressureSaveValue);
		
	}
	
	private  OnClickListener bloodPressureClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			_sendByteBuff[0]=0x55;
			_sendByteBuff[1]=0x01;
			_sendByteBuff[2]=0x01;
			// TODO Auto-generated method stub
			if((bloodpressure_detail.getVisibility() ==  View.INVISIBLE)||(bloodpressure_detail.getVisibility() ==  View.GONE)){
				bloodpressure_detail.setVisibility(View.VISIBLE);
				bloodPressureFold.setImageResource(R.drawable.arrow_unfold);
			}
			else if((bloodpressure_detail.getVisibility() ==  View.VISIBLE)){
				bloodpressure_detail.setVisibility(View.GONE);
				bloodPressureFold.setImageResource(R.drawable.arrow_fold);
			}
		}
	};
	
	private  OnClickListener temperatureClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
				_sendByteBuff[0]=0x55;
				_sendByteBuff[1]=0x01;
				_sendByteBuff[2]=0x03;
			// TODO Auto-generated method stub
			if((temperature_detail.getVisibility() ==  View.INVISIBLE)||(temperature_detail.getVisibility() ==  View.GONE)){
				temperature_detail.setVisibility(View.VISIBLE);
				temperatureFold.setImageResource(R.drawable.arrow_unfold);
			}
			else if((temperature_detail.getVisibility() ==  View.VISIBLE)){
				temperature_detail.setVisibility(View.GONE);
				temperatureFold.setImageResource(R.drawable.arrow_fold);
			}
		}
		
	};
	
	private  OnClickListener weightClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
				_sendByteBuff[0]=0x55;
				_sendByteBuff[1]=0x01;
				_sendByteBuff[2]=0x02;
			// TODO Auto-generated method stub
			if((weight_detail.getVisibility() ==  View.INVISIBLE)||(weight_detail.getVisibility() ==  View.GONE)){
				weight_detail.setVisibility(View.VISIBLE);
				weightFold.setImageResource(R.drawable.arrow_unfold);
			}
			else if((weight_detail.getVisibility() ==  View.VISIBLE)){
				weight_detail.setVisibility(View.GONE);
				weightFold.setImageResource(R.drawable.arrow_fold);
			}
		}
		
	};
	
	private OnClickListener bloodPressureSaveValue = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Uri uri = AppConsts.BLOODPRESSURE_URI;
			ContentValues value = new ContentValues();
			value.put("_id", AppConsts.getCurrentTime());
			value.put(AppConsts.Value_Pressure_high, 120);
			value.put(AppConsts.Value_Pressure_low, 80);
			value.put(AppConsts.Value_HeartBeat, 75);
			getContentResolver().insert(uri, value);
		}
	};
	
	
	
	///////////////////////////////////////////////
	// Listeners
	///////////////////////////////////////////////	
	private OutgoingByteListener _outgoingByteListener = new OutgoingByteListener() {
		public void OutgoingByteTransmit(int[] outgoingRaw) {
			synchronized (HealthManagerActivity.this) {
				_pendingTransmitBytes += outgoingRaw.length;
			}
			
			for (int i = 0; i < outgoingRaw.length; i++) {
				_serialDecoder.sendByte(outgoingRaw[i]);
			}
		}
	};
	
	private OnBytesAvailableListener _bytesAvailableListener = new OnBytesAvailableListener() {
		public void onBytesAvailable(int count) {
//			Log.i(TAG, "===>onBytesAvailable");
			while(count > 0) {
				int byteVal = _serialDecoder.readByte();
				//System.out.println("Received: " + byteVal);
				_framer.receiveByte(byteVal);
				count--;
			}
		}
	};
	
	private OnByteSentListener _byteSentListener = new OnByteSentListener() {
		public void onByteSent() {
			synchronized (HealthManagerActivity.this) {
				_pendingTransmitBytes--;
				if (_pendingTransmitBytes == 0) {
					
					for (int i = 0; i < _sendByteBuff.length; i++) {
						_framer.transmitByte(_sendByteBuff[i]);
					}
					_framer.transmitEnd();
				}
			}	
		}
	};
	
	private IncomingPacketListener _incomingPacketListener = new IncomingPacketListener() {
		public void IncomingPacketReceive(int[] packet) {
			short sAvg=0;
			for (int i = 0; i < packet.length; i++) {
				//System.out.print(Integer.toHexString(packet[i])+ " ");
			}
			//blood press
			if(packet[0] == 0xfe)
			{
				if((packet[1]==0xaa)&&(packet[2]==0xbb)&&(packet[3]==0xcc)&&(packet[4]==0xdd))
				{
					systolicValue = (short)((packet[6]<<8) + packet[5]);//systolic
					diastolicValue = (short)((packet[8]<<8) + packet[7]);//diastolic
					systolicValue= (short) ((float) (systolicValue)/(float) (30));
					diastolicValue= (short) ((float) (diastolicValue)/(float) (30));
					
					Message msg = Message.obtain();
					msg.what = AppConsts.MSG_PRESSSURE;
					msg.arg1 = systolicValue;
					msg.arg2 = diastolicValue;
					mHandler.sendMessage(msg);
					
					iStartHeartTimerFlag = true;
					//System.out.println(Integer.toString(diastolicValue));
				}
				else
				{
					systolicValue = (short)((packet[2]<<8) + packet[1]);//systolic
					//System.out.println("<------->"+Integer.toString(systolicValue));
					systolicValue= (short) ((float) (systolicValue)/(float) (30));
					
					Message msg = Message.obtain();
					msg.what = AppConsts.MSG_PRESSSURE;
					msg.arg1 = systolicValue;
					msg.arg2 = 0;
					mHandler.sendMessage(msg);
				}
				
				
				_RxDcShortBuff[0] = (short)((packet[2]<<8) + packet[1]);//dc
				_RxAcShortBuff[0] = (short)((packet[4]<<8) + packet[3]);//ac
				
				_RxAcShortBuff[1] = (short)((packet[6]<<8) + packet[5]);//ac
				_RxAcShortBuff[2] = (short)((packet[8]<<8) + packet[7]);//ac
				
				System.out.println();
				System.out.println(Integer.toString(_RxAcShortBuff[0]));
				System.out.println(Integer.toString(_RxAcShortBuff[1]));
				System.out.println(Integer.toString(_RxAcShortBuff[2]));
				
	
				if(_RxDcShortBuff[0]>1500)//1500 for debug will be replaced
				{
					sAvg = (short) ((_RxAcShortBuff[0]+_RxAcShortBuff[1]+_RxAcShortBuff[2])/3);
					if(sAvg>900)
						iHeartEdgeFlag = 1;
					if((iHeartEdgeFlag == 1)&&(sAvg<900))
					{
						iHeartEdgeFlag = 0;
						iHeartNum++;
						if(iStartHeartTimerFlag){//
							if(mHeartTimerTask != null)
							{
								mHeartTimerTask.cancel();
							}
							
							mHeartTimerTask = new HeartTimerTask();
							
							iStartHeartTimerFlag = false;
							iHeartNumFirst = iHeartNum;
							mTimer.schedule(mHeartTimerTask, 30*1000); 
						}
					}
				}
			}
			//weight
			else if(packet[0] == 0xfd)
			{
				_RxWeightShortBuff[0] = (short)((packet[2]<<8) + packet[1]);//dc
				_RxWeightShortBuff[1] = (short)((packet[4]<<8) + packet[3]);//ac
				
				_RxWeightShortBuff[2] = (short)((packet[6]<<8) + packet[5]);//ac
				_RxWeightShortBuff[3] = (short)((packet[8]<<8) + packet[7]);//ac
				
				Message msg = Message.obtain();
				msg.what = AppConsts.MSG_WEIGHT;
				msg.arg1 = (_RxWeightShortBuff[0]+_RxWeightShortBuff[1]+_RxWeightShortBuff[2]+_RxWeightShortBuff[3])/4;
				msg.arg2 = 0;
				mHandler.sendMessage(msg);
			}
			//temperature
			else if(packet[0] == 0xfc)
			{
				_RxTempShortBuff[0] = (short)((packet[2]<<8) + packet[1]);//dc
				_RxTempShortBuff[1] = (short)((packet[4]<<8) + packet[3]);//ac
				
				_RxTempShortBuff[2] = (short)((packet[6]<<8) + packet[5]);//ac
				_RxTempShortBuff[3] = (short)((packet[8]<<8) + packet[7]);//ac
				
				Message msg = Message.obtain();
				msg.what = AppConsts.MSG_TEMPERATURE;
				msg.arg1 = (_RxTempShortBuff[0]+_RxTempShortBuff[1]+_RxTempShortBuff[2]+_RxTempShortBuff[3])/4;
				msg.arg2 = 0;
				mHandler.sendMessage(msg);
			}

		}
	};
	
	   @Override
	    public void onPause() {
//	    	_serialDecoder.stop();
	    	super.onPause();
	    }
	    
	    @Override
	    public void onResume() {
	    	if(!_serialDecoder.isStart){
	    		_serialDecoder.start();
	    	}
	    	super.onResume();
	    }
	    
		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			_serialDecoder.stop();
			super.onDestroy();
		}
	
}
