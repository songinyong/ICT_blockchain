/*
 * NFT 교환 기록 저장용 엔티티
 * */

package blockchain.domain.log;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import blockchain.domain.CreateTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name="tradelog")
public class TradeLog extends CreateTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id ;
	
	@Column()
	private String token_id;
	@Column()
	private String hash ;
	@Column()
	private String owner ;
	@Column()
	private String receiver ;
	@Column()
	private String sender ;
	
	@CreatedDate
	private LocalDateTime createdDate;
	

	@Builder()
	public TradeLog(String hash, String owner, String to, String sender, String token_id) {
		this.token_id = token_id ;
		this.hash = hash ;
		this.owner = owner ;
		this.receiver = to;
		this.sender = sender ;
	}
	

	
}
