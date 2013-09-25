package com.healthguard.app;

import java.util.Timer;
import java.util.TimerTask;

import com.healthguard.app.HealthManagerActivity.HeartTimerTask;
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
import android.widget.TextView;

public class BloodPressureActivity extends Activity{
	
	private final static String TAG = "BloodPressureActivity";
	
	private SerialDecoder serialDecoder;
	private FramingEngine framer;
	
	private int _pendingTransmitBytes=1;
	private byte[] _sendByteBuff;
	private short[] _RxAcShortBuff;
	private short[] _RxDcShortBuff;
	private short systolicValue;
	private short diastolicValue;
	
	private short[] _RxWeightShortBuff;
	private short[] _RxTempShortBuff;
	
	private short iHeartNumFirst;
	private short iHeartNumSecond;
	private boolean iStartHeartTimerFlag = true;	
	private short iHeartEdgeFlag=0;
	
	
	//读数
	private TextView bloodPressureHigh;
	private TextView bloodPressureLow;
	private TextView heartBeatNumber;
	
	//按键
	private ImageButton bloodPressureMeasureStart;
	private ImageButton bloodPressureSave;
	
	private Timer mTimer = new Timer(); 
	private HeartTimerTask mHeartTimerTask;
	
	private short iHeartNum=0;
	
	
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
			
		}
		
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blood_pressure);
		
		bloodPressureHigh = (TextView)findViewById(R.id.high);
		bloodPressureLow = (TextView)findViewById(R.id.low);
		heartBeatNumber = (TextView)findViewById(R.id.heartnum);
		
		bloodPressureMeasureStart = (ImageButton)findViewById(R.id.start_blood_test);
		bloodPressureSave = (ImageButton)findViewById(R.id.save_blood_test_result);
		
		initButtonClick();
		
		framer = new FramingEngine();
		serialDecoder = new SerialDecoder();
		
		_sendByteBuff = new byte[8];
		
		_sendByteBuff[0]=0x01;
		_sendByteBuff[1]=0x7c;
		_sendByteBuff[2]=0x23;
		_sendByteBuff[3]=0x54;
		_sendByteBuff[4]=0x65;
		_sendByteBuff[5]=0x76;
		_sendByteBuff[6]=0x27;
		_sendByteBuff[7]=0x18;
		
		serialDecoder.registerBytesAvailableListener(bytesAvailableListener);
		serialDecoder.registerByteSentListener(_byteSentListener);
		framer.registerIncomingPacketListener(_incomingPacketListener);
		framer.registerOutgoingByteListener(_outgoingByteListener);	
		
	}
	
	
	private void initButtonClick(){
		bloodPressureMeasureStart.setOnClickListener(bloodPressureMeasureStartListener);
		bloodPressureSave.setOnClickListener(bloodPressureSaveValueListener);
	}
	
	private OnClickListener bloodPressureMeasureStartListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private OnClickListener bloodPressureSaveValueListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Uri uri = AppConsts.BLOODPRESSURE_URI;
			System.out.println("<----uri  is ---->"+uri);
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
			synchronized (BloodPressureActivity.this) {
				_pendingTransmitBytes += outgoingRaw.length;
			}
			
			for (int i = 0; i < outgoingRaw.length; i++) {
				serialDecoder.sendByte(outgoingRaw[i]);
			}
		}
	};
	
	
	private OnByteSentListener _byteSentListener = new OnByteSentListener() {
		public void onByteSent() {
			synchronized (BloodPressureActivity.this) {
				_pendingTransmitBytes--;
				if (_pendingTransmitBytes == 0) {
					
					for (int i = 0; i < _sendByteBuff.length; i++) {
						framer.transmitByte(_sendByteBuff[i]);
					}
					framer.transmitEnd();
				}
			}	
		}
	};
	
	private OnBytesAvailableListener bytesAvailableListener = new OnBytesAvailableListener() {
		public void onBytesAvailable(int count) {
//			Log.i(TAG, "===>onBytesAvailable");
			while(count > 0) {
				int byteVal = serialDecoder.readByte();
				//System.out.println("Received: " + byteVal);
				framer.receiveByte(byteVal);
				count--;
			}
		}
	};
	
	private OnByteSentListener byteSentListener = new OnByteSentListener() {
		public void onByteSent() {
			synchronized (BloodPressureActivity.this) {
				_pendingTransmitBytes--;
				if (_pendingTransmitBytes == 0) {
					
					for (int i = 0; i < _sendByteBuff.length; i++) {
						framer.transmitByte(_sendByteBuff[i]);
					}
					framer.transmitEnd();
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
	    	if(!serialDecoder.isStart){
	    		serialDecoder.start();
	    	}
	    	super.onResume();
	    }
	    
		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			serialDecoder.stop();
			super.onDestroy();
		}
	
}
