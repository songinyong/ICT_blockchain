/*
 * NFT 아이템 정보를 DB에서 읽어들일때 사용하는 DTO
 * 
 * */

package blockchain.web.dto;

import java.time.LocalDateTime;

import blockchain.domain.Item;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetNftDto {

	
	private String title ;

	private int price;
	private LocalDateTime createdDate ;
	private LocalDateTime modifiedDate ;
	private String nft_description;
	private String nft_hash;
	private String token_id;
	private String creator;
	private String image_path ;
	private String owner;
	
	
	public GetNftDto(Item entity) {
        this.title = entity.getTitle();
        this.price = entity.getPrice();
        this.createdDate = entity.getCreatedDate();
        this.modifiedDate = entity.getModifiedDate();
        this.nft_description = entity.getNft_description();
        this.nft_hash = entity.getNft_hash();
        this.token_id = entity.getToken_id();
        this.creator = entity.getCreator();
        this.image_path = entity.getImage_path();
        this.owner = entity.getOwner();
    
	}
	
}
