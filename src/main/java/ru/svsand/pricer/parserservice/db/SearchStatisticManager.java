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

	@Transactional
	public void save(SearchStatistic statistic) {
		repository.save(toDao(statistic));
	}

	// Conversion

	public static SearchStatisticDao toDao(SearchStatistic statistic) {
		SearchStatisticDao statisticEntity = new SearchStatisticDao();
		if (!statistic.isNew())
			statisticEntity.setId(statistic.getId());

		statisticEntity.setSearch(SearchManager.toDao(statistic.getSearch()));
		statisticEntity.setStatusCode(statistic.getStatusCode());
		statisticEntity.setStatusDescription(statistic.getStatusDescription());
		statisticEntity.setCount(statistic.getCount());
		statisticEntity.setTimestamp(statistic.getTimestamp());
		statisticEntity.setVersion(statistic.getVersion());

		return statisticEntity;
	}

	public static SearchStatistic fromDao(SearchStatisticDao statisticEntity) {
		if (statisticEntity == null)
			return null;

		return SearchStatistic.builder()
				.id(statisticEntity.getId())
				.search(SearchManager.fromDao(statisticEntity.getSearch()))
				.statusCode(statisticEntity.getStatusCode())
				.statusDescription(statisticEntity.getStatusDescription())
				.count(statisticEntity.getCount())
				.timestamp(statisticEntity.getTimestamp())
				.version(statisticEntity.getVersion())
				.build();
	}
}
