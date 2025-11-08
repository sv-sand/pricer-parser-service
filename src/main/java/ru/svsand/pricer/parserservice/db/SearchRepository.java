package ru.svsand.pricer.parserservice.db;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author sand <sve.snd@gmail.com>
 * @since 28.10.2025
 */

public interface SearchRepository extends JpaRepository<SearchEntity, Long> {
}
