package blockchain.domain.channel;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class NftInfoSender {
	
	@Qualifier(NftInfoChannel.INPUT)
    private final  SubscribableChannel nftInfoIn;
	@Qualifier(NftInfoChannel.OUTPUT)
    private final MessageChannel nftInfoOut;
    
    public boolean send(String wallet_address) {
        return nftInfoOut.send(MessageBuilder.withPayload(wallet_address).build());
    }
    
}
