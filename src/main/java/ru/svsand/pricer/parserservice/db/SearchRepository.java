package ru.svsand.pricer.parserservice.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * Spring Data JPA repository for {@link SearchDao} entities.
 *
 * @author sand <sve.snd@gmail.com>
 * @since 28.10.2025
 */
public interface SearchRepository extends JpaRepository<SearchDao, Long> {

	/**
	 * Returns up to 3 searches that have never been requested or were last requested
	 * before the given cutoff timestamp. Used to implement the one-hour refresh cooldown.
	 *
	 * @param date the cutoff timestamp; searches with {@code last_request_date} before this
	 *             value (or {@code NULL}) are considered ready
	 * @return list of up to 3 searches ready for a new parsing run
	 */
	@Query(value = "SELECT * " +
			"FROM searches t " +
			"WHERE t.last_request_date IS NULL OR t.last_request_date < :date " +
			"LIMIT 3", nativeQuery = true)
	List<SearchDao> findAllReadyForRequest(@Param("date") Timestamp date);
}
