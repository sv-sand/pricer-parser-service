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
				.map(SearchManager::map)
				.toList();
	}

	@Transactional
	public List<Search> findAllReadyForRequest() {
		LocalDateTime date = LocalDateTime.now().minusHours(1);
		return repository.findAllReadyForRequest(Timestamp.valueOf(date))
				.stream()
				.map(SearchManager::map)
				.toList();
	}

	@Transactional
	public void save(@NotNull Search search) {
		repository.save(map(search));
	}

	// Conversion

	public static SearchEntity map(Search search) {
		SearchEntity searchEntity = new SearchEntity();
		if (!search.isNew())
			searchEntity.setId(search.getId());

		searchEntity.setUser(UserManager.map(search.getUser()));
		searchEntity.setStore(search.getStore().name());
		searchEntity.setKeyWords(search.getKeyWords());
		searchEntity.setTargetPrice(search.getTargetPrice());
		searchEntity.setLastRequestDate(search.getLastRequestDate());
		searchEntity.setVersion(search.getVersion());

		return searchEntity;
	}

	public static Search map(SearchEntity searchEntity) {
		if (searchEntity == null)
			return null;

		return Search.builder()
				.id(searchEntity.getId())
				.user(UserManager.map(searchEntity.getUser()))
				.store(Store.valueOf(searchEntity.getStore()))
				.keyWords(searchEntity.getKeyWords())
				.targetPrice(searchEntity.getTargetPrice())
				.lastRequestDate(searchEntity.getLastRequestDate())
				.version(searchEntity.getVersion())
				.build();
	}
}
