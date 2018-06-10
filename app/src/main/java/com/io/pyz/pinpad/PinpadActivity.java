package com.io.pyz.pinpad;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.io.pyz.pinpad.view.Pinpad;

public class PinpadActivity extends Activity {

    private Toast toast = null;
    private Handler mHandler = new Handler();
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinpad);
        mContext = this.getApplicationContext();
        Pinpad pinpad = findViewById(R.id.test_pinpad);
        pinpad.setOnFinishListener(new Pinpad.OnFinishListener() {
            @Override
            public void onFinish(final int returnCode,final String password) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        if(toast != null){
                            toast.cancel();
                        }
                        toast = Toast.makeText(mContext, "结果：" + returnCode
                                + " ," + password, Toast.LENGTH_SHORT);
                        toast.show();
                        if(returnCode == -1){
                            finish();
                        }
                    }
                });
            }
        });

    }
}
