package com.tomzxy.web_quiz.configs;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.protocol.ProtocolVersion;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    @Bean
    public LettuceClientConfigurationBuilderCustomizer lettuceClientConfigurationBuilderCustomizer() {
        return clientConfigurationBuilder -> clientConfigurationBuilder.clientOptions(
                ClientOptions.builder()
                        .protocolVersion(ProtocolVersion.RESP2)
                        .build());
    }
}