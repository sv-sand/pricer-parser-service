package ru.svsand.pricer.parserservice.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author sand <sve.snd@gmail.com>
 * @since 28.10.2025
 */

public interface SearchRepository extends JpaRepository<SearchEntity, Long> {
	@Query(value = "SELECT * " +
			"FROM searches t " +
			"WHERE t.last_request_date IS NULL OR t.last_request_date < :date " +
			"LIMIT 1", nativeQuery = true)
	List<SearchEntity> findAllReadyForRequest(@Param("date") Timestamp date);
}
