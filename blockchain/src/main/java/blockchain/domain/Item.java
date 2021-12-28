package blockchain.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name="item")
public class Item extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id ;
	
	@Column()
	private String nft_description;
	@Column()
	private String nft_hash;
	
	//아이템 아이디를 16진수롤 변환해서 저장
	@Column()
	private String token_id ;
	@Column()
	private String title;
	@Column()
	private String creator;
	@Column()
	private String image_path;
	@Column()
	private String owner;
	@Column()
	private int price;
	
	@CreatedDate
	private LocalDateTime createdDate;
	
	@LastModifiedDate
	private LocalDateTime modifiedDate;
	

	
	@Builder()
	public Item(String nft_description, String nft_hash, String token_id, String title, String creator, String image_path, String owner, int price) {
		this.nft_description = nft_description;
		this.nft_hash = nft_hash;
		this.token_id = token_id;
		this.title = title;
		this.creator = creator;
		this.image_path = image_path;
		this.owner = owner;
		this.price = price;

	}
	
}
