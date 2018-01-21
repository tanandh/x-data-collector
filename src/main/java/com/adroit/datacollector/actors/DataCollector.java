package com.adroit.datacollector.actors;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.adroit.datacollector.message.publisher.MessagePublisher;
import com.adroit.datacollector.vo.ExchangeConfig;

import akka.actor.AbstractActor;
import akka.actor.Scheduler;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpEntity;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.util.ByteString;
import scala.concurrent.duration.Duration;

@Component
@Scope("prototype")
public class DataCollector extends AbstractActor {

	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
	Scheduler scheduler = null;
	ActorMaterializer materializer = null;
	
	@Autowired
	MessagePublisher messagePublisher;
	
	@Override
	public void preStart() throws Exception {
		scheduler = getContext().getSystem().scheduler();
		materializer = ActorMaterializer.create(getContext().getSystem());
	}

	// Receives Exchange Configuration from Supervisor to schedule the data collection
	// Once the scheduler is started, it posts the service configuration to fetch the response.
	// This method handles both the messages.
	@Override
	public Receive createReceive() {
		return receiveBuilder().match(String.class, s -> {
			log.info("Data Collecting is Started: {}", s);
			ExchangeConfig config = new ExchangeConfig();
			config.setExchnageName("GDAX");
			scheduler.schedule(Duration.Zero(), Duration.create(10, TimeUnit.SECONDS), this.getSelf(), config,
					getContext().getSystem().dispatcher(), null);

		}).match(ExchangeConfig.class, config -> {
			log.info("Received message from Data Collector to fetch Ticker data: {}", config);
			
			// TODO Based on the type of the service call either handleRestApi or handleWebHookApi
			handleRestApi(config);

		}).matchAny(o -> {
			log.info("received other message");

		}).build();
	}

	// Calls the REST service and post the response to Redis
	private void handleRestApi(ExchangeConfig config) {
		try {
			log.info("Calling GDAX Ticker API: ");
			
			// TODO get the servie details from the configuration
			
			CompletableFuture<HttpResponse> response = (CompletableFuture<HttpResponse>) Http
					.get(getContext().getSystem())
					.singleRequest(HttpRequest.create("https://api.gdax.com/products/BTC-USD/ticker"), materializer);
			HttpResponse httpResponse = response.get();
			HttpEntity.Strict result = httpResponse.entity().toStrict(1000, materializer).toCompletableFuture().get();

			ByteString byteString = result.getData();
			System.out.println(byteString.utf8String());
			//Jedis redisClient = new Jedis("localhost", 6379);
			//redisClient.set("GDAX_BTC_USD", byteString.utf8String());
			//redisClient.close();
			messagePublisher.publish(byteString.utf8String());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Handles the web hooks
	private void handleWebHookApi(ExchangeConfig config){
		// TODO implementation is pending
	}
}
