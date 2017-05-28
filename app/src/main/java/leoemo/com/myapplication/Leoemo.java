package leoemo.com.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

/**
 * Created by 65993 on 2017/5/28.
 */

public class Leoemo extends AppCompatActivity {
    private String name = "Hello,I'm Canicula.This app just a beta version,thanks to everyone once helped me!\n\n\n\n ";
    private String other = "螃蟹在剥我的壳，笔记本在写我\n"+
                             " 漫天的我落在枫叶上雪花上\n" +
                             " 而你在想我\n\n\n";
    private TextView tv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leoemo);
        setTitle("用什么标题好呢...");
        tv1 = (TextView) findViewById(R.id.tv1);
        tv1.setText(name+other);
    }
}
