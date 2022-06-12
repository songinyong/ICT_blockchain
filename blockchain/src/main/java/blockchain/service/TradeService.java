/*
 * nft 토큰을 거래하는 서비스
 * 
 *  
 * */

package blockchain.service;
import java.util.Optional;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import blockchain.domain.Item;
import blockchain.domain.ItemRepository;
import blockchain.domain.WalletRepository;
import blockchain.domain.log.TradeLog;
import blockchain.domain.log.TradeLogRepository;
import blockchain.web.dto.CreateFtTTradeDto;
import blockchain.web.dto.CreateTradeDto;


@Service
public class TradeService {

	//contract 주소
	@Value("${contract.address}")
	String contract_address ;
	
	//보안 토큰 주소
	@Value("${token.header}")
	String header ;
	
	@Autowired
	WalletRepository wtrepo ;
	
	@Autowired
	ItemRepository itemRepository ;
	
	@Autowired
	TradeLogRepository tradelogRepository;
	
	
	//trade - FT 토큰 거래 시스템 완료되면 private로 변경 -일반 호출 불가
	//거래 로그 저장, 거래후 현재 nft 아이템 상태 테이블에 저장
	@Transactional(rollbackFor = Exception.class)
    public ResponseEntity<JSONObject> createTrade(CreateTradeDto ctdto) {
  
		RestTemplate rt = new RestTemplate();		

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/json");
		headers.set("x-chain-id", "1001");
		headers.set("authorization", header);

		JSONObject createData = new JSONObject();
		createData.put("sender",  ctdto.getSender());
		createData.put("owner", ctdto.getOwner());
		createData.put("to", ctdto.getTo());
		
		 HttpEntity<String> entity = 
			      new HttpEntity<String>(createData.toString(), headers);
		String uri = "https://kip17-api.klaytnapi.com/v1/contract/arttoken/token/" + ctdto.getToken_id();
		JSONObject resultObj = new JSONObject(); 
		
		//잘못된 파라미터 입력하여 실해할시
		try {
		ResponseEntity<JSONObject> result =rt.exchange(uri, HttpMethod.POST,entity, JSONObject.class); 

		//거래 성공했을때
		if( result.getStatusCode().equals(HttpStatus.OK)) {
			//거래 성공시 DB에 업데이트 
			System.out.println(result.getBody().get("transactionHash"));
			Optional<Item> item = itemRepository.findByTokenId(ctdto.getToken_id());
		
			item.get().update(ctdto.getTo(), (String) result.getBody().get("transactionHash"));
			savetradelog(ctdto, (String) result.getBody().get("transactionHash"));
			return new ResponseEntity<JSONObject>(tradeResult("true",ctdto.getToken_id(),ctdto.getSender(),ctdto.getTo(), (String) result.getBody().get("transactionHash")), HttpStatus.ACCEPTED);
		}
		//거래 실패했을때
		else 
		return new ResponseEntity<JSONObject>(tradeResult("false",ctdto.getToken_id(),ctdto.getSender(),ctdto.getTo(),""), HttpStatus.BAD_REQUEST);
		}
		catch ( HttpClientErrorException e) {
			 
			resultObj.put("result","false");
			resultObj.put("reason","bad request");
			return new ResponseEntity<JSONObject>(  resultObj , HttpStatus.BAD_REQUEST);
		}

	}
	
    //거래 성공 200 OK 발생시 tradelog 정보 추가
    private boolean savetradelog(CreateTradeDto ctdto, String hash) {
    	
    	try {
		tradelogRepository.save(TradeLog
    			.builder()
    			.owner(ctdto.getOwner())
    			.sender(ctdto.getSender())
    			.to(ctdto.getTo())
    			.token_id(ctdto.getToken_id())
    			.hash(hash).build());
    	
    	return true ;
    	}
    	catch (Exception e) {
    		return false;
    	}
    }
    
    //FT 토큰 거래 관련
    public ResponseEntity<JSONObject> createFtTrade(CreateFtTTradeDto cftDto) {
		RestTemplate rt = new RestTemplate();		

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/json");
		headers.set("x-chain-id", "1001");
		headers.set("authorization", header);

		JSONObject createData = new JSONObject();
		createData.put("from",  cftDto.getSender());
		createData.put("to", cftDto.getTo());
		createData.put("amount", cftDto.getAmount());
		
		 HttpEntity<String> entity = 
			      new HttpEntity<String>(createData.toString(), headers);
		System.out.println(entity);
		String uri = "https://kip7-api.klaytnapi.com/v1/contract/moonstone/transfer" ;
		
		JSONObject resultObj = new JSONObject();

		try {
			
			ResponseEntity<JSONObject> response = rt.exchange(uri, HttpMethod.POST, entity, JSONObject.class);
			System.out.println(response);
			if( response.getStatusCode().equals(HttpStatus.OK)) {
			
				resultObj.put("result",true);
				resultObj.put("transactionHash",response.getBody().get("transactionHash"));
			
				return new ResponseEntity<JSONObject>(resultObj, HttpStatus.ACCEPTED);
			}
			else {
				resultObj.put("result","false");
				resultObj.put("reason","insufficient balance; amount");
				
				return new ResponseEntity<JSONObject>(resultObj, HttpStatus.BAD_REQUEST);
			}
		}
		catch(HttpClientErrorException e) {
			resultObj.put("result","false");
			resultObj.put("reason","insufficient balance; amount");
			
			return new ResponseEntity<JSONObject>(resultObj, HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			resultObj.put("result","false");
			resultObj.put("reason",e);
			
			return new ResponseEntity<JSONObject>(resultObj, HttpStatus.BAD_REQUEST);
		}
		
    }
    
    
    //거래 결과 
    private JSONObject tradeResult(String result,String token_id,String sender, String to, String hash ) {
		JSONObject resultObj = new JSONObject();  
		resultObj.put("result",result);
		resultObj.put("token_id", token_id);
		resultObj.put("sender",sender);
		resultObj.put("to",to);
		resultObj.put("hash", hash);
		
		return resultObj ;
	}
	//FT 토큰 거래 완료후 trade 거래
}
