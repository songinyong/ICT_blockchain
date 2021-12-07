package blockchain.web;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import blockchain.service.CreateTransaction;
import blockchain.service.CreateWalletService;
import blockchain.service.NftService;
import blockchain.web.dto.SearchByWallet;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class IndexController {
	@Autowired
	CreateWalletService cws ;

	@Autowired
	NftService nfts;
	
	@Autowired
	CreateTransaction ctran;
	
	@Value("${token.header}")
	String header ;
	
	//api를 호출하면 지갑 주소를 생성한다.
	@PostMapping("/chain/walletCreate")
	public ResponseEntity<JSONObject> createWallet() {
		return cws.createWallet();
	}
	//특정 사용자 소유의 nft 토큰을 만드는 서비스 
	@PostMapping("/chain/nftCreate")
	public ResponseEntity<JSONObject> createNft() {
		return nfts.createNftbyapi();
	}
	//컨트랙트 내에 있는 토큰 목록들을 불러온다.
	@PostMapping("/chain/test")
	public ResponseEntity<JSONObject> createtest() throws ParseException {


		return nfts.interdb() ;
	}
	
	@PostMapping("/chain/findnft")
	public ResponseEntity<JSONObject> findnftbywalletaddress(@RequestParam("address") String wallet) throws ParseException {


		return nfts.interdbbywallet(wallet) ;
	}
	
	//특정 사용자의 지갑 주소를 기준으로 사용자 소유의 nft 토큰들을 불러온다.
	//request parameter 형식으로 받아오는데 카프카 적용할떄 같이 수정할 부분
	@PostMapping("/chain/getNftInfo")
	public ResponseEntity<JSONObject> createTransaction(@RequestParam("address") String wallet) {
		return nfts.getNftInfo(wallet);
	}
}
