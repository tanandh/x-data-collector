package com.adroit.datacollector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.adroit.datacollector.extension.DataCollectorExtension;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

@Configuration
@EnableAutoConfiguration
@ComponentScan("com.adroit.datacollector")
public class DataCollectorApplication {

	public static void main(String[] args) throws Exception {

		ApplicationContext context = SpringApplication.run(DataCollectorApplication.class, args);

		ActorSystem system = context.getBean(ActorSystem.class);

		DataCollectorExtension ext = context.getBean(DataCollectorExtension.class);

		ActorRef supervisor = system.actorOf(ext.props("supervisor"));
		System.out.println(supervisor);

		supervisor.tell("GDAX", supervisor);

	}

}
