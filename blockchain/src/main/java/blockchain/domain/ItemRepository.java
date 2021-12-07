package blockchain.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ItemRepository extends JpaRepository<Item, Long>{

	
	@Query(value = "SELECT max(i.id) FROM Item i")
	Long getMaxTransactionId();
	
	@Query(value="SELECT i.token_id FROM Item i WHERE i.token_id =?1 ", nativeQuery = true)
	List<String> checkID(String token_id);
	
	@Query(value="SELECT i.token_id FROM Item i ", nativeQuery = true)
	List<String> findAllID();
	
	@Query(value="SELECT * FROM Item i WHERE i.owner =?1 ", nativeQuery = true)
	List<Item> findByWallet(String wallet);

}
