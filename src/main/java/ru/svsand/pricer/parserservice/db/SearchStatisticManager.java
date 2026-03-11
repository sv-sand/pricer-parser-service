package ru.svsand.pricer.parserservice.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.svsand.pricer.parserservice.logic.SearchStatistic;

/**
 * Service that manages persistence of {@link SearchStatistic} domain objects.
 * Handles conversion between the {@link SearchStatistic} domain model
 * and the {@link SearchStatisticDao} JPA entity.
 * Status descriptions longer than 255 characters are truncated before saving.
 *
 * @author sand <sve.snd@gmail.com>
 * @since 07.11.2025
 */
@Service
public class SearchStatisticManager {
	@Autowired
	SearchStatisticRepository repository;

	@Autowired
	SearchManager searchManager;

	/**
	 * Persists a search statistic record.
	 * The {@code statusDescription} is truncated to 255 characters if necessary.
	 *
	 * @param statistic the statistic to save
	 * @return the saved statistic with its assigned database ID
	 */
	@Transactional
	public SearchStatistic save(SearchStatistic statistic) {
		SearchStatisticDao searchStatisticDao = repository.save(toDao(statistic));
		return fromDao(searchStatisticDao);
	}

	// Conversion

	/**
	 * Converts a {@link SearchStatistic} domain object to a {@link SearchStatisticDao} JPA entity.
	 *
	 * @param searchStatistic the domain object to convert
	 * @return the corresponding JPA entity
	 */
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

	/**
	 * Converts a {@link SearchStatisticDao} JPA entity to a {@link SearchStatistic} domain object.
	 *
	 * @param searchStatisticDao the entity to convert; may be {@code null}
	 * @return the corresponding domain object, or {@code null} if the input is {@code null}
	 */
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
