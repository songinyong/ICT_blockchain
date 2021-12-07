package blockchain.service;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/*
 * nft 토큰을 다른 사용자에게 소유권을 이전하는 서비스
 * 
 * */

import blockchain.domain.WalletRepository;
import blockchain.web.dto.CreateNftDto;

@Service
public class CreateTransaction {
	
	@Value("${contract.address}")
	String contract_address ;
	
	@Value("${token.header}")
	String header ;
	
	@Autowired
	WalletRepository wtrepo ;
	
	
	//거래 로그 저장, 거래후 현재 nft 아이템 상태 테이블에 저장
    public ResponseEntity<JSONObject> createTransaction() {
    	System.out.println(contract_address);
    	CreateNftDto nftdto = new CreateNftDto();
		RestTemplate rt = new RestTemplate();
				
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/json");
		headers.set("x-chain-id", "1001");
		headers.set("authorization", header);
		//headers.set("x-krn", "krn:1001:wallet:34f7b5b9-8caa-4e25-8057-2302ccf0607f:account-pool:test");
		//params.add("to", "0xa39c61e989bD868F1B0B7E398375E86EC9948B58");
		params.add("sender", "0xbc7cc9517400cff0ec953efb585e424301a395b0");
		params.add("owner", "0xbc7cc9517400cff0ec953efb585e424301a395b0");
		params.add("to", "0x4f7045C8F7959c82AFd5BE9feFeC98bb80622849");
		
		System.out.println(params.values());
		HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(params, headers); 
		String uri = "https://kip17-api.klaytnapi.com/v1/contract/arttoken/token/0x1";
		System.out.println(uri);
		ResponseEntity<JSONObject> result =rt.exchange(uri, HttpMethod.POST,entity, JSONObject.class);
		System.out.println(result);
		
		return new ResponseEntity<JSONObject>(walletCreateResult("true", "test"), HttpStatus.ACCEPTED);
		
			}
    
    public JSONObject walletCreateResult(String result,String wallet ) {
		JSONObject resultObj = new JSONObject();  
		resultObj.put("result",result);
		resultObj.put("wallet_address",wallet);
		
		return resultObj ;
	}

}
