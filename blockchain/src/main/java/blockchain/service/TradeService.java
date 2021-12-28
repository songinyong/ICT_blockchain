/*
 * nft 토큰을 거래하는 서비스
 * 
 *  
 * */

package blockchain.service;

import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class TradeService {

	//contract 주소
	@Value("${contract.address}")
	String contract_address ;
	
	//보안 토큰 주소
	@Value("${token.header}")
	String header ;
	

}
