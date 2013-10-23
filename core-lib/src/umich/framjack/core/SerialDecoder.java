/**
 *  This file is part of hijack-infinity.
 *
 *  hijack-infinity is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  hijack-infinity is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with hijack-infinity.  If not, see <http://www.gnu.org/licenses/>.
 */

package umich.framjack.core;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class SerialDecoder {
	
	public static final String TAG = "renrui";
	private AudioReceiver _audioReceiver;
	
	private enum TransmitState { IDLE, PENDING, DATA };
	private enum ReceiveState { IDLE, DATA, DATANEXT };
	
	// Receiver State
	private int _lastEdgeLength = 0;
	
	private int _currentEdgeLength = 0;
	private boolean _currentEdgeHigh = false;
	
	// FOR THE MSP430FR5969:
	//private int _threshold = 7;
	
	// FOR THE MSP430F1611:
	private int _threshold = 12;
	
	private int _deltaT = 0;
	
	private ReceiveState _rxState = ReceiveState.IDLE;
	
	private int _rxByte = 0;
	private int _rxBits = 0;
	
	// Transmit State
	private TransmitState _txState = TransmitState.IDLE;
	private int _txByte = 0;
	private int _txBytePosition = 0;
	private int _txBitPosition;

	private int _idleCycles = 20;
	private int _idleCycleCount = 0;
	
	// Byte Buffers
	private List<Integer> _incoming = new ArrayList<Integer>();
	private List<Integer> _outgoing = new ArrayList<Integer>();
	
	// Listeners for byte sent + byte received events
	private OnBytesAvailableListener _bytesAvailableListener = null;
	private OnByteSentListener _byteSentListener = null;
	
	public  boolean isStart = false;//renrui

	
	//////////////////////////////
	// Transmit State Machine
	/////////////////////////////
	private boolean transmitIdle() {
		return _txBitPosition == 1;
	}
	
	private boolean transmitPending() {
		if (_txBitPosition == 0) {
			_txState = TransmitState.DATA;
			return transmitData();
		} else {
			return true;
		}
	}
	
	private boolean transmitData() {
//		Log.i("renrui", "===>transmitData");
		boolean ret = ((_txByte >> _txBytePosition) & 0x1) == 1;
		
		if (_txBitPosition == 1) {
			_txBytePosition++;
			
			if (_txBytePosition == 10) {

				synchronized(this) {	
					_idleCycleCount = 0;
					_txState = TransmitState.IDLE;
					_txBytePosition = 0;
					notifyByteSent();
				}
			}
		}
		else {
			ret = !ret;
		}
		
		return ret;
	}
	
	/////////////////////////////
	// Receive State Machine
	/////////////////////////////
	private void receiveIdle() {
//		Log.i("renrui", "===>receiveIdle");
		if (_currentEdgeHigh == true &&
				isWithinThreshold(_currentEdgeLength,
						_lastEdgeLength * 2)) {
			if(_lastEdgeLength<30)
				_deltaT = 19;
			
			//_deltaT = _lastEdgeLength;
			
			_rxState = ReceiveState.DATA;
			
			_rxByte = 0;
			_rxBits = 1;
		}
	}
	
	private void receiveData() {
//		Log.i("renrui", "===>receiveData");
		//System.out.println("EL: " + _currentEdgeLength + " DT: " + _deltaT);
		if (isWithinThreshold(_currentEdgeLength, _deltaT)) {
			_rxState = ReceiveState.DATANEXT;
		}
		else if (isWithinThreshold(_currentEdgeLength, _deltaT * 2)) {
			if (((_rxByte >> (_rxBits - 1)) & 1) == 0) {
				_rxByte |= (1 << _rxBits);
			}
			_rxBits++;
			advanceReceiveDataState();
		}
		else {
			//System.out.println("EL: " + _currentEdgeLength + " DT: " + _deltaT);
			//System.out.println("Error.");
			_rxState = ReceiveState.IDLE;
		}
	}
	
	private void receiveDataNext() {
		if (isWithinThreshold(_currentEdgeLength, _deltaT)) {
			if (((_rxByte >> (_rxBits - 1)) & 1) == 1) {
				_rxByte |= (1 << _rxBits);
			}
			
			_rxBits++;
			advanceReceiveDataState();
		}
		else {
			//System.out.println("BEL: " + _currentEdgeLength + " DT: " + _deltaT);
			_rxState = ReceiveState.IDLE;
			//System.out.println("Error.");
		}
	}

	/////////////////////////////
	// Public Functions
	/////////////////////////////
	
	public SerialDecoder() {
//		Log.i(TAG, "===>SerialDecoder");
		_audioReceiver = new AudioReceiver();
		_audioReceiver.registerIncomingSink(_incomingSink);
		_audioReceiver.registerOutgoingSource(_outgoingSink);		
	}
	
	public void start() {
		isStart = true;
		_audioReceiver.startAudioIO();		
	}
	
	public void stop() {
		isStart = false;
		_audioReceiver.stopAudioIO();
	}
	
	public void sendByte(int val) {
		synchronized(this) {
			_outgoing.add(val);
		}
	}
	
	public int bytesAvailable() {
		synchronized(this) {
			return _incoming.size();
		}
	}
	
	public int readByte() {
		synchronized(this) {
			if (_incoming.size() == 0) {
				return -1;
			}
			int ret = _incoming.get(0);
			_incoming.remove(0);
			return ret;
		}
	}
	
	public void setFreq(int freq) {
		Log.i(TAG, "===>setFreq");
		_audioReceiver.setPowerFrequency(freq);
	}
	
	/////////////////////////////
	// Listener Functions
	/////////////////////////////
	// WARNING: These are not thread-safe. We are using a
	// guarantee by AudioInterface that events will occur
	// sequentially and originate from a single thread.
	
	public void registerBytesAvailableListener(OnBytesAvailableListener _listener) {
		_bytesAvailableListener = _listener;
	}
	
	public void registerByteSentListener(OnByteSentListener _listener) {
		_byteSentListener = _listener;
	}
	
	private void notifyBytesAvailable(int numberBytes) {
		if (_bytesAvailableListener != null) {
			_bytesAvailableListener.onBytesAvailable(numberBytes);
		}
	}
	
	private void notifyByteSent() {
		if (_byteSentListener != null) {
			_byteSentListener.onByteSent();
		}
	}
	
	/////////////////////////////
	// Helper Functions
	/////////////////////////////
	
	private boolean isWithinThreshold(int value, int desired) {
		return value < desired + _threshold && value > desired - _threshold;
	}
	
	private byte calcParity(int in) {
		byte parity = 0;
		for (int i = 0; i < 8; i++) {
			parity ^= ((in >> i) & 1);
		}	
		return parity;
	}
	
	private void advanceReceiveDataState() {
		if (_rxBits == 10) {
			if (calcParity(_rxByte >> 1) ==  (_rxByte >> 9)) {
				synchronized(this) {
					_incoming.add((_rxByte >> 1) & 0xFF);
					//if(((_rxByte >> 1) & 0xFF) != 0xab)
					//System.out.println("recerive data: " + ((_rxByte >> 1) & 0xFF));
					notifyBytesAvailable(_incoming.size());
					
					System.out.println("checksum ok." + "data:" +((_rxByte >> 1) & 0xFF));
				}
			}
			else
			{
				System.out.println("checksum failed.");
			}
			_rxState = ReceiveState.IDLE;
		}
		else {
			_rxState = ReceiveState.DATA;
		}
	}

	/////////////////////////////
	// AudioInterface Listeners
	////////////////////////////
	
	private IncomingSink _incomingSink = new IncomingSink() {
		public void handleNextBit(int transistionPeriod, boolean isHighToLow) {
			//System.out.println("Tran: " + transistionPeriod + " HL: " + isHighToLow);
			_currentEdgeLength = transistionPeriod;
			_currentEdgeHigh = isHighToLow;
			switch (_rxState) {
			case DATA:
				receiveData();
				break;
			case DATANEXT:
				receiveDataNext();
				break;
			case IDLE:
				receiveIdle();
				break;
			default:
				break;
			}
			_lastEdgeLength = _currentEdgeLength;
		}
	};
	
	private OutgoingSource _outgoingSink = new OutgoingSource() {
		public boolean getNextBit() {
			boolean ret = false;
			switch (_txState) {
			case DATA:
				ret = transmitData();
				break;
			case IDLE:
				if(_idleCycleCount < _idleCycles) {
					_idleCycleCount++;
				}
				
				if (_outgoing.size() == 0 && _idleCycleCount == _idleCycles) {
					notifyByteSent();
				}
				
				synchronized(SerialDecoder.this) {
					if (_outgoing.size() > 0 && _idleCycleCount == _idleCycles) {
					//if (_idleCycleCount == _idleCycles) {
						
						int byteToSend = _outgoing.get(0);
						_outgoing.remove(0);
						//int byteToSend = 0x7c;
						int parity = calcParity(byteToSend);
						_txByte = (byteToSend << 1) | (parity << 9);
						_txState = TransmitState.PENDING;
					}
				}
				
				ret = transmitIdle();
				break;
			case PENDING:
				ret = transmitPending();
				break;
			default:
				break;
			}
			_txBitPosition = _txBitPosition == 0 ? 1 : 0;
			
			return ret;
		}
	};
}
