package ru.svsand.pricer.parserservice.db;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.svsand.pricer.parserservice.logic.Search;
import ru.svsand.pricer.parserservice.logic.SearchStatistic;
import ru.svsand.pricer.parserservice.Data;
import ru.svsand.pricer.parserservice.logic.Store;
import ru.svsand.pricer.parserservice.logic.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchStatisticManagerTest {

	@InjectMocks
	private SearchStatisticManager manager;

	@Mock
	private SearchStatisticRepository repository;

	@Test
	void save() {
		Search search = Data.search(Store.WB, Data.user());
		SearchDao searchDao = Data.searchDao(Store.WB, Data.userDao());
		SearchStatistic searchStatistic = Data.searchStatistic(search);
		SearchStatisticDao searchStatisticDao = Data.searchStatisticDao(searchDao);

		// Arrange
		when(repository.save(any())).thenReturn(searchStatisticDao);

		// Act
		SearchStatistic result = manager.save(searchStatistic);

		// Assert
		checkCallRepositorySave(searchStatisticDao);
		checkSearchStatistic(searchStatistic, result);
	}

	// Checks

	private void checkCallRepositorySave(SearchStatisticDao searchStatisticDao) {
		ArgumentCaptor<SearchStatisticDao> captor = ArgumentCaptor.forClass(SearchStatisticDao.class);
		verify(repository, times(1)).save(captor.capture());

		assertEquals(searchStatisticDao.getId(), captor.getValue().getId());
		assertEquals(searchStatisticDao.getSearch(), captor.getValue().getSearch());
		assertEquals(searchStatisticDao.getStatusCode(), captor.getValue().getStatusCode());
		assertEquals(searchStatisticDao.getStatusDescription(), captor.getValue().getStatusDescription());
		assertEquals(searchStatisticDao.getCount(), captor.getValue().getCount());
		assertEquals(searchStatisticDao.getTimestamp(), captor.getValue().getTimestamp());
	}

	private void checkSearchStatistic(SearchStatistic searchStatistic, SearchStatistic result) {
		assertEquals(searchStatistic.getId(), result.getId());
		assertEquals(searchStatistic.getSearch(), result.getSearch());
		assertEquals(searchStatistic.getStatusCode(), result.getStatusCode());
		assertEquals(searchStatistic.getStatusDescription(), result.getStatusDescription());
		assertEquals(searchStatistic.getCount(), result.getCount());
		assertEquals(searchStatistic.getTimestamp(), result.getTimestamp());
	}
}
