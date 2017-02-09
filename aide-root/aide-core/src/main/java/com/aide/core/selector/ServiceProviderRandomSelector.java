package com.aide.core.selector;

import com.aide.core.ServiceProvider;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Administrator on 2016/12/24.
 */
public class ServiceProviderRandomSelector implements ServiceProviderSelector {
    private static final ServiceProviderSelector INSTANCE = new ServiceProviderRandomSelector();

    public ServiceProviderRandomSelector(){

    }
    public static ServiceProviderSelector get(){
        return  INSTANCE;
    }
    @Override
    public ServiceProvider select(List<ServiceProvider> providers) {
        return (ServiceProvider) randomGet(providers);
    }
    private static <T> T randomGet(List<T> list){
        if(null!=list &&!list.isEmpty()){
            int pos = ThreadLocalRandom.current().nextInt(list.size());
            return list.get(pos);
        }else{
            return  null ;
        }
    }
}
