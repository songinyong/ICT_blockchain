package blockchain.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;



public interface WalletRepository extends JpaRepository<Wallet, Long>{
	@Query(value="SELECT w FROM wallet w WHERE w.owner =?1 ", nativeQuery = true)
	Wallet findItemByWallet(String wallet);
	

}
