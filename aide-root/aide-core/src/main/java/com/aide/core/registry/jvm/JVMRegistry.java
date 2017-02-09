package com.aide.core.registry.jvm;

import com.aide.core.AideException;
import com.aide.core.ServiceNotFoundException;
import com.aide.core.ServiceProvider;
import com.aide.core.registry.ServiceRegister;
import com.aide.core.registry.ServiceResolver;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Administrator on 2016/12/24.
 */
public class JVMRegistry implements ServiceRegister,ServiceResolver {

    private Map<String,List<ServiceProvider>> providerNameMap = new ConcurrentHashMap<>();
    private Map<String,ServiceProvider> providerIdMap = new ConcurrentHashMap<>();

    public JVMRegistry(){
    }
    public  JVMRegistry(List<ServiceProvider> providers){
        if(null!=providers){
            providers.forEach(this::register);
        }
    }
    @Override
    public void register(ServiceProvider serviceProvider) throws AideException {
        this.providerIdMap.replace(serviceProvider.getService().getId(),serviceProvider);
        CopyOnWriteArrayList list = new CopyOnWriteArrayList();
        list.add(serviceProvider);
        List list1 = (List)this.providerNameMap.putIfAbsent(serviceProvider.getService().getName(),list);
        if(null!=list1){
            list1.add(serviceProvider);
        }
    }

    @Override
    public void deregister(String serviceId) throws AideException {
        ServiceProvider serviceProvider = (ServiceProvider)this.providerIdMap.get(serviceId);
        if(null ==serviceProvider){
            throw new ServiceNotFoundException(serviceId);
        }else{
            List list  = (List) this.providerNameMap.get(serviceProvider.getService().getName());
            if(null!=list){
                list.remove(serviceProvider);
            }
            this.providerIdMap.remove(serviceId);
        }
    }

    @Override
    public void keepAlive(String serviceName) throws AideException {

    }

    @Override
    public List<ServiceProvider> resolve(String serviceName) throws AideException {
        LinkedList result = new LinkedList();
        List list =(List)this.providerNameMap.get(serviceName);
        if(null!=null){
            result.addAll(list);
        }
        return null;
    }
}
