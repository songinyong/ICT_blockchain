/*
 * 지갑을 생성해주는 서비스
 * login 서비스가 이 서비스를 호출하여 지갑을 생성한후 DB에 저장한다.
 * 
 * 후에 카프카랑 연동해 처리할 예정
 * 
 * 최종작성일 21.12.06
 * */
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

import com.fasterxml.jackson.databind.JsonNode;

import blockchain.domain.ItemRepository;
import blockchain.domain.WalletRepository;
import blockchain.web.dto.CreateWalletDto;
import blockchain.web.dto.GetNftIdDto;

@Service
public class WalletService {

	@Value("${token.header}")
	String header ;
	
	@Autowired
	WalletRepository wtrepo ;
	
	@Autowired
	ItemRepository irepo;

	//사용자 지갑 생성
    public ResponseEntity<JSONObject> createWallet() {
    	
    	CreateWalletDto cwdto = new CreateWalletDto();
		RestTemplate rt = new RestTemplate();
		//ResponseEntity<String> response = rt.getForEntity("https://wallet-api.klaytnapi.com/v2/account", String.class);
				
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/json;charset=utf-8");
		headers.add("authorization", header);
		headers.add("x-chain-id", "1001");

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers); 
		JSONObject result =rt.postForObject("https://wallet-api.klaytnapi.com/v2/account", entity, JSONObject.class);
		System.out.println(result);
		cwdto.setAddress((String)result.get("address"));
		cwdto.setChainId(String.valueOf(result.get("chainId")));
		cwdto.setPublicKey((String)result.get("publicKey"));
		cwdto.setCreatedAt(String.valueOf(result.get("createdAt")));
		cwdto.setUpdatedAt(String.valueOf(result.get("updatedAt")));
		wtrepo.save(cwdto.toEntity());
		return new ResponseEntity<JSONObject>(walletCreateResult("true", (String)result.get("address")), HttpStatus.ACCEPTED);
		
			}
    
    //사용자 지갑 기준 klaytn 갯수 조회
    
    //사용자 지갑 기준 token_id가 속한 컨트랙트 정보 조회
    public ResponseEntity<JSONObject> getContractInfo(GetNftIdDto getNftIdDto ) {
    	RestTemplate rt = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/json;charset=utf-8");
		headers.add("authorization", header);
		headers.add("x-chain-id", "1001");
		
		HttpEntity request = new HttpEntity(headers);
		JSONObject resultObj = new JSONObject();
		try {
			
			if(irepo.findByTokenId(getNftIdDto.getToken_id()).isPresent()) {
				String token_alias = irepo.findByTokenId(getNftIdDto.getToken_id()).get().getToken_name();
				System.out.println(token_alias);
				ResponseEntity<JsonNode> response = rt.exchange("https://kip17-api.klaytnapi.com/v1/contract/"+ token_alias , HttpMethod.GET, request, JsonNode.class);
			  
				resultObj.put("result","true");
				resultObj.put("address",response.getBody().get("address"));
				resultObj.put("alias",response.getBody().get("alias"));
				resultObj.put("name",response.getBody().get("name"));
				resultObj.put("symbol",response.getBody().get("symbol"));
			
				return new ResponseEntity<JSONObject>(resultObj, HttpStatus.ACCEPTED);
			}
			else {
				resultObj.put("result","false");
				resultObj.put("reason","ID 조회되지 않음");
				
				return new ResponseEntity<JSONObject>(resultObj, HttpStatus.BAD_REQUEST);
					
			}
			
		}
		catch(Exception e) {
			resultObj.put("result","false");
			resultObj.put("reason",e);
			
			return new ResponseEntity<JSONObject>(resultObj, HttpStatus.BAD_REQUEST);
		}
		
		
    }
    
    
    //사용자 기준 기준 FT 갯수 조회
    public ResponseEntity<JSONObject> getNumOfFt(String wallet) {
    	RestTemplate rt = new RestTemplate();
		
    	HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/json;charset=utf-8");
		headers.set("authorization", header);
		headers.set("x-chain-id", "1001");
		HttpEntity request = new HttpEntity(headers);
		JSONObject resultObj = new JSONObject();
		try {
			ResponseEntity<JsonNode> response = rt.exchange("https://kip7-api.klaytnapi.com/v1/contract/moonstone/account/"+wallet+"/balance", HttpMethod.GET, request, JsonNode.class);
			  
			resultObj.put("result","true");
			resultObj.put("balance",response.getBody().get("balance"));
			resultObj.put("decimals",response.getBody().get("decimals"));
			
			return new ResponseEntity<JSONObject>(resultObj, HttpStatus.ACCEPTED);
		}
		catch(Exception e) {
			resultObj.put("result","false");
			resultObj.put("reason",e);
			
			return new ResponseEntity<JSONObject>(resultObj, HttpStatus.BAD_REQUEST);
		}

    }
    
    
    private JSONObject walletCreateResult(String result,String wallet ) {
		JSONObject resultObj = new JSONObject();
		resultObj.put("result",result);
		resultObj.put("wallet_address",wallet);
		
		return resultObj ;
	}
    
    //지갑 기준으로 가지고 있는 
}
