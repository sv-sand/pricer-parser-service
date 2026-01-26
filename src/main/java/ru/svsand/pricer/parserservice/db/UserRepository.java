package ru.svsand.pricer.parserservice.db;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author sand <sve.snd@gmail.com>
 * @since 05.11.2025
 */

public interface UserRepository extends JpaRepository<UserDao, Long>  {
	List<UserDao> findByTgId(Long tgId);
}
