package com.adroit.datacollector.actors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.adroit.datacollector.extension.DataCollectorExtension;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;

@Component
@Scope("prototype")
public class Supervisor extends AbstractActor {

	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

	@Autowired
	private DataCollectorExtension dataCollectorExtension;

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(String.class, s -> {
			log.info("Starting Data Collector... {}", s);

			ActorRef dataCollector = getContext().getSystem().actorOf(dataCollectorExtension.props("dataCollector"));

			// TODO Sending the exchange configuration is pending. 
			// TODO Once the Config loader is completed change the hard coded logic

			dataCollector.tell("Start", sender());

		}).matchAny(o -> {
			log.info("received unknown message");
			getContext().system().terminate();
		}).build();
	}

}
