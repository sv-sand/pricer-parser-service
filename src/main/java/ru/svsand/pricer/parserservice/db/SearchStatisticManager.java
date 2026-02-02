package ru.svsand.pricer.parserservice.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.svsand.pricer.parserservice.logic.SearchStatistic;

/**
 * @author sand <sve.snd@gmail.com>
 * @since 07.11.2025
 */

@Service
public class SearchStatisticManager {
	@Autowired
	SearchStatisticRepository repository;

	@Autowired
	SearchManager searchManager;

	@Transactional
	public SearchStatistic save(SearchStatistic statistic) {
		SearchStatisticDao searchStatisticDao = repository.save(toDao(statistic));
		return fromDao(searchStatisticDao);
	}

	// Conversion

	public static SearchStatisticDao toDao(SearchStatistic searchStatistic) {
		SearchStatisticDao statisticEntity = new SearchStatisticDao();
		if (!searchStatistic.isNew())
			statisticEntity.setId(searchStatistic.getId());

		String description = searchStatistic.getStatusDescription();
		if (description.length() > 255)
			description = description.substring(0, 255);

		statisticEntity.setSearch(SearchManager.toDao(searchStatistic.getSearch()));
		statisticEntity.setStatusCode(searchStatistic.getStatusCode());
		statisticEntity.setStatusDescription(description);
		statisticEntity.setCount(searchStatistic.getCount());
		statisticEntity.setTimestamp(searchStatistic.getTimestamp());
		statisticEntity.setVersion(searchStatistic.getVersion());

		return statisticEntity;
	}

	public static SearchStatistic fromDao(SearchStatisticDao searchStatisticDao) {
		if (searchStatisticDao == null)
			return null;

		return SearchStatistic.builder()
				.id(searchStatisticDao.getId())
				.search(SearchManager.fromDao(searchStatisticDao.getSearch()))
				.statusCode(searchStatisticDao.getStatusCode())
				.statusDescription(searchStatisticDao.getStatusDescription())
				.count(searchStatisticDao.getCount())
				.timestamp(searchStatisticDao.getTimestamp())
				.version(searchStatisticDao.getVersion())
				.build();
	}
}
