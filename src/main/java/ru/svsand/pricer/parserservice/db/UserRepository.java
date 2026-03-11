package ru.svsand.pricer.parserservice.db;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link UserDao} entities.
 *
 * @author sand <sve.snd@gmail.com>
 * @since 05.11.2025
 */
public interface UserRepository extends JpaRepository<UserDao, Long> {

	/**
	 * Returns all users with the given Telegram ID.
	 *
	 * @param tgId the Telegram user identifier
	 * @return list of matching users; normally contains at most one element
	 */
	List<UserDao> findByTgId(Long tgId);
}
