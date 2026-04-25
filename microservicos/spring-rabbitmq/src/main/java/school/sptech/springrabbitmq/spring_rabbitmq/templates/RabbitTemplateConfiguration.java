package school.sptech.springrabbitmq.spring_rabbitmq.templates;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter; // ← correto
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;             // ← correto
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import school.sptech.springrabbitmq.spring_rabbitmq.dto.RabbitPropertiesConfiguration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(RabbitPropertiesConfiguration.class)
public class RabbitTemplateConfiguration {

    private final RabbitPropertiesConfiguration properties;

    @Bean
    public Declarables rabbitDeclarables() {
        FanoutExchange exchange = new FanoutExchange(properties.exchange().name());

        Queue queue = QueueBuilder
                .durable(properties.queue().name())
                .build();

        Binding binding = BindingBuilder
                .bind(queue)
                .to(exchange);

        return new Declarables(exchange, queue, binding);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter(); // ← Jackson2, não JacksonJson
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }
}