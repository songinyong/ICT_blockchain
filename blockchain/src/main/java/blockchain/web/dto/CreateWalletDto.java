package blockchain.web.dto;

import java.util.Map;

import blockchain.domain.Wallet;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateWalletDto {

	private String address ;
	private String publicKey;
	private String chainId;
	private String createdAt;
	private String updatedAt;
	
	@Builder
	public CreateWalletDto(String address, String publicKey, String chainId, String createdAt, String updatedAt) {
		this.address = address;
		this.publicKey = publicKey;
		this.chainId = chainId;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	
	public Wallet toEntity() {
		
		return Wallet.builder().address(address)
				.chainId(chainId)
				.publicKey(publicKey)
				.createdAt(createdAt)
				.updatedAt(updatedAt)
				.build();
	}
}



	    