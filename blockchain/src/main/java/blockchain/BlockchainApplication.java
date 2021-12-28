package blockchain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import blockchain.service.CreateWalletService;

//JPA Auditing 기능 활성화하여 CreateTime, ModifiedTime를 관리하도록 위임 
@EnableJpaAuditing
@SpringBootApplication
public class BlockchainApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlockchainApplication.class, args);
		
		
		
	}
	

}
