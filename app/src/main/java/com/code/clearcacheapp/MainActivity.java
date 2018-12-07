package com.code.clearcacheapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.code.clearcacheapp.interfaces.InfoCallback;
import com.code.clearcacheapp.utils.DataCleanManager;
import com.code.clearcacheapp.utils.RootUtils;

public class MainActivity extends BaseActivity implements InfoCallback {

    //要删除的APP包名
    private final String appPackageName = "com.hundsun.qy.hospitalcloud.bj.xhhosp.hsyy";

    //要删除的文件夹
    private final String dir1 = "/mnt/sdcard/hsyuntai_log";
    private final String dir2 = "/mnt/sdcard/com.hundsun";
    private final String dir3 = "/mnt/sdcard/Android/data/" + appPackageName;

    //要删除的文件
    private final String file1 = "/mnt/sdcard/hundsunApp";

    private Button clear;
    private TextView info;

    private DataCleanManager dataCleanManager;
    private StringBuilder stringBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clear = findViewById(R.id.btn_clear);
        info = findViewById(R.id.info);


        dataCleanManager = new DataCleanManager(this);


        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringBuilder = new StringBuilder();

                if(RootUtils.upgradeRootPermission(getPackageCodePath())){
                    Toast.makeText(MainActivity.this, "已获取Root权限", Toast.LENGTH_SHORT).show();

                    //清除APP数据
                    if(dataCleanManager.cleanOtherAppData(appPackageName)){
                        Toast.makeText(MainActivity.this, "成功清除app数据", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MainActivity.this, "清除app数据失败！！！", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "获取Root权限失败", Toast.LENGTH_SHORT).show();
                }

                //删除外存中的指定文件夹、文件
                if(dataCleanManager.getSDPath() != null){
                    getInfo("指定的外部数据 清除开始...\n");
                    dataCleanManager.delete(MainActivity.this, dir1);
                    dataCleanManager.delete(MainActivity.this, dir2);
                    dataCleanManager.delete(MainActivity.this, dir3);
                    dataCleanManager.delete(MainActivity.this, file1);
                    getInfo("指定的外部数据 清除结束...\n");
                }
            }
        });
    }

    @Override
    public void getInfo(String str) {
        stringBuilder.append(str);
        info.setText(stringBuilder.toString());
    }
}
