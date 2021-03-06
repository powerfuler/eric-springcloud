package com.gupaoedu.malll.gpmallportal;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

public class GpDefinieIpHashRule extends AbstractLoadBalancerRule {
    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
        //....
    }

    public Server choose(ILoadBalancer lb, Object key) {
        if (lb == null) {
            return null;
        }
        Server server = null;
        while (server == null) {
            //表示启动的服务列表.(默认情况下单纯只用Ribbon时，不会对目标服务做心跳检测）
            List<Server> upList = lb.getReachableServers();
            List<Server> allList = lb.getAllServers();
            int serverCount = upList.size();
            if (serverCount == 0) {
                return null;
            }
            int index = ipAddressHash(serverCount);
            server = upList.get(index);
        }
        return server;
    }

    private int ipAddressHash(int serverCount) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String remoteAddr = requestAttributes.getRequest().getRemoteAddr();
        int code = Math.abs(remoteAddr.hashCode());
        return code % serverCount;
    }

    @Override
    public Server choose(Object key) {
        return choose(getLoadBalancer(), key);
    }
}
