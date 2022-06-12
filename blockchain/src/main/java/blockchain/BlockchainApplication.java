package blockchain;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import blockchain.domain.channel.NftInfoChannel;
import blockchain.domain.channel.NftInfoSender;


//JPA Auditing 기능 활성화하여 CreateTime, ModifiedTime를 관리하도록 위임 
@EnableJpaAuditing
@SpringBootApplication
@EnableBinding(NftInfoChannel.class)
public class BlockchainApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlockchainApplication.class, args);
			
	}
	

	
}
