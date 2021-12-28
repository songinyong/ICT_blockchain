package blockchain.web.dto;

import blockchain.domain.Item;
import lombok.Builder;

public class SyncNftDto {


	private String nft_hash;
	
	//아이템 아이디를 16진수롤 변환해서 저장
	private String token_id ;
	private String image_path;
	private String owner;


	
	@Builder()
	public SyncNftDto(String nft_hash, String token_id, String title, String image_path, String owner, int price) {

		this.nft_hash = nft_hash;
		this.token_id = token_id;
		this.image_path = image_path;
		this.owner = owner;

	}
	
	public Item toEntity() {
		
		return Item.builder()
				.nft_hash(nft_hash)
				.token_id(token_id)
				.image_path(image_path)
				.owner(owner)
				.build();
	}
}
