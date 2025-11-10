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
		repository.save(map(statistic));
	}

	// Conversion

	public static SearchStatisticEntity map(SearchStatistic statistic) {
		SearchStatisticEntity statisticEntity = new SearchStatisticEntity();
		if (!statistic.isNew())
			statisticEntity.setId(statistic.getId());

		statisticEntity.setSearch(SearchManager.map(statistic.getSearch()));
		statisticEntity.setStatusCode(statistic.getStatusCode());
		statisticEntity.setStatusDescription(statistic.getStatusDescription());
		statisticEntity.setCount(statistic.getCount());
		statisticEntity.setTimestamp(statistic.getTimestamp());
		statisticEntity.setVersion(statistic.getVersion());

		return statisticEntity;
	}

	public static SearchStatistic map(SearchStatisticEntity statisticEntity) {
		if (statisticEntity == null)
			return null;

		return SearchStatistic.builder()
				.id(statisticEntity.getId())
				.search(SearchManager.map(statisticEntity.getSearch()))
				.statusCode(statisticEntity.getStatusCode())
				.statusDescription(statisticEntity.getStatusDescription())
				.count(statisticEntity.getCount())
				.timestamp(statisticEntity.getTimestamp())
				.version(statisticEntity.getVersion())
				.build();
	}
}
