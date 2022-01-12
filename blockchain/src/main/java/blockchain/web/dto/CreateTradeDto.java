/*
 * 아이템 소유권 바꾸는 trade할때 사용하는 dto
 * */

package blockchain.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class CreateTradeDto {

	//아이템 받는 사람
	private String to;
	
	//아이템 보내는 사람
	private String sender;
	
	//아이템 가지고 있는 사람 sender와 동일
	private String owner;
	private String token_id;
	
	public CreateTradeDto(String to, String sender, String owner, String token_id) {
		this.to = to ;
		this.sender = sender ;
		this.owner = owner ;
		this.token_id = token_id ;
	}
	
}
