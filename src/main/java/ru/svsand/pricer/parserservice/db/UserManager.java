package ru.svsand.pricer.parserservice.db;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.svsand.pricer.parserservice.logic.User;

import java.util.List;

/**
 * Service that manages persistence and retrieval of {@link User} domain objects.
 * Handles conversion between the {@link User} domain model and the {@link UserDao} JPA entity.
 *
 * @author sand <sve.snd@gmail.com>
 * @since 05.11.2025
 */
@Service
public class UserManager {
	@Autowired
	UserRepository repository;

	/**
	 * Finds a user by their Telegram ID.
	 *
	 * @param tgId the Telegram user identifier
	 * @return the matching {@link User}, or {@code null} if not found
	 */
	@Transactional
	public User findByTgId(Long tgId) {
		List<UserDao> entities = repository.findByTgId(tgId);
		if (entities.isEmpty())
			return null;

		return fromDao(entities.get(0));
	}

	/**
	 * Persists a new or existing user.
	 *
	 * @param user the user to save; must not be {@code null}
	 * @return the saved user with its assigned database ID
	 */
	@Transactional
	public User save(@NotNull User user) {
		UserDao userDao = repository.save(toDao(user));
		return fromDao(userDao);
	}

	// Conversion

	/**
	 * Converts a {@link User} domain object to a {@link UserDao} JPA entity.
	 *
	 * @param User the domain object to convert; must not be {@code null}
	 * @return the corresponding JPA entity
	 */
	public static UserDao toDao(@NotNull User User) {
		UserDao UserDao = new UserDao();
		if (!User.isNew())
			UserDao.setId(User.getId());

		UserDao.setName(User.getName());
		UserDao.setTgId(User.getTgId());
		UserDao.setVersion(User.getVersion());

		return UserDao;
	}

	/**
	 * Converts a {@link UserDao} JPA entity to a {@link User} domain object.
	 *
	 * @param UserDao the entity to convert; may be {@code null}
	 * @return the corresponding domain object, or {@code null} if the input is {@code null}
	 */
	public static User fromDao(UserDao UserDao) {
		if (UserDao == null)
			return null;

		return User.builder()
				.id(UserDao.getId())
				.name(UserDao.getName())
				.tgId(UserDao.getTgId())
				.version(UserDao.getVersion())
				.build();
	}
}
