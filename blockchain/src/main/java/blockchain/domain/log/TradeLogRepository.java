/*
 * 아이템 테이블에 매핑되는 repository
 * */

package blockchain.domain.log;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeLogRepository extends JpaRepository<TradeLog, Long>{

	

}
