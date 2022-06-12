/*
 * nft 아이템 만들때 사용하는 dto
 * */

package blockchain.web.dto;

import java.time.LocalDateTime;

import javax.persistence.Column;

import org.springframework.data.annotation.CreatedDate;

import blockchain.domain.Item;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class CreateNftDto {

	private String nft_description;
	private String nft_hash;
	
	//아이템 아이디를 16진수롤 변환해서 저장
	private String token_id ;
	private String token_name;
	private String title;
	private String creator;
	private String image_path;
	private String owner;

	@Builder()
	public CreateNftDto(String nft_description, String nft_hash, String token_id, String tokenName, String title, String creator, String image_path, String owner) {
		this.nft_description = nft_description;
		this.nft_hash = nft_hash;
		this.token_id = token_id;
		this.token_name = tokenName;
		this.title = title;
		this.creator = creator;
		this.image_path = image_path;
		this.owner = owner;	
	}
	
	public Item toEntity() {
		
		return Item.builder().nft_description(nft_description)
				.nft_hash(nft_hash)
				.token_id(token_id)
				.title(title)
				.creator(creator)
				.image_path(image_path)
				.owner(owner)
				.token_name("arttoken")
				.build();
	}
}
