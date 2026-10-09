package com.schwab.agentic.config;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.*;

@Configuration
public class SpringAIConfig {
    @Bean
    public ChatClient agentChatClient(ChatClient.Builder builder) {
        return builder.build();
    }
}
