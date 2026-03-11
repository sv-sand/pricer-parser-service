package ru.svsand.pricer.parserservice.db;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.svsand.pricer.parserservice.logic.Search;
import ru.svsand.pricer.parserservice.logic.Store;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service that manages persistence and retrieval of {@link Search} domain objects.
 * Handles conversion between the {@link Search} domain model and the {@link SearchDao} JPA entity.
 *
 * @author sand <sve.snd@gmail.com>
 * @since 29.10.2025
 */
@Service
public class SearchManager {

	@Autowired
	SearchRepository repository;

	@Autowired
	private UserManager userManager;

	/**
	 * Returns all persisted searches.
	 *
	 * @return list of all {@link Search} domain objects
	 */
	@Transactional
	public List<Search> findAll() {
		return repository.findAll()
				.stream()
				.map(SearchManager::fromDao)
				.toList();
	}

	/**
	 * Returns up to 3 searches that are due for a new parsing run
	 * (never requested, or last requested more than one hour ago).
	 *
	 * @return list of searches ready to be processed
	 */
	@Transactional
	public List<Search> findAllForRequest() {
		LocalDateTime date = LocalDateTime.now().minusHours(1);

		return repository.findAllReadyForRequest(Timestamp.valueOf(date))
				.stream()
				.map(SearchManager::fromDao)
				.toList();
	}

	/**
	 * Persists a new or existing search.
	 *
	 * @param search the search to save; must not be {@code null}
	 * @return the saved search with its assigned database ID
	 */
	@Transactional
	public Search save(@NotNull Search search) {
		SearchDao searchDao = repository.save(toDao(search));
		return fromDao(searchDao);
	}

	// Conversion

	/**
	 * Converts a {@link Search} domain object to a {@link SearchDao} JPA entity.
	 *
	 * @param search the domain object to convert; must not be {@code null}
	 * @return the corresponding JPA entity
	 */
	public static SearchDao toDao(@NotNull Search search) {
		SearchDao searchDao = new SearchDao();
		if (!search.isNew())
			searchDao.setId(search.getId());

		searchDao.setUser(UserManager.toDao(search.getUser()));
		searchDao.setStore(search.getStore().name());
		searchDao.setKeyWords(search.getKeyWords());
		searchDao.setTargetPrice(search.getTargetPrice());
		searchDao.setLastRequestDate(search.getLastRequestDate());
		searchDao.setVersion(search.getVersion());

		return searchDao;
	}

	/**
	 * Converts a {@link SearchDao} JPA entity to a {@link Search} domain object.
	 *
	 * @param searchDao the entity to convert; may be {@code null}
	 * @return the corresponding domain object, or {@code null} if the input is {@code null}
	 */
	public static Search fromDao(SearchDao searchDao) {
		if (searchDao == null)
			return null;

		return Search.builder()
				.id(searchDao.getId())
				.user(UserManager.fromDao(searchDao.getUser()))
				.store(Store.valueOf(searchDao.getStore()))
				.keyWords(searchDao.getKeyWords())
				.targetPrice(searchDao.getTargetPrice())
				.lastRequestDate(searchDao.getLastRequestDate())
				.version(searchDao.getVersion())
				.build();
	}
}
