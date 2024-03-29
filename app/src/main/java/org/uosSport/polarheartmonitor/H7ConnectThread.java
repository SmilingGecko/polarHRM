package org.uosSport.polarheartmonitor;

import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.util.Log;


@SuppressLint("NewApi")
public class H7ConnectThread  extends Thread{
	
	MainActivity ac;	
	private BluetoothGatt gat;
	private final String HRUUID = "0000180D-0000-1000-8000-00805F9B34FB";
	static BluetoothGattDescriptor descriptor;
	static BluetoothGattCharacteristic cc;
	
	public H7ConnectThread(BluetoothDevice device, MainActivity ac) {
		Log.i("H7ConnectThread", "Starting H7 reader BTLE");
		this.ac=ac;
		gat = device.connectGatt(ac, false, btleGattCallback); // Connect to the device and store the server (gatt)
	}

	
	/** Will cancel an in-progress connection, and close the socket */
	public void cancel() {
		gat.setCharacteristicNotification(cc,false);
		descriptor.setValue( BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
	    gat.writeDescriptor(descriptor);
		gat.disconnect();
		gat.close();
		Log.i("H7ConnectThread", "Closing HRsensor");
	}

	
	//Callback from bluetooth
	private final BluetoothGattCallback btleGattCallback = new BluetoothGattCallback() {
		 
		//Called every time the sensor sends data
		@Override
	    public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
	    	byte[] data = characteristic.getValue();
	    	int bmp = data[1] & 0xFF; //
	    	DataHandler.getInstance().cleanInput(bmp);
			Log.v("H7ConnectThread", "Data received from HR "+bmp);
	    }
	 
		//Called on successful connection
	    @Override
	    public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) { 
	    	if (newState ==  BluetoothGatt.STATE_DISCONNECTED)
	    	{
				Log.e("H7ConnectThread", "device Disconnected");
				ac.connectionError();
	    	}
	    	else{
				gatt.discoverServices();
				Log.d("H7ConnectThread", "Connected and discovering services");
	    	}
	    }
	 
	    //Called when services are discovered.
	    @Override
	    public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
	    	BluetoothGattService service = gatt.getService(UUID.fromString(HRUUID)); // Return the HR service
			List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics(); //Get BPM VAL
			for (BluetoothGattCharacteristic cc : characteristics)
				{
					for (BluetoothGattDescriptor descriptor : cc.getDescriptors()) {

						H7ConnectThread.descriptor=descriptor;
						H7ConnectThread.cc=cc;
												
						gatt.setCharacteristicNotification(cc,true);//Register to updates
						descriptor.setValue( BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
					    gatt.writeDescriptor(descriptor);
						Log.d("H7ConnectThread", "Connected and regisering to info");
					}
				}
	    }
	};
}