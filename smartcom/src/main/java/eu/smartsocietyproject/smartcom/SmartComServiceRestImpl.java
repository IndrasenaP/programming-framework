package eu.smartsocietyproject.smartcom;

import at.ac.tuwien.dsg.smartcom.Communication;
import at.ac.tuwien.dsg.smartcom.adapter.OutputAdapter;
import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.rest.model.NotificationDTO;
import at.ac.tuwien.dsg.smartcom.rest.model.RoutingRuleDTO;
import eu.smartsocietyproject.peermanager.PeerManager;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import at.ac.tuwien.dsg.smartcom.exception.CommunicationException;
import at.ac.tuwien.dsg.smartcom.model.Message;

import java.time.Duration;

public class SmartComServiceRestImpl implements SmartComService {

	private RestOperations restOperations;

	public SmartComServiceRestImpl(RestOperations restOperations){
		this.restOperations = restOperations;
	}

	@Override
	public void send(Message msg) throws CommunicationException {

		ResponseEntity<String> res = restOperations.postForEntity("http://localhost:8081/route", msg, String.class);

	}

	public static Factory factory() {return new Factory();}


	public void removeOutputAdapter(Identifier email) {

		//ResponseEntity<String> res = restOperations.postForEntity("http://localhost:8081/SmartCom/message", msg, String.class);

	}

	public void registerOutputAdapter(Class<? extends OutputAdapter>  adapter) {

		RoutingRuleDTO rule = new RoutingRuleDTO();
		rule.setType("test");

		ResponseEntity<String> res = restOperations.postForEntity("http://localhost:8081/SmartCom/notification", rule , String.class);

	}

	public Identifier registerNotificationCallback(NotificationDTO notificationDTO) {
		ResponseEntity<Identifier> identifier = restOperations.postForEntity("http://localhost:8081/SmartCom/notification", notificationDTO, Identifier.class);

		return identifier.getBody();
	}

	public static class Factory implements SmartComService.Factory {
		@Override
		public SmartComService create(PeerManager pm) {
			RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
			RestOperations restOperations = restTemplateBuilder.setReadTimeout(Duration.ofSeconds(10))
					.setConnectTimeout(Duration.ofSeconds(10)).build();

			return new SmartComServiceRestImpl(restOperations);
		}
	}



}
