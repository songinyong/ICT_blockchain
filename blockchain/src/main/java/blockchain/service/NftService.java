/*
 * nft 토큰을 발급하고 각 계정들의 가지고 있는 토큰이나 토큰 정보를 알려주는 서비스
 * 
 *  
 * */

package blockchain.service;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import blockchain.domain.ItemRepository;
import blockchain.domain.WalletRepository;
import blockchain.web.dto.CheckNftDto;
import blockchain.web.dto.CreateNftDto;

@Service
public class NftService {

	//contract 주소
	@Value("${contract.address}")
	String contract_address ;
	
	@Value("${token.header}")
	String header ;
	
	@Autowired
	WalletRepository wtrepo ;
	
	@Autowired
	ItemRepository irepo;
	
	//nft 토큰을 발급해주는 서비스
	//creator는 처음 지갑 계정으로 들어가며 createditem table에 저장된다. 
    public ResponseEntity<JSONObject> createNftbyapi() {
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
		params.add("id", String.valueOf("0x"+Long.toHexString(irepo.getMaxTransactionId()+2)));
		System.out.println("0x"+Long.toHexString(irepo.getMaxTransactionId()+10));
		params.add("uri", "/post/1");
		System.out.println(params.values());
		
		HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(params, headers); 
		String uri = "https://kip17-api.klaytnapi.com/v1/contract/"+"arttoken"+"/token";
		System.out.println(uri);
		ResponseEntity<JSONObject> result =rt.exchange(uri, HttpMethod.POST,entity, JSONObject.class);
		System.out.println(result);
		
		return new ResponseEntity<JSONObject>(walletCreateResult("true", "test"), HttpStatus.ACCEPTED);
		
			}
    
    //사용자 지갑에 가지고 있는 토큰들 알려주는 서비스
    public ResponseEntity<JSONObject> getNftInfo(String wallet) {
    	
    	RestTemplate rt = new RestTemplate();
		
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/json");
		headers.set("x-chain-id", "1001");
		headers.set("authorization", header);
		
		HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(params, headers); 
		String uri = "https://kip17-api.klaytnapi.com/v1/contract/arttoken/owner/"+wallet;
		System.out.println(uri);
		ResponseEntity<JSONObject> result =rt.exchange(uri, HttpMethod.GET,entity, JSONObject.class);
		System.out.println(result.getBody().get("items"));
    	List test = (List) result.getBody().get("items") ;
    	System.out.println(test.get(0));
    	return new ResponseEntity<JSONObject>(getNftInfoResult("true",result.getBody().get("items")), HttpStatus.ACCEPTED);
    }
    
    //DB에 누락된 아이템 정보 있나 확인하고 있을시 db에 추가
    /*JSONObject는 리스트가 포함된 json 파싱이 어려워서 JsonNode로 대체하였음*/
    public  ResponseEntity<JSONObject> interdb() throws ParseException {
    	ResponseEntity<JsonNode> response = checknftlist();
    	//JSONParser jsonParser = new JSONParser(); 
    	//JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody().get("items").toString()); 
    	ObjectMapper mapper = new ObjectMapper();

    	JsonNode items =  response.getBody().get("items");
    	List<CheckNftDto> accountList = mapper.convertValue(
    		    items, 
    		    new TypeReference<List<CheckNftDto>>(){}
    		);
    	
    	for(int i =0; i<accountList.size(); i++) {
    		if(irepo.checkID(accountList.get(i).getTokenId()).isEmpty()) {
    	        saveNftdb(accountList.get(i));  	

    		}
    	}
    	

	       return new ResponseEntity<JSONObject>(getNftInfoResult("true","clear"), HttpStatus.CONFLICT);
    }
    
    public ResponseEntity<JSONObject> interdbbywallet(String wallet_address) throws ParseException {
    	System.out.println(irepo.findByWallet(wallet_address));
    	

	       return new ResponseEntity<JSONObject>(getNftInfoResult("true",irepo.findByWallet(wallet_address)), HttpStatus.CREATED);
    }
    
    //컨트랙트에 있는 모든 nft 정보를 읽어온다.
    private ResponseEntity<JsonNode> checknftlist() {
    	RestTemplate rt = new RestTemplate();
		
    	HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/json;charset=utf-8");
		headers.set("authorization", header);
		headers.set("x-chain-id", "1001");
		HttpEntity request = new HttpEntity(headers);

		//ResponseEntity<JSONObject> response = rt.exchange("https://kip17-api.klaytnapi.com/v1/contract/arttoken", HttpMethod.GET, request, JSONObject.class );
		//ResponseEntity<List<CheckNftDto>> response = rt.exchange("https://kip17-api.klaytnapi.com/v1/contract/arttoken/token", HttpMethod.GET, request, new ParameterizedTypeReference<List<CheckNftDto>>() {});
		ResponseEntity<JsonNode> response = rt.exchange("https://kip17-api.klaytnapi.com/v1/contract/arttoken/token", HttpMethod.GET, request, JsonNode.class);
		
		return response ;
    }
    
    //nft 정보 db에 저장
    private boolean saveNftdb(CheckNftDto chkdto) {
    	
    	try {
    		
    	
    	CreateNftDto cndto = new CreateNftDto();
    	cndto.setToken_id(chkdto.getTokenId());
    	cndto.setNft_hash(chkdto.getTransactionHash());
    	cndto.setOwner(chkdto.getOwner());
    	cndto.setCreator(chkdto.getOwner());
    	cndto.setImage_path(chkdto.getTokenUri());
    	cndto.setNft_description("");
    	cndto.setPrice(0);
    	cndto.setTitle("test");
    	cndto.setSell_state(0);
    	
    	irepo.save(cndto.toEntity());
    	
    	return true ;
    	}
    	catch(Exception e) {
    	return false ;
    	}
    }
    
    
    
    public JSONObject walletCreateResult(String result,String wallet ) {
		JSONObject resultObj = new JSONObject();  
		resultObj.put("result",result);
		resultObj.put("wallet_address",wallet);
		
		return resultObj ;
	}
    
    public JSONObject getNftInfoResult(String result, Object items) {
    	JSONObject resultObj = new JSONObject();  
		resultObj.put("result",result);
		resultObj.put("items", items);
		return resultObj ;
    }
    
    private JSONObject createnftResult(String result) {
    	JSONObject resultObj = new JSONObject();  
		resultObj.put("result",result);
		return resultObj;
    }
}
