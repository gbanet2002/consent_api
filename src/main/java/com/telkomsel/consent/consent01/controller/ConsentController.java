package com.telkomsel.consent.consent01.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.telkomsel.consent.consent01.model.Consent;


@Controller
public class ConsentController {
	@Autowired
	Consent cn;

	
	@RequestMapping(value="/Consent-API", method = RequestMethod.GET)
	@ResponseBody
	public String APICONSENT(@RequestParam String username, @RequestParam String password, @RequestParam String msisdn, @RequestParam String product_code, @RequestParam String customer_name, @RequestParam String consent_id, @RequestParam String consent_channel ) {
		String resp = cn.consent(username, password, msisdn, product_code, customer_name, consent_id, consent_channel);
		return resp;
	}
}
