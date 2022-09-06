package com.lagou.edu.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lagou.edu.ResModel;
import com.lagou.edu.dao.LagouTokenDao;
import com.lagou.edu.dao.LagouUserDao;
import com.lagou.edu.entity.LagouToken;
import com.lagou.edu.entity.LagouUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 定义全局过滤器，会对所有路由生效
 */
@Slf4j
@Component  // 让容器扫描到，等同于注册了
@RefreshScope
public class BlackListFilter implements GlobalFilter, Ordered {

    @Autowired
    private LagouTokenDao lagouTokenDao;

    /**
     * 暴力访问最大限度
     */
    @Value("${ip-visit-setting.visit-count}")
    private int visitCount;

    /**
     * ip白名单
     */
    @Value("#{'${ip-visit-setting.white-ip-list}'.split(',')}")
    private List<String> whiteIpList;

    /**
     * 黑名单缓存
     */
    private static List<String> blackList = new CopyOnWriteArrayList<>();

    /**
     * 临时IP存放容器
     */
    public static Map<String, AtomicInteger> ipTemContent = new ConcurrentHashMap<>();

    /**
     * 过滤器核心方法
     * @param exchange 封装了request和response对象的上下文
     * @param chain 网关过滤器链（包含全局过滤器和单路由过滤器）
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 思路：获取客户端ip，判断是否在黑名单中，在的话就拒绝访问，不在的话就放行
        // 从上下文中取出request和response对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 从request对象中获取客户端ip
        String clientIp = request.getRemoteAddress().getHostString();
        if(!whiteIpList.contains(clientIp)&&blackList.contains(clientIp)) {
            // 决绝访问，返回
            response.setStatusCode(HttpStatus.UNAUTHORIZED); // 状态码
            log.debug("=====>IP:" + clientIp + " 在黑名单中，将被拒绝访问！");
            String data = "Request be denied!";
            DataBuffer wrap = response.bufferFactory().wrap(data.getBytes());
            return response.writeWith(Mono.just(wrap));
        }

        // 记录IP访问频率
        AtomicInteger atomicInteger = ipTemContent.computeIfPresent(clientIp, (s, integer) -> {
            integer.set(integer.incrementAndGet());
            return integer;
        });
        if ( atomicInteger == null ) {
            ipTemContent.put(clientIp,new AtomicInteger(1));
        }

        //  如果超过设定的频率则将其加入黑名单中
        int count = ipTemContent.get(clientIp) == null ? 0 : ipTemContent.get(clientIp).get();
        if ( !whiteIpList.contains(clientIp) && count > visitCount ) {
            blackList.add(clientIp);
        }

        //  拦截没有token的请求
        String path = request.getURI().getPath();
        if ( !path.contains("user/login") && !path.contains("user/register") && !path.contains("verificationCode/createCodeAndSendEmail") ) {

            //  判断有无tokenid,有就放行
            List<HttpCookie> tokenId = request.getCookies().get("tokenId");
            if ( tokenId != null ) {
                List<String> collect = tokenId.stream().map(HttpCookie::getValue).collect(Collectors.toList());
                for (String s : collect) {
                    LagouToken lagouToken = new LagouToken();
                    lagouToken.setToken(s);
                    Optional<LagouToken> one = lagouTokenDao.findOne(Example.of(lagouToken));
                    if (one.isPresent() && LocalDateTime.now().isBefore(one.get().getExpireTime()) ) {
                        return chain.filter(exchange);
                    }
                }
            }

            //  未找到tokenid则进行拦截
            String data;
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                data= objectMapper.writeValueAsString(ResModel.FAIL("3000", "user not logged in"));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                data = "system error";
            }
            log.debug("用户未登入");
            DataBuffer wrap = response.bufferFactory().wrap(data.getBytes());
            return response.writeWith(Mono.just(wrap));
        }


        return chain.filter(exchange);

    }

    /**
     * 返回值表示当前过滤器的顺序(优先级)，数值越小，优先级越高
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }

}
