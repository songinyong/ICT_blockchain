/*
 * nft 토큰을 발급하고 각 계정들의 가지고 있는 토큰이나 토큰 정보를 알려주는 서비스
 * 
 * */

package blockchain.service;

import java.util.List;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import blockchain.web.dto.GetNftDto;
import blockchain.web.dto.RequestnftDto;

@Service
public class NftService {

	//contract 주소
	@Value("${contract.address}")
	String contract_address ;
	
	//보안 토큰 주소
	@Value("${token.header}")
	String header ;
	
	@Autowired
	WalletRepository wtrepo ;
	
	@Autowired
	ItemRepository irepo;
	
	/*nft 토큰을 발급해주는 서비스
	creator는 처음 지갑 계정으로 들어가며 createditem table에 저장된다. 
	- 서비 이상으로 일부 데이터 누락을 대비해서 nft 등록 프로세스를 2중으로 변경 에정
	사용자로부터 받은 정보를 우선 DB에저장 -> nft 아이템 등록후 나온 hash값들을 저장 트랜잭션 단위로 묶어서 등록 안되면 롤백 기능 제공  
	*/
    public ResponseEntity<JSONObject> createNftbyapi(RequestnftDto requestnftDto) {
    	
    	CreateNftDto nftdto = new CreateNftDto();
		RestTemplate rt = new RestTemplate();
				
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/json");
		headers.set("x-chain-id", "1001");
		headers.set("authorization", header);
	
		JSONObject createData = new JSONObject();

		createData.put("to", "0xa39c61e989bD868F1B0B7E398375E86EC9948B58");
		createData.put("id", String.valueOf("0x"+Long.toHexString(irepo.getMaxTransactionId()+7)));
		createData.put("uri", "https://cdna.artstation.com/p/assets/images/images/043/253/704/4k/romain-lebouleux-geothermal-plant-view1.jpg?1636732465");

		//nft아이템을 16진수로 바꾸어 저장한다.
		String token_id = String.valueOf("0x"+Long.toHexString(irepo.getMaxTransactionId()+7));
		createData.put("to", requestnftDto.getCreator());
		createData.put("id", token_id);
		createData.put("uri", requestnftDto.getUri());
		

		 HttpEntity<String> entity = 
			      new HttpEntity<String>(createData.toString(), headers);
		
		String uri = "https://kip17-api.klaytnapi.com/v1/contract/"+"arttoken"+"/token";
		
		ResponseEntity<JSONObject> result =rt.exchange(uri, HttpMethod.POST, entity, JSONObject.class);
		
		//생성된 nft 정보 DB에 저장하는 부분
		nftdto.setCreator(requestnftDto.getCreator());
		nftdto.setImage_path(requestnftDto.getUri());
		nftdto.setToken_id(token_id);
		nftdto.setOwner(requestnftDto.getCreator());
		nftdto.setTitle(requestnftDto.getTitle());
		nftdto.setNft_description(requestnftDto.getNft_description());
		nftdto.setNft_hash((String) result.getBody().get("transactionHash"));
		saveNftdb(nftdto);

		//결과 엔티티 상속 관계로 바꿔야함
		return new ResponseEntity<JSONObject>(walletCreateResult("true", "test"), HttpStatus.ACCEPTED);
		
			}
    
    //각 사용자 지갑에 소유하고 있는 토큰들의 정보를 알려주는 서비스
    public ResponseEntity<JSONObject> getNftInfo(String wallet) {
    	
    	RestTemplate rt = new RestTemplate();
		
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/json");
		headers.set("x-chain-id", "1001");
		headers.set("authorization", header);
		
		HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(params, headers); 
		String uri = "https://kip17-api.klaytnapi.com/v1/contract/arttoken/owner/"+wallet;
		
		ResponseEntity<JSONObject> result =rt.exchange(uri, HttpMethod.GET,entity, JSONObject.class);
		
    	List test = (List) result.getBody().get("items") ;
    	
    	return new ResponseEntity<JSONObject>(getNftInfoResult("true",result.getBody().get("items")), HttpStatus.ACCEPTED);
    }
    
    //DB에 누락된 아이템 정보 있나 확인하고 있을시 db에 추가

    /* 컨트랙트와 블록체인DB와 동기화하기 위한 메서드 - 컨트랙트에 저장된 아이템 정보중 db에 없는 아이템이면 DB에 저장된다.
     * - 일부 데이터 누락됬을때의 문제 -> 아이템 등록 과정을 단계별로 나누어 해결할 에정
     * JSONObject는 리스트가 포함된 json 파싱이 어려워서 JsonNode로 대체하였음
     * ->JSON ARRAY로 대체할 가능성 남아있음
     * */
    /*JSONObject는 리스트가 포함된 json 파싱이 어려워서 JsonNode로 대체하였음*/

    
    public  ResponseEntity<JSONObject> interdb() throws ParseException {
    	
    	try {
    	ResponseEntity<JsonNode> response = checknftlist();

    	ObjectMapper mapper = new ObjectMapper();

    	JsonNode items =  response.getBody().get("items");
    	List<CheckNftDto> accountList = mapper.convertValue(
    		    items, new TypeReference<List<CheckNftDto>>(){}
    		);
    	
    	//-stream으로 교체 예정 (작업완료되면 삭제)
    	//db에 토큰 ID 확인해서 없는 토큰 아이디는 db에 저장 
    	for(int i =0; i<accountList.size(); i++) {
    		if(irepo.checkID(accountList.get(i).getTokenId()).isEmpty()) {
    	        saveNftdb(accountList.get(i));  	

    		}
    	}
    	

	       return new ResponseEntity<JSONObject>(getNftInfoResult("true","clear"), HttpStatus.CONFLICT);
	       
    	} catch (Exception e) {
    		 return new ResponseEntity<JSONObject>(getNftInfoResult("false","clear"), HttpStatus.CONFLICT);
    	}
    }
    
