package leoemo.com.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView textview,tvdevice,AcceptTV;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice device;
    public String acc_data;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            AcceptTV.setText(msg.obj.toString());
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("蓝牙传输小程序");
        textview = (TextView) findViewById(R.id.textView13);
        tvdevice = (TextView) findViewById(R.id.textView15);
        AcceptTV = (TextView) findViewById(R.id.textView14);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> paired = mBluetoothAdapter.getBondedDevices();
        if(paired.size()>0){
            for(BluetoothDevice device1:paired){
                tvdevice.append("已配对设备:" + device1.getName()+":"+device1.getAddress()+"\n");
                device = mBluetoothAdapter.getRemoteDevice(device1.getAddress().toString());
            }
        }
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_UI);
        new AcceptThread().start();
    }
    public void beginSend(View v){
        new ConnectThread(device).start();
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                String acceleromter = "加速度传感器数据：\n"+"X: "+event.values[0]+"\n"+"Y: "+event.values[1]+"\n"+"Z: "+event.values[2];
                acc_data = acceleromter;
                textview.setText(acceleromter);
                break;
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    //接受线程
    private class AcceptThread extends Thread{
        private BluetoothServerSocket serverSocket;
        private BluetoothSocket socket;
        private InputStream inputStream;
        {
            try{
                serverSocket = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("MyBluetooth", UUID.fromString("d7afed6b-43c9-4a36-b63b-d2966aa23a91"));
            }catch (IOException e){
            }
        }
        public void run(){
            try{
                socket = serverSocket.accept();
                inputStream = socket.getInputStream();
                while (true){
                    byte[] buffer = new byte[100];
                    int count = inputStream.read(buffer);
                    Message message = new Message();
                    message.obj = new String(buffer,0,count,"utf-8");
                    handler.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //发送线程
    private class ConnectThread extends Thread {
        public InputStream inputStream ;
        public OutputStream outputStream ;
        private BluetoothSocket mmSocket;
        public ConnectThread(BluetoothDevice device) {
            try {
                mmSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("d7afed6b-43c9-4a36-b63b-d2966aa23a91"));
            } catch (IOException e) { }
        }
        public void run() {
            mBluetoothAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
                inputStream = mmSocket.getInputStream();
                outputStream = mmSocket.getOutputStream();
                while (true){
                    outputStream.write(acc_data.getBytes("utf-8"));
                    int mount = acc_data.length();
                    Log.d("Demo", String.valueOf(mount));
                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }
        }
    }
}
