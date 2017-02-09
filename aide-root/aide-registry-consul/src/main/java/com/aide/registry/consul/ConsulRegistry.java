package com.aide.registry.consul;

import com.aide.core.AideException;
import com.aide.core.Service;
import com.aide.core.ServiceNotFoundException;
import com.aide.core.ServiceProvider;
import com.aide.core.registry.ServiceRegister;
import com.aide.core.registry.ServiceResolver;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.OperationException;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.health.model.HealthService;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/24.
 */
public class ConsulRegistry implements ServiceRegister, ServiceResolver {

    private final ConsulClient consulClient ;
    private final  long ttl;
    private static final  String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8500;
    private static final String VERSION_TAG_PREFIX="version: ";

    public ConsulRegistry(){
        this("localhost",8500,16000L);
    }
    public ConsulRegistry(String host,int port){
        this(host,port,16000L);
    }
    public ConsulRegistry(String host ,int port ,long ttl){
        if(null!=host){
           host = host.trim();
        }
        if(null ==host ||host.isEmpty()){
            host = "localhost";
        }
        if(port<0){
            port = 8500;
        }
        if(ttl<0){
            ttl = 16000L;
        }
        this.consulClient = new ConsulClient(host,port);
        this.ttl = ttl ;
    }

    @Override
    public void register(ServiceProvider serviceProvider) throws AideException {
        NewService newService = createServiceDef(serviceProvider,this.ttl);
        try{
            this.consulClient.agentServiceRegister(newService);
        }catch (Exception e){
            throw  new AideException(e);
        }
    }

    @Override
    public void deregister(String serviceId) throws AideException {
        try {
            this.consulClient.agentServiceDeregister(serviceId);
        }catch (Exception e){
            throw  new AideException(e);
        }
    }

    @Override
    public void keepAlive(String serviceId) throws AideException {
        try {
            String checkId = getCheckId(serviceId);
            this.consulClient.agentCheckPass(checkId);
        }catch (OperationException e){
            throw translate(e);
        }
    }

    @Override
    public List<ServiceProvider> resolve(String serviceName) throws AideException {
        try{
            Response response = this.consulClient.getHealthServices(serviceName,true, QueryParams.DEFAULT);
            List healthServices = (List)response.getValue();
            LinkedList providers = new LinkedList();
            Iterator iterator = healthServices.iterator();
            while (iterator.hasNext()){
                HealthService healthService = (HealthService)iterator.next();
                HealthService.Service service = healthService.getService();
                String serviceVersion = getVersionFromTags(service.getTags());
                String serviceId = service.getId();
                com.aide.core.Service  s = new com.aide.core.Service(serviceName,serviceVersion,serviceId);
                ServiceProvider serviceProvider = new ServiceProvider(s);
                serviceProvider.setHost(service.getAddress());
                serviceProvider.setPort(service.getPort().intValue());
                providers.add(serviceProvider);
            }
            return  providers ;
        }catch (OperationException e){
            throw  translate(e);
        }
    }
    private static String getCheckId(String serviceId) {
        return "service:" + serviceId;
    }

    private static AideException translate(OperationException e) {
        String serviceNotFoundContent = "CheckID does not have associated TTL";
        return (AideException)(serviceNotFoundContent.equals(e.getStatusContent())?new ServiceNotFoundException(e):new AideException(e));
    }
    private static NewService createServiceDef(ServiceProvider serviceProvider,long ttl){
        NewService newService = new NewService();
        newService.setAddress(serviceProvider.getHost());
        newService.setPort(Integer.valueOf(serviceProvider.getPort()));
        newService.setId(serviceProvider.getService().getId());
        newService.setName(serviceProvider.getService().getName());
        String version = makeVersionTag(serviceProvider.getService());
        newService.setTags(Collections.singletonList(version));
        int ttlInSeconds = (int) ttl/1000;
        NewService.Check check = new NewService.Check();
        check.setTtl(ttlInSeconds+"s");
        newService.setCheck(check);
        return newService ;
    }

    private static String makeVersionTag(com.aide.core.Service service) {
        String version = service.getVersion();
        return "version: " + version;
    }
    private static String getVersionFromTags(List<String> tags) {
        if(tags != null && !tags.isEmpty()) {
            Iterator var1 = tags.iterator();
            String version;
            do {
                if(!var1.hasNext()) {
                    return "1.0.0";
                }
                String tag = (String)var1.next();
                version = getVersionFromTag(tag);
            } while(version == null);

            return version;
        } else {
            return "1.0.0";
        }
    }
    private static boolean isVersionTag(String tag) {
        return tag != null && tag.startsWith("version: ");
    }

    private static String getVersionFromTag(String tag) {
        return !isVersionTag(tag)?null:tag.substring("version: ".length());
    }
}
