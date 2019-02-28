package com.itc.smartbroadcast;


import com.itc.smartbroadcast.util.ConfigUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.encryption;

public class Test {

    @org.junit.Test
    public void test() {


        String str = "1c0002b17c76682ecec200000000000001000000000000000000";
        String hex = SmartBroadCastUtils.checkSum(str);
        System.out.println(hex);


    }


}
