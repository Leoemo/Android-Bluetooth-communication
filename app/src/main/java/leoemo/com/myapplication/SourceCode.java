package leoemo.com.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by 65993 on 2017/5/28.
 */

public class SourceCode  extends AppCompatActivity {
    private TextView tv1;
    private String github ="完整代码请查看我的主页:github.com/Leoemo";
    private String code = "package leoemo.com.myapplication;\n" +
            "import android.bluetooth.BluetoothAdapter;\n" +
            "import android.bluetooth.BluetoothDevice;\n" +
            "import android.bluetooth.BluetoothServerSocket;\n" +
            "import android.bluetooth.BluetoothSocket;\n" +
            "import android.content.Intent;\n" +
            "import android.hardware.Sensor;\n" +
            "import android.hardware.SensorEvent;\n" +
            "import android.hardware.SensorEventListener;\n" +
            "import android.hardware.SensorManager;\n" +
            "import android.os.Handler;\n" +
            "import android.os.Message;\n" +
            "import android.support.v7.app.AppCompatActivity;\n" +
            "import android.os.Bundle;\n" +
            "import android.view.View;\n" +
            "import android.widget.AdapterView;\n" +
            "import android.widget.ArrayAdapter;\n" +
            "import android.widget.ListView;\n" +
            "import android.widget.TextView;\n" +
            "import android.widget.Toast;\n" +
            "import java.io.IOException;\n" +
            "import java.io.InputStream;\n" +
            "import java.io.OutputStream;\n" +
            "import java.util.ArrayList;\n" +
            "import java.util.Set;\n" +
            "import java.util.UUID;\n" +
            "public class MainActivity extends AppCompatActivity implements SensorEventListener {\n" +
            "    private TextView textview,tvdevice,AcceptTV;\n" +
            "    private BluetoothAdapter mBluetoothAdapter;\n" +
            "    private BluetoothDevice device;\n" +
            "    public String acc_data;\n" +
            "    private ListView listView;\n" +
            "    ArrayList<String> arrayList = new ArrayList<String>();\n" +
            "    Handler handler = new Handler(){\n" +
            "        @Override\n" +
            "        public void handleMessage(Message msg) {\n" +
            "            AcceptTV.setText(msg.obj.toString());\n" +
            "        }\n" +
            "    };\n" +
            "    @Override\n" +
            "    protected void onCreate(Bundle savedInstanceState) {\n" +
            "        super.onCreate(savedInstanceState);\n" +
            "        setContentView(R.layout.activity_main);\n" +
            "        setTitle(\"蓝牙传输小程序\");\n" +
            "        textview = (TextView) findViewById(R.id.textView13);\n" +
            "        tvdevice = (TextView) findViewById(R.id.textView15);\n" +
            "        AcceptTV = (TextView) findViewById(R.id.textView14);\n" +
            "        listView = (ListView) findViewById(R.id.MyListView);\n" +
            "        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();\n" +
            "        if(mBluetoothAdapter.isEnabled()){\n" +
            "            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);\n" +
            "            startActivityForResult(intent,1);\n" +
            "        }\n" +
            "        Set<BluetoothDevice> paired = mBluetoothAdapter.getBondedDevices();\n" +
            "        if(paired.size()>0){\n" +
            "            for(BluetoothDevice device1:paired){\n" +
            "                arrayList.add(device1.getName()+ \":\" + device1.getAddress());\n" +
            "            }\n" +
            "        }\n" +
            "        tvdevice.setText(\"点击蓝牙设备以初始化接受环境\");\n" +
            "        final ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,arrayList);\n" +
            "        listView.setAdapter(arrayAdapter);\n" +
            "        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {\n" +
            "            @Override\n" +
            "            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {\n" +
            "                String s = (String) arrayAdapter.getItem(position);\n" +
            "                String address = s.substring(s.indexOf(\":\")+1).trim();\n" +
            "                Toast.makeText(MainActivity.this,\"正在初始化接受\"+address+\"的环境\",Toast.LENGTH_SHORT).show();\n" +
            "                device = mBluetoothAdapter.getRemoteDevice(address);\n" +
            "                Toast.makeText(MainActivity.this,\"接受环境准备好了，请用另一部手机发送数据\",Toast.LENGTH_LONG).show();\n" +
            "                new AcceptThread().start();\n" +
            "            }\n" +
            "        });\n" +
            "        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);\n" +
            "        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_UI);\n" +
            "    }\n" +
            "    public void beginSend(View v){\n" +
            "        new ConnectThread(device).start();\n" +
            "    }\n" +
            "    @Override\n" +
            "    public void onSensorChanged(SensorEvent event) {\n" +
            "        switch (event.sensor.getType()){\n" +
            "            case Sensor.TYPE_ACCELEROMETER:\n" +
            "                String acceleromter = \"加速度传感器数据：\\n\"+\"X: \"+event.values[0]+\"\\n\"+\"Y: \"+event.values[1]+\"\\n\"+\"Z: \"+event.values[2];\n" +
            "                acc_data = acceleromter;\n" +
            "                textview.setText(acceleromter);\n" +
            "                break;\n" +
            "        }\n" +
            "    }\n" +
            "    @Override\n" +
            "    public void onAccuracyChanged(Sensor sensor, int accuracy) {\n" +
            "    }\n" +
            "    //接受线程\n" +
            "    private class AcceptThread extends Thread{\n" +
            "        private BluetoothServerSocket serverSocket;\n" +
            "        private BluetoothSocket socket;\n" +
            "        private InputStream inputStream;\n" +
            "        {\n" +
            "            try{\n" +
            "                serverSocket = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(\"MyBluetooth\", UUID.fromString(\"d7afed6b-43c9-4a36-b63b-d2966aa23a91\"));\n" +
            "            }catch (IOException e){\n" +
            "            }\n" +
            "        }\n" +
            "        public void run(){\n" +
            "            try{\n" +
            "                socket = serverSocket.accept();\n" +
            "                inputStream = socket.getInputStream();\n" +
            "                while (true){\n" +
            "                    byte[] buffer = new byte[100];\n" +
            "                    int count = inputStream.read(buffer);\n" +
            "                    Message message = new Message();\n" +
            "                    message.obj = new String(buffer,0,count,\"utf-8\");\n" +
            "                    handler.sendMessage(message);\n" +
            "                }\n" +
            "            } catch (IOException e) {\n" +
            "                e.printStackTrace();\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "    //发送线程\n" +
            "    private class ConnectThread extends Thread {\n" +
            "        public OutputStream outputStream ;\n" +
            "        private BluetoothSocket mmSocket;\n" +
            "        public ConnectThread(BluetoothDevice device) {\n" +
            "            try {\n" +
            "                mmSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(\"d7afed6b-43c9-4a36-b63b-d2966aa23a91\"));\n" +
            "            } catch (IOException e) { }\n" +
            "        }\n" +
            "        public void run() {\n" +
            "            mBluetoothAdapter.cancelDiscovery();\n" +
            "            try {\n" +
            "                mmSocket.connect();\n" +
            "                outputStream = mmSocket.getOutputStream();\n" +
            "                while (true){\n" +
            "                    outputStream.write(acc_data.getBytes(\"utf-8\"));\n" +
            "                    try {\n" +
            "                        sleep(10);\n" +
            "                    } catch (InterruptedException e) {\n" +
            "                        e.printStackTrace();\n" +
            "                    }\n" +
            "                }\n" +
            "            } catch (IOException connectException) {\n" +
            "                try {\n" +
            "                    mmSocket.close();\n" +
            "                } catch (IOException closeException) { }\n" +
            "                return;\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.code);
        setTitle("主代码");
        tv1 = (TextView) findViewById(R.id.tv1);
        tv1.setText(code);
        Toast.makeText(this,github,Toast.LENGTH_LONG).show();
    }
}
