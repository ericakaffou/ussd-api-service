package com.app.ussd.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.ussd.metier.App2Data;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Api(tags = "App3-Inbound")
@RequestMapping("${app.root.url}" + "/inbound")
public class InboundRequestConroller {
	
	
	
	
	
	@PostMapping("/app2inbound")
	public ResponseEntity<String> App3Inbound(@RequestBody App2Data request){
		
		// envoie à ServiceManager.ussdService

		return ResponseEntity.ok(null);
	}

}
