package ru.svsand.pricer.parserservice.db;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author sand <sve.snd@gmail.com>
 * @since 07.11.2025
 */

public interface SearchStatisticRepository extends JpaRepository<SearchStatisticEntity, Long> {
}
