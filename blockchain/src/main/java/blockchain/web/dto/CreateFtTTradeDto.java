package blockchain.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateFtTTradeDto {

	//아이템 받는 사람
	private String to;
		
	//아이템 보내는 사람
	private String sender;
	
	//아이템 가지고 있는 사람 sender와 동일
	private String amount ;
		
	public CreateFtTTradeDto(String to, String sender, String amount) {
		this.to = to ;
		this.sender = sender ;
		this.amount = amount ;
	}
		
}
