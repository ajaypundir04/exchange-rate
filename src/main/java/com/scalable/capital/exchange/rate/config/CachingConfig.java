package com.scalable.capital.exchange.rate.config;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import java.util.HashMap;
import java.util.Map;

import static java.time.Duration.ofSeconds;
import static org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig;

@Configuration
@EnableConfigurationProperties(CachingConfig.CacheTtlConfig.class)
@EnableCaching
public class CachingConfig {

    private final CacheTtlConfig cacheTtlConfig;

    public CachingConfig(CacheTtlConfig cacheTtlConfig) {
        this.cacheTtlConfig = cacheTtlConfig;
    }

    @Bean
    RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> {
            Map<String, RedisCacheConfiguration> cacheConfiguration = new HashMap<>();
            cacheConfiguration.put(
                    "ecbExchangeRate",
                    defaultCacheConfig().entryTtl(
                            ofSeconds(cacheTtlConfig.getCurrent())
                    )
            );

            cacheConfiguration.put(
                    "ecbHistoricalExchangeRate",
                    defaultCacheConfig().entryTtl(
                            ofSeconds(cacheTtlConfig.getHistory())
                    )
            );
            builder.withInitialCacheConfigurations(cacheConfiguration);
        };
    }

    @ConfigurationProperties(prefix = "ecb.refresh")
    public static class CacheTtlConfig {
        private int current;
        private int history;

        public int getCurrent() {
            return current;
        }

        public void setCurrent(int current) {
            this.current = current;
        }

        public int getHistory() {
            return history;
        }

        public void setHistory(int history) {
            this.history = history;
        }
    }
}