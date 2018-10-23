package com.car.controller;

import java.util.Calendar;

class One_s_Timer {
    String time;
    void Timer() throws InterruptedException {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        time = year+"年"+month+"月"+day+"日"+hour+":"+minute+":"+second;
        Thread.sleep(1000);
    }
}
