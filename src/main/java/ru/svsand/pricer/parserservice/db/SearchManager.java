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
 * @author sand <sve.snd@gmail.com>
 * @since 29.10.2025
 */

@Service
public class SearchManager {

	@Autowired
	SearchRepository repository;

	@Transactional
	public List<Search> findAll() {
		return repository.findAll()
				.stream()
				.map(SearchManager::fromDao)
				.toList();
	}

	@Transactional
	public List<Search> findAllForRequest() {
		LocalDateTime date = LocalDateTime.now().minusHours(1);
		return repository.findAllReadyForRequest(Timestamp.valueOf(date))
				.stream()
				.map(SearchManager::fromDao)
				.toList();
	}

	@Transactional
	public void save(@NotNull Search search) {
		repository.save(toDao(search));
	}

	// Conversion

	public static SearchDao toDao(Search search) {
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
