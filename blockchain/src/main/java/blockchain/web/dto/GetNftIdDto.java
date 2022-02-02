/*
 * API 호출할때 토큰 ID만 별도로 필요한 경우가 많아 토큰 ID만 받아올떄 사용하는 Dto
 * 
 * 보안을 위해 각 요청마다 지갑 주소를 함께 받는데,  jwt 토큰에 들어 있는 wallet과 비교하여 토큰의 생성자이면서 소유주임을 추가로 증명하기 위해서이다.
 * */
package blockchain.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetNftIdDto {
	
	private String token_id;
	private String owner;
	
	public GetNftIdDto(String token_id, String owner) {
		this.token_id = token_id ;
		this.owner = owner ;
	}


}
