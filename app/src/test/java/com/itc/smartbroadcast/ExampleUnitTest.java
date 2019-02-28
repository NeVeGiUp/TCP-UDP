package com.itc.smartbroadcast;

import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.junit.Test;

/**
 * Example local unit AddRingingTaskActivity, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void test(){


        String str = "3f0000b97c76682ecec200000000610064006d0069006e0000000000000000000000000000000000000000000000310032003300340035003600000000";


        System.out.print(SmartBroadCastUtils.checkSum(str));  //1537372800000
//        System.out.print("-------------"+Integer.toHexString(14));  //1537372800000
    }
}