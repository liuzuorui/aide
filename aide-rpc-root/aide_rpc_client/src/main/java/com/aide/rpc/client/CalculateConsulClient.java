package com.aide.rpc.client;

import com.aide.rpc.api.Calculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2016/11/10.
 */
@Component
public class CalculateConsulClient {

    public void cal(int a, int b){
//        System.out.println("this is clinet result");
        try{
//            System.out.println(calculator.add(a,b));
            int max = 100000;
            Long start = System.currentTimeMillis();
            System.out.print("thrift consul test begin");
            for (int i = 0; i < max; i++) {
                calculator.add(1, 2);;
            }
            Long end = System.currentTimeMillis();
            Long elapse = end - start;
            int perform = Double.valueOf(max / (elapse / 1000d)).intValue();

            System.out.print("thrift consul" + max + " 次RPC调用，耗时：" + elapse + "毫秒，平均" + perform + "次/秒");
        }catch (Exception e){
            e.printStackTrace();;
        }
    }


    @Autowired
    private Calculator calculator;
}
