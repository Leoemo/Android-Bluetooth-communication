package leoemo.com.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by 65993 on 2017/5/27.
 */

public class Login extends AppCompatActivity {
    Button btn1,btn2,btn3,btn4;
    BluetoothAdapter bluetoothAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        setTitle("一些必要的准备工作");
        btn1 = (Button) findViewById(R.id.Btn1);
        btn2 = (Button) findViewById(R.id.Btn2);
        btn3 = (Button) findViewById(R.id.Btn3);
        btn4 = (Button) findViewById(R.id.Btn4);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!bluetoothAdapter.isEnabled()){
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent,1);
                }
                Toast.makeText(Login.this,"正在开启蓝牙",Toast.LENGTH_SHORT).show();

            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bluetoothAdapter.isEnabled()){
                    Toast.makeText(Login.this,"请先打开蓝牙",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent();
                    intent.setClass(Login.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Login.this,SourceCode.class);
                startActivity(intent);
            }
        });

    }
    public void setBtn3(View v){
        Toast.makeText(this,"这个人要装逼了，快跑！",Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.setClass(Login.this,Leoemo.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        if(bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
            Toast.makeText(Login.this, "由于程序关闭，蓝牙执行关闭操作", Toast.LENGTH_LONG).show();
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == event.KEYCODE_BACK){
            moveTaskToBack(false);
            Toast.makeText(Login.this,"转入后台运行，而不杀死程序(不关闭蓝牙)",Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
