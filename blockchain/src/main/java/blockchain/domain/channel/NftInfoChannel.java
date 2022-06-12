/*rabbitMq를 사용하기 위해 구현하는 채널*/

package blockchain.domain.channel;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;


public interface NftInfoChannel {
	String INPUT = "nftInfoIn";
    String OUTPUT = "nftInfoOut";

    @Input(INPUT)
    SubscribableChannel nftInfoIn();

    @Output(OUTPUT)
    MessageChannel nftInfoOut();
}
