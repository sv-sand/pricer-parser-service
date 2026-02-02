package ru.svsand.pricer.parserservice.db;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.svsand.pricer.parserservice.Data;
import ru.svsand.pricer.parserservice.logic.Search;
import ru.svsand.pricer.parserservice.logic.SearchStatistic;
import ru.svsand.pricer.parserservice.logic.Store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchStatisticManagerTest {

	@InjectMocks
	private SearchStatisticManager manager;

	@Mock
	private SearchStatisticRepository repository;

	@Mock
	private SearchManager searchManager;

	@Test
	void save() {
		Search search = Data.search(Store.WB, Data.user());
		SearchDao searchDao = Data.searchDao(Store.WB, Data.userDao());
		SearchStatistic searchStatistic = Data.searchStatistic(search);
		SearchStatisticDao searchStatisticDao = Data.searchStatisticDao(searchDao);

		// Arrange
		when(repository.save(any(SearchStatisticDao.class))).thenReturn(searchStatisticDao);

		// Act
		SearchStatistic result = manager.save(searchStatistic);

		// Assert
		checkCall_Repository_Save(searchStatisticDao);
		compareSearchStatistic(searchStatistic, result);
	}

	// Checks

	private void checkCall_Repository_Save(SearchStatisticDao searchStatisticDao) {
		ArgumentCaptor<SearchStatisticDao> captor = ArgumentCaptor.forClass(SearchStatisticDao.class);
		verify(repository, times(1)).save(captor.capture());

		compareSearchStatisticDao(searchStatisticDao, captor.getValue());
	}

	// Helpful methods

	private void compareSearchStatistic(SearchStatistic expected, SearchStatistic actual) {
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getSearch(), actual.getSearch());
		assertEquals(expected.getStatusCode(), actual.getStatusCode());
		assertEquals(expected.getStatusDescription(), actual.getStatusDescription());
		assertEquals(expected.getCount(), actual.getCount());
		assertEquals(expected.getTimestamp(), actual.getTimestamp());
		assertEquals(expected.getVersion(), actual.getVersion());
	}

	private void compareSearchStatisticDao(SearchStatisticDao expected, SearchStatisticDao actual) {
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getSearch(), actual.getSearch());
		assertEquals(expected.getStatusCode(), actual.getStatusCode());
		assertEquals(expected.getStatusDescription(), actual.getStatusDescription());
		assertEquals(expected.getCount(), actual.getCount());
		assertEquals(expected.getTimestamp(), actual.getTimestamp());
		assertEquals(expected.getVersion(), actual.getVersion());
	}
}
