package com.adroit.datacollector.actors;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;

@Component
public class DataCollectorProducer implements IndirectActorProducer {

    private final ApplicationContext applicationContext;
    private final String actorBeanName;

    public DataCollectorProducer(ApplicationContext applicationContext,
        String actorBeanName) {
        this.applicationContext = applicationContext;
        this.actorBeanName = actorBeanName;
    }

  public DataCollectorProducer() {
	  
	  this.applicationContext = null;
      this.actorBeanName = "supervisor";
  }
    @Override
    public Actor produce() {
        return (Actor) applicationContext.getBean(actorBeanName);
    }

    @Override
    public Class<? extends Actor> actorClass() {
        return (Class<? extends Actor>) applicationContext.getType(actorBeanName);
    }
}