    //
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
    

    // 게시물 아이템 DB와 블록체인 아이템DB를 연동하기 위한 메서드 - 현재 DB에 저장된 아이템 정보를 전송한다.
    public ResponseEntity<JSONObject> transferNftlist() {
    	RestTemplate rt = new RestTemplate();
		
    	HttpHeaders headers = new HttpHeaders();

    	
		HttpEntity request = new HttpEntity(headers);

		//ResponseEntity<JSONObject> response = rt.exchange("https://kip17-api.klaytnapi.com/v1/contract/arttoken", HttpMethod.GET, request, JSONObject.class );
		//ResponseEntity<List<CheckNftDto>> response = rt.exchange("https://kip17-api.klaytnapi.com/v1/contract/arttoken/token", HttpMethod.GET, request, new ParameterizedTypeReference<List<CheckNftDto>>() {});
		
		List<GetNftDto> itemList = findAllItem();
		System.out.println(itemList.size());
		System.out.println(itemList.get(0));
		JSONObject requestData = new JSONObject();
		requestData.put("items", itemList);
			
		
		return new ResponseEntity<JSONObject>(requestData, HttpStatus.CREATED);
    }
    
    /*DB상에 있는 모든 아이템들을 불러온다*/
    @Transactional(readOnly = true)
    private List<GetNftDto> findAllItem() {
    	
        return irepo.findAllItems().stream()
                .map(GetNftDto::new)
                .collect(Collectors.toList());
    }
    
    //nft 정보 db에 저장 - 거래이후 수정된 정보 아이템 테이블에 저장
    private boolean saveNftdb(CheckNftDto chkdto) {
    	
    	try {
  
    	CreateNftDto cndto = new CreateNftDto();
    	cndto.setToken_id(chkdto.getTokenId());
    	cndto.setNft_hash(chkdto.getTransactionHash());
    	cndto.setOwner(chkdto.getOwner());
    	cndto.setCreator(chkdto.getOwner());
    	cndto.setImage_path(chkdto.getTokenUri());
    	cndto.setNft_description("");
    	cndto.setTitle("test");
    	
    	irepo.save(cndto.toEntity());
    	
    	return true ;
    	}
    	catch(Exception e) {
    	return false ;
    	}
    }
    
  /*nft 정보 db에 저장 - 아이템 등록후 생성된 정보 아이템 테이블에 저장 
   * */
    private boolean saveNftdb(CreateNftDto cftdto) {
    	
    	try {
    	irepo.save(cftdto.toEntity());
    	
    	return true ;
    	}
    	catch(Exception e) {
    		
    	return false ;
    	}
    }
    
    /*토큰 거래 히스토리 조회 */
    public ResponseEntity<JSONObject> getNftTradeInfo(String token_id) {
    	RestTemplate rt = new RestTemplate();
		
    	HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/json;charset=utf-8");
		headers.set("authorization", header);
		headers.set("x-chain-id", "1001");
		HttpEntity request = new HttpEntity(headers);
		JSONObject resultObj = new JSONObject();
		try {
			ResponseEntity<JsonNode> response = rt.exchange("https://kip17-api.klaytnapi.com/v1/contract/arttoken/token/"+ token_id  +"/history", HttpMethod.GET, request, JsonNode.class);
			  
			resultObj.put("result","true");
			resultObj.put("items",response.getBody().get("items"));
			
			return new ResponseEntity<JSONObject>(resultObj, HttpStatus.ACCEPTED);
		}
		catch(Exception e) {
			resultObj.put("result","true");
			resultObj.put("reason",e);
			
			return new ResponseEntity<JSONObject>(resultObj, HttpStatus.ACCEPTED);
		}

    }
    
    /*파라미터 테스트용 삭제할꺼임*/
    public ResponseEntity<JSONObject> testMultiValueMap() {
    	RestTemplate rt = new RestTemplate();
		
    	HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/json;charset=utf-8");
		headers.set("authorization", header);
		headers.set("x-chain-id", "1001");
		HttpEntity request = new HttpEntity(headers);

		ResponseEntity<JSONObject> response = rt.exchange("https://kip17-api.klaytnapi.com/v1/contract/arttoken/token", HttpMethod.GET, request, JSONObject.class);
    	
		JSONArray items =  (JSONArray) response.getBody().get("items");
		System.out.println(items);
		
		return response ;
    	
    }
    
    /* 실행 결과를 JSON 형태로 만드는 메서드
    /*
     * 결과 메서드들은 후에 리팩토링 진행
     * */
    private JSONObject walletCreateResult(String result,String wallet ) {
		JSONObject resultObj = new JSONObject();  
		resultObj.put("result",result);
		resultObj.put("wallet_address",wallet);
		
		return resultObj ;
	}
    
    private JSONObject getNftInfoResult(String result, Object items) {
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
