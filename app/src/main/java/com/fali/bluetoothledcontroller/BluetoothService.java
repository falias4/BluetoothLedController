package com.fali.bluetoothledcontroller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.fali.bluetoothledcontroller.SerialMapping.ValidationState;

public class BluetoothService {
	// Debugging
    private static final String TAG = "BluetoothService";
    private static final boolean D = true;
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    	
    private final BluetoothAdapter mAdapter;
    private BluetoothDevice device;
	private BluetoothSocket socket;
	private OutputStream outStream;
	private InputStream inputStream;
	
	private int mState;

    // Constants that indicate the current connection state
    public static final int STATE_CONNECTED = 0;       // we're doing nothing
    public static final int STATE_DISCONNECTED = 1;       // we're doing nothing

	public BluetoothService(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_DISCONNECTED;
    }
	
	public synchronized void connectTo(String deviceName)
	{
		if(mAdapter != null)
		{
			device = findBondedDevice(deviceName); 
			
			if(device != null)
			{
				try
				{
					socket = device.createInsecureRfcommSocketToServiceRecord(myUUID);
					socket.connect();
					outStream = socket.getOutputStream();
					mState = STATE_CONNECTED;
				} 
				catch(IOException e)
				{
					Log.e("BTFali", "socketError", e);
				}
			}
		}
	}
	
	private synchronized BluetoothDevice findBondedDevice(String deviceName)
	{
		Set<BluetoothDevice> bonded = mAdapter.getBondedDevices();
		for (BluetoothDevice device : bonded) {
			if(D) Log.d("BTFali", device.getName());
			
			if(device.getName().equals(deviceName))
			{
				return mAdapter.getRemoteDevice(device.getAddress());	
			}
	    }
		
		return null;
	}
	
	public synchronized void write(String str)
	{
		write(str.getBytes());
	}
	
	public synchronized void write(Integer value)
	{
		write(new byte[]{value.byteValue()});
	}
	
	public synchronized void write(byte[] out) {
		if(mState == STATE_CONNECTED)
		{
			try
			{
				byte[] lineEnding = "\n".getBytes();
				byte[] outWithLineEnding = new byte[out.length + lineEnding.length];
				System.arraycopy(out, 0, outWithLineEnding, 0, out.length);
				System.arraycopy(lineEnding, 0, outWithLineEnding, out.length, lineEnding.length);

				outStream.write(outWithLineEnding);
			}
			catch(IOException e)
			{
				Log.e(TAG, "Writing failed", e);
			}
		}
    }
	
	public synchronized int readInt() {
		int result = 0;
		int treshHold = 0;
		try {
        while (socket.getInputStream().available()==0 && treshHold<3000)
        {
            Thread.sleep(1);            
            treshHold++;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.reset();

        while (socket.getInputStream().available() > 0)
        {            
            baos.write(socket.getInputStream().read());
            Thread.sleep(1);   
        }

        
            result = Integer.parseInt(baos.toString());
        }
		catch(NumberFormatException nfe) { result = 0; }
		catch (IOException e) { result = 0; }
		catch (InterruptedException e) { result = 0; }
        return result;
	}
}
