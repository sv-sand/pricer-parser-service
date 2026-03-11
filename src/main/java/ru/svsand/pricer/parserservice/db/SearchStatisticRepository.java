package ru.svsand.pricer.parserservice.db;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link SearchStatisticDao} entities.
 *
 * @author sand <sve.snd@gmail.com>
 * @since 07.11.2025
 */
public interface SearchStatisticRepository extends JpaRepository<SearchStatisticDao, Long> {
}
