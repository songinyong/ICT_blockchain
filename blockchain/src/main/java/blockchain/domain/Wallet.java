/*
 * wallet 테이블과 매핑되는 wallet 엔티팉
 * */

package blockchain.domain;


import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;




import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name="wallet")
public class Wallet  {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id ;
	
	@Column()
	private String address;
	
	@Column(name="publickey",nullable = false)
	private String publicKey ;
	
	@Column(name="chainid" )
	private String chainId;
		
	@Column()
	private String createdAt;
	
	
	@Column()
	private String updatedAt;
	
	
	@Builder
	public Wallet(String address, String publicKey, String chainId, String createdAt, String updatedAt) {
		this.address = address ;
		this.publicKey = publicKey ;
		this.chainId = chainId;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		
	}
	
}
