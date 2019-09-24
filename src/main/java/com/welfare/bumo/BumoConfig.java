package com.welfare.bumo;

import io.bumo.SDK;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author ：chenxinyou.
 * @Title :
 * @Date ：Created in 2019/9/11 17:55
 * @Description:
 */
@Configuration
public class BumoConfig {
    @Value("${welfare.bumo.url}")
    private String url;

    @Bean
    public SDK getSDK(){
        return SDK.getInstance(url);
    }
}
