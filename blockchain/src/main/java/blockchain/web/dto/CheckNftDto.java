package blockchain.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckNftDto {
		private String createdAt;
	    private String owner;
	    private String previousOwner;
	    private String tokenId;
	    private String tokenName;
	    private String tokenUri;
	    private String transactionHash;
	    private String updatedAt;
	    
	    @Builder
	    public CheckNftDto(String createdAt, String owner, String previousOwner, String tokenId, String tokenName , String tokenUri, String transactionHash, String updatedAt  ) {
	    	this.createdAt = createdAt ;
	    	this.owner = owner;
	    	this.previousOwner = previousOwner;
	    	this.tokenId = tokenId;
	    	this.tokenName = tokenName;
	    	this.tokenUri = tokenUri;
	    	this.transactionHash = transactionHash;
	    	this.updatedAt = updatedAt;
	    }
	}
