package ru.svsand.pricer.parserservice.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.svsand.pricer.parserservice.logic.Search;
import ru.svsand.pricer.parserservice.logic.Store;

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
				.stream().map(SearchManager::map)
				.toList();
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
				.version(searchEntity.getVersion())
				.build();
	}
}
