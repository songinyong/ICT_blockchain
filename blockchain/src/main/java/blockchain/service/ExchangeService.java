/*
 * klaytn과 Ft 토큰의 교환을 담당하는 서비스
 * 
 * */

package blockchain.service;



import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.http.HttpService;

import com.klaytn.caver.Caver;
import com.klaytn.caver.methods.response.Addresses;
import com.klaytn.caver.methods.response.Quantity;

import okhttp3.Credentials;


@Service
public class ExchangeService {

	//보안 토큰 주소
	@Value("${token.header}")
	String header ;
	
	//klaytn 체인에서 정보를 불러오는 공통 메서드
	
	//klaytn을 ft로 환전해주는 서비스
	
	//ft를 klaytn으로 환전해주는 서비스
	
	//klaytn 잔액 조회
	
	public void test()  {

		
		HttpService httpService = new HttpService("https://node-api.klaytnapi.com/v1/klaytn");
		
		String auth = Credentials.basic("KASK702UW6L93F7UD189IWV1", "MA7MyIh7VuQ7h3-hALiTGn2I489T5hQZRwfSQCsa", StandardCharsets.UTF_8);
		System.out.println(httpService);
		
		httpService.addHeader("Authorization", auth);
		httpService.addHeader("x-chain-id", "1001");

		
		Caver caver = Caver.build(httpService);
		
		
		//Request<?, Quantity> d = caver.klay().getBalance("0xbc7cc9517400cff0ec953efb585e424301a395b0", DefaultBlockParameter.valueOf("latest"));
		Request<?, Quantity> d = caver.rpc.getKlay().getBalance("0xbc7cc9517400cff0ec953efb585e424301a395b0");


}

}
