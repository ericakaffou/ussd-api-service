package com.app.ussd.controller;

import static com.app.ussd.utils.UrlConstants.APP_ROOT;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.ussd.metier.App2Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(APP_ROOT + "/app2inbound")
public class InboundRequestConroller {
	
	
	
	
	
	@PostMapping("/app2inbound")
	public ResponseEntity<String> App3Inbound(@RequestBody App2Data request){
		
		// envoie à ServiceManager.ussdService

		return ResponseEntity.ok(null);
	}

}
