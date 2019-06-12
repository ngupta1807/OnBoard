package com.example.servicetypes;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.MyAidlInterface;

public class AidlService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return new MyAidlInterface.Stub(){
            @Override
            public int multiply(int firstNumber, int secondNumber) {
                return (firstNumber * secondNumber);
            }
        };
    }


}