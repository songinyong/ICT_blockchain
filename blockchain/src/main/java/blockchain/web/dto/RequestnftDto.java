package blockchain.web.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class RequestnftDto {

	//api요청할때 to 처음 만드는 사람이므로 owner랑 같음
	private String creator;
	private String uri;
	
	private String title;
	private String nft_description;
	
	@Builder
	public RequestnftDto(String creator, String uri,  String title, String nft_description) {
		this.creator = creator;
		this.uri = uri;
		this.title = title;
		this.nft_description = nft_description;
	}
	

}
