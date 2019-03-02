package eu.smartsocietyproject.smartcom.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@ConfigurationProperties
public class SmartComRestTemplate {
	
	private RestTemplate restTemplate;
	
}
