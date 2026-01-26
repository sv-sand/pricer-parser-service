package ru.svsand.pricer.parserservice.db;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.svsand.pricer.parserservice.logic.User;

import java.util.List;

/**
 * @author sand <sve.snd@gmail.com>
 * @since 05.11.2025
 */

@Service
public class UserManager {
	@Autowired
	UserRepository repository;

	@Transactional
	public User findByTgId(Long tgId) {
		List<UserDao> entities = repository.findByTgId(tgId);
		if (entities.isEmpty())
			return null;

		return fromDao(entities.get(0));
	}

	@Transactional
	public User save(@NotNull User user) {
		return fromDao(repository.save(toDao(user)));
	}

	// Conversion

	public static UserDao toDao(@NotNull User User) {
		UserDao UserDao = new UserDao();
		if (!User.isNew())
			UserDao.setId(User.getId());

		UserDao.setName(User.getName());
		UserDao.setTgId(User.getTgId());
		UserDao.setVersion(User.getVersion());

		return UserDao;
	}

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
