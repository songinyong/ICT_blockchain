package blockchain.service;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import blockchain.domain.WalletRepository;
import blockchain.web.dto.CreateWalletDto;

@Service
public class CreateWalletService {

	
	@Value("${token.header}")
	String header ;
	
	@Autowired
	WalletRepository wtrepo ;
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
    
    public JSONObject walletCreateResult(String result,String wallet ) {
		JSONObject resultObj = new JSONObject();
		resultObj.put("result",result);
		resultObj.put("wallet_address",wallet);
		
		return resultObj ;
	}
}
