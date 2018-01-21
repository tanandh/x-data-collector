package com.adroit.datacollector.message.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class MessagePublisherImpl implements MessagePublisher{
	
	 @Autowired
	 private RedisTemplate< String, Object > template;
	 
	 @Autowired
	 private ChannelTopic topic; 

	@Override
	public void publish(String message) {
		template.convertAndSend( topic.getTopic(), message );
		
	}

}
