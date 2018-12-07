package com.code.clearcacheapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

/**
 * activity 父类
 */
public abstract class BaseActivity extends AppCompatActivity {

    private String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPermissions();
        }
    }

    /**
     * android 6.0及以上需要申请权限
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getPermissions(){
        for(String p: permissions){
            if(checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{p}, 100);
            }
        }
    }
}
