package tech.bananaz.bot.repositories;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tech.bananaz.bot.models.ListingEvent;

@Repository
public interface ListingEventRepository extends JpaRepository<ListingEvent, Long> {
	
	ListingEvent	   findById(long id);
	boolean 		   existsByIdAndConsumedFalse(long id);
	List<ListingEvent> findByConfigIdAndConsumedFalseAndCreatedDateGreaterThanOrderByCreatedDateAsc(long configId, Instant instantAsUTC);
	List<ListingEvent> findByConfigIdAndConsumedFalseOrderByCreatedDateAsc(long configId );
	
	@Transactional
	@Modifying
	@Query("UPDATE ListingEvent e SET e.consumed = 1, e.consumedBy = ?2 WHERE e.id = ?1 AND e.consumed = 0")
	int			       updateByIdSetConsumedTrueAndConsumedBy(long id, String consumedBy);
}
