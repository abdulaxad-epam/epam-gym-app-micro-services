package epam.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@EnableTransactionManagement
@Configuration
public class ActiveMQConfig {

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Value("${spring.activemq.user}")
    private String username;

    @Value("${spring.activemq.password}")
    private String password;

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate template = new JmsTemplate(platformTransactionManager());
        template.setMessageConverter(jacksonJmsMessageConverter());
        template.setDeliveryPersistent(true);
        template.setSessionTransacted(true);
        return template;
    }

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        converter.setObjectMapper(jacksonObjectMapper());

        return converter;
    }

    @Bean
    public ObjectMapper jacksonObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        return objectMapper;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory defaultJmsListenerContainerFactory = new DefaultJmsListenerContainerFactory();
        defaultJmsListenerContainerFactory.setConnectionFactory(platformTransactionManager());
        defaultJmsListenerContainerFactory.setMessageConverter(jacksonJmsMessageConverter());
        defaultJmsListenerContainerFactory.setTransactionManager(jmsTransactionManager());
        defaultJmsListenerContainerFactory.setSessionTransacted(true);
        defaultJmsListenerContainerFactory.setErrorHandler(e -> log.warn("Transactional error: {}", e.getMessage()));

        return defaultJmsListenerContainerFactory;
    }

    @Bean
    public CachingConnectionFactory platformTransactionManager() {
        CachingConnectionFactory factory = new CachingConnectionFactory(
                new ActiveMQConnectionFactory(username, password, brokerUrl));
        factory.setSessionCacheSize(100);
        return factory;
    }

    @Bean
    public PlatformTransactionManager jmsTransactionManager() {
        return new JmsTransactionManager(platformTransactionManager());
    }

    @Bean
    @Primary
    public JpaTransactionManager transactionManager() {
        return new JpaTransactionManager();
    }

}
