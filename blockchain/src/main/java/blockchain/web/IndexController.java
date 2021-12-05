package blockchain.web;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;

import blockchain.service.CreateWalletService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class IndexController {
	@Autowired
	CreateWalletService cws ;

	
	@PostMapping("/chain/walletCreate")
	public ResponseEntity<JSONObject> createWallet() {
		return cws.createWallet();
	}
}
