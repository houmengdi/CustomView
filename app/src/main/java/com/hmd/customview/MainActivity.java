package com.hmd.customview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.hmd.address.selector.SelectAddressWindow;
import com.hmd.address.selector.listener.OnSelectedFinishListener;

public class MainActivity extends AppCompatActivity implements OnSelectedFinishListener {

    private SelectAddressWindow mSelectAddressWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void selectAddress(View view) {
        if (mSelectAddressWindow == null) {
            mSelectAddressWindow = new SelectAddressWindow(this);
        }
        mSelectAddressWindow.setSelectedFinishListener(this);
        mSelectAddressWindow.showAsDropDown(view, 0, 0);
    }

    @Override
    public void onSelectedFinish(String province, String city, String district) {
        Toast.makeText(this, province + city + district, Toast.LENGTH_SHORT).show();
    }
}
