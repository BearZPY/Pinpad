package com.io.pyz.pinpad;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private Toast toast = null;

    @BindView(R.id.tv_show_result)
    TextView tvShowResult;

    @BindView(R.id.bt_input_password)
    Button btInputPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.bt_input_password)
    public void onViewClicked(View v){
        if(v.getId() == R.id.bt_input_password){
            if(toast != null){
                toast.cancel();
            }
            toast = Toast.makeText(this.getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT);
            toast.show();

            Intent intent = new Intent(this, PinpadActivity.class);
            startActivity(intent);
        }
    }
}
