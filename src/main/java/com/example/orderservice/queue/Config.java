package com.example.orderservice.queue;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.amqp.core.BindingBuilder.bind;

@Configuration
public class Config {

    public static final String DIRECT_EXCHANGE = "direct.exchange";
    public static final String TOPIC_EXCHANGE = "amq.topic";
    public static final String QUEUE_ORDER = "direct.queue.order";
    public static final String DIRECT_QUEUE_PAY = "direct.queue.pay";
    public static final String DIRECT_QUEUE_INVENTORY = "direct.queue.inventory";
    public static final String QUEUE_PAY = "topic.queue.pay";
    public static final String QUEUE_INVENTORY = "topic.queue.inventory";



    public static final String DIRECT_ROUTING_KEY_ORDER = "direct.routingKeyOrder";
    //    public static final String DIRECT_ROUTING_KEY_PAY = "direct.routingKeyPay";
//    public static final String DIRECT_ROUTING_KEY_INVENTORY = "routingKeyInventory";
    public static final String DIRECT_ROUTING_KEY_PAY = "order.routingKeyPay";
    public static final String DIRECT_ROUTING_KEY_INVENTORY = "order.routingKeyInventory";
    public static final String TOPIC_SHARE_ROUTING_KEY = "order.#";


    @Bean
    public Declarables binding() {
        Queue directQueueOrder = new Queue(QUEUE_ORDER);
        Queue queuePay = new Queue(QUEUE_PAY);
        Queue queueInventory = new Queue(QUEUE_INVENTORY);
        Queue directQueueInventory = new Queue(DIRECT_QUEUE_INVENTORY);
        Queue directQueuePay = new Queue(DIRECT_QUEUE_PAY);
        DirectExchange directExchange = new DirectExchange(DIRECT_EXCHANGE);
        TopicExchange topicExchange = new TopicExchange(TOPIC_EXCHANGE);
        return new Declarables(
                directQueueOrder,
                queuePay,
                queueInventory,
                topicExchange,
                bind(directQueueOrder).to(directExchange).with(DIRECT_ROUTING_KEY_ORDER),

                bind(directQueuePay).to(directExchange).with(DIRECT_ROUTING_KEY_PAY),
                bind(directQueueInventory).to(directExchange).with(DIRECT_ROUTING_KEY_INVENTORY),

                bind(queuePay).to(topicExchange).with(TOPIC_SHARE_ROUTING_KEY),
                bind(queueInventory).to(topicExchange).with(TOPIC_SHARE_ROUTING_KEY)
        );
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }

}