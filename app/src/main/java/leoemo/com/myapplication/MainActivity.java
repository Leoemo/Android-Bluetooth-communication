package leoemo.com.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private TextView textview,tvdevice,AcceptTV,introduce;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothServerSocket serverSocket;//发送用
    private BluetoothSocket mmSocket;//接受用
    private BluetoothDevice device;
    public String acc_data;
    private ListView listView;
    private String introDuctText ="操作简介如下：\n\n" +
            "Step1.两部手机均进入当前界面\n\n" +
            "Step2.需要向远程手机发送数据时，在本机点击远程设备地址\n\n" +
            "Step3.等待系统提示对方已经同意连接，再在本地点击发送\n\n" +
            "Final.远程手机开始实时显示本地手机的数据(可双向数据同时传送，方法步骤同上)\n\n";
    ArrayList<String> arrayList = new ArrayList<String>();
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
        listView = (ListView) findViewById(R.id.MyListView);
        introduce = (TextView) findViewById(R.id.introduce);
        introduce.setText(introDuctText);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> paired = mBluetoothAdapter.getBondedDevices();
        if(paired.size()>0){
            for(BluetoothDevice device1:paired){
                arrayList.add(device1.getName()+ ":" + device1.getAddress());
            }
        }
        tvdevice.setText("点击蓝牙设备发起连接请求(如下)");
        final ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = (String) arrayAdapter.getItem(position);
                String address = s.substring(s.indexOf(":")+1).trim();
                Toast.makeText(MainActivity.this,"正在向"+address+"发起连接",Toast.LENGTH_SHORT).show();
                device = mBluetoothAdapter.getRemoteDevice(address);
                Toast.makeText(MainActivity.this,"对方已接受连接，传送数据吧！",Toast.LENGTH_SHORT).show();
            }
        });
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
                serverSocket.close();//关闭无用的server
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
        public OutputStream outputStream ;
        public ConnectThread(BluetoothDevice device) {
            try {
                mmSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("d7afed6b-43c9-4a36-b63b-d2966aa23a91"));
            } catch (IOException e) { }
        }
        public void run() {
            mBluetoothAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
                outputStream = mmSocket.getOutputStream();
                while (true){
                    outputStream.write(acc_data.getBytes("utf-8"));
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mmSocket != null) {
            try {
                mmSocket.close();//退出当前activity时释放
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
