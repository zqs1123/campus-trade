package org.zqs.orderservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 1. 延迟队列（消息在这里等待到期）
    @Bean
    public Queue delayQueue() {
        return QueueBuilder.durable("order.delay.queue")
                .withArgument("x-dead-letter-exchange", "order.dlx.exchange")
                .withArgument("x-dead-letter-routing-key", "order.dlx.routing")
                .build();
    }

    // 2. 延迟交换机
    @Bean
    public DirectExchange delayExchange() {
        return new DirectExchange("order.delay.exchange");
    }

    // 3. 延迟队列绑定到延迟交换机
    @Bean
    public Binding delayBinding() {
        return BindingBuilder.bind(delayQueue())
                .to(delayExchange())
                .with("order.delay.routing");
    }

    // 4. 死信队列（消息到期后最终到达这里）
    @Bean
    public Queue dlxQueue() {
        return QueueBuilder.durable("order.dlx.queue").build();
    }

    // 5. 死信交换机
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange("order.dlx.exchange");
    }

    // 6. 死信队列绑定到死信交换机
    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(dlxQueue())
                .to(dlxExchange())
                .with("order.dlx.routing");
    }
}