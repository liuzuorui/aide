package com.aide.test;


import com.aide.rpc.client.CalculateConsulClient;
import org.springframework.context.ApplicationContext;
import com.aide.util.ContextSupport;

/**
 * Created by Administrator on 2016/11/10.
 */
public class CalculateTest extends ContextSupport {
    public static void main(String[] args) throws Exception{
        ApplicationContext context = createContext();
        CalculateConsulClient action = context.getBean(CalculateConsulClient.class);
        action.cal(11,21);
    }
}
