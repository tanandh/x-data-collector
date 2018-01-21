package com.adroit.datacollector.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.adroit.datacollector.extension.DataCollectorExtension;
import com.adroit.datacollector.message.publisher.MessagePublisher;
import com.adroit.datacollector.message.publisher.MessagePublisherImpl;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;


@Configuration
@Lazy
@ComponentScan(basePackages = { "com.adroit.datacollector"})
public class ApplicationConfiguration {

    // The application context is needed to initialize the Application Extension
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DataCollectorExtension dataCollectorExtension;

    /**
     * Actor system singleton for this application.
     */
    @Bean
    public ActorSystem actorSystem() {

        ActorSystem system = ActorSystem
            .create("DataCollectorProcessing", akkaConfiguration());

        // Initialize the application context in the Application Extension
        dataCollectorExtension.initialize(applicationContext);
        return system;
    }

    /**
     * Read configuration from application.conf file
     */
    @Bean
    public Config akkaConfiguration() {
        return ConfigFactory.load();
    }

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
		JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory();
        
        redisConnectionFactory.setHostName("127.0.0.1");
        redisConnectionFactory.setPort(6379);
        return redisConnectionFactory;
    }

    @Bean
    RedisTemplate< String, Object > redisTemplate() {
        final RedisTemplate< String, Object > template =  new RedisTemplate< String, Object >();
        template.setConnectionFactory( jedisConnectionFactory() );
        template.setKeySerializer( new StringRedisSerializer() );
        template.setHashValueSerializer( new GenericToStringSerializer< Object >( Object.class ) );
        template.setValueSerializer( new GenericToStringSerializer< Object >( Object.class ) );
        return template;
    }
    
    @Bean
    MessagePublisher messagePublisher() {
        return new MessagePublisherImpl( );
    }
    
    @Bean
    ChannelTopic topic() {
        return new ChannelTopic( "pubsub:ticker" );
    }
}
