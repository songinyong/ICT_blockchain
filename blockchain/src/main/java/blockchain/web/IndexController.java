package blockchain.web;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import blockchain.domain.channel.NftInfoChannel;
import blockchain.service.ExchangeService;
import blockchain.service.NftService;
import blockchain.service.TradeService;
import blockchain.service.WalletService;
import blockchain.web.dto.CreateFtTTradeDto;
import blockchain.web.dto.CreateTradeDto;
import blockchain.web.dto.GetNftIdDto;
import blockchain.web.dto.RequestnftDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
@EnableBinding(Processor.class)
public class IndexController {
	@Autowired
	WalletService wls ;

	@Autowired
	NftService nfts;
	
	@Autowired
	TradeService tradeservice;
	
	@Autowired
	ExchangeService exchangeService;
	
	@Value("${token.header}")
	String header ;
	
	//api를 호출하면 지갑 주소를 생성한다.
	@PostMapping("/chain/walletCreate")
	public ResponseEntity<JSONObject> createWallet() {
		return wls.createWallet();
	}
	//특정 사용자 소유의 nft 토큰을 만드는 서비스 
	@PostMapping("/chain/nftCreate")
	public ResponseEntity<JSONObject> createNft(@RequestBody RequestnftDto requestnftDto) {
		return nfts.createNftbyapi(requestnftDto);
	}
	//컨트랙트 내에 있는 토큰 목록들을 불러오고 아이디가 없을시 블록체인 DB에 아이템 정보 저장
	@PostMapping("/chain/nfts")
	public ResponseEntity<JSONObject> createtest() throws ParseException {


		return nfts.interdb() ;
	}
	
	//DB에서 지갑 주소 기준 nft 아이템을 불러온다
	@PostMapping("/chain/findnft")
	public ResponseEntity<JSONObject> findnftbywalletaddress(@RequestParam("address") String wallet) throws ParseException {


		return nfts.interdbbywallet(wallet) ;
	}
	

	//DB에 저장된 모든 아이템 정보를 불러온다.
	@GetMapping("/chain/findAllnfts")
	public ResponseEntity<JSONObject> findAllnft() throws ParseException {


		return nfts.transferNftlist() ;
	}
	
	//아이템 소유권 이전
	@PutMapping("/chain/trade")
	public ResponseEntity<JSONObject> tradenft(@RequestBody CreateTradeDto ctdto) {
		
		return tradeservice.createTrade(ctdto);
	}

	//token_id로 아이템의 거래 기록 조회
	@GetMapping("/chain/tradeinfo")
	public ResponseEntity<JSONObject> getNftTradeInfo(@RequestParam("tokenId") String token_id) throws ParseException {

		return nfts.getNftTradeInfo(token_id) ;
	}

	//FT 잔액 조회
	@GetMapping("/chain/numOfFt")
	public ResponseEntity<JSONObject> getNumOfFt(@RequestParam("address") String wallet) throws ParseException {

		return wls.getNumOfFt(wallet) ;
	}
	
	//token_id로 contract 정보 조회
	@PostMapping("/chain/contractInfo")
	public ResponseEntity<JSONObject> getContractInfo(@RequestBody GetNftIdDto getNftIdDto) throws ParseException {

		return wls.getContractInfo(getNftIdDto) ;
	}
	
	//FT token 교환
	@PostMapping("/chain/ftTrade")
	public ResponseEntity<JSONObject> createFtTrade(@RequestBody CreateFtTTradeDto createFtTradeDto) throws ParseException {

		return tradeservice.createFtTrade(createFtTradeDto) ;
	}
	
	
	@PostMapping("/test")
	public ResponseEntity<JSONObject> test() throws ParseException {
		exchangeService.test();
		return nfts.testMultiValueMap() ;
	}
	
	//컨트렉트에서 특정 사용자의 지갑 주소를 기준으로 사용자 소유의 nft 토큰들을 불러온다.
	//request parameter 형식으로 받아오는데 카프카 적용할떄 같이 수정할 부분
	@PostMapping("/chain/getNftInfo")
	public ResponseEntity<JSONObject> createTransaction(@RequestParam("address") String wallet) {
		return nfts.getNftInfo(wallet);
	}
	
	@GetMapping("/test2")
	public void test2() {
		exchangeService.test();
	}
	
    @StreamListener( NftInfoChannel.INPUT)
    public void test(String wallet_address) throws ParseException {
    	System.out.println(wallet_address);
    	
    }
}
