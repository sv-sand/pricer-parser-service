package ru.svsand.pricer.parserservice.db;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.svsand.pricer.parserservice.Data;
import ru.svsand.pricer.parserservice.logic.Search;
import ru.svsand.pricer.parserservice.logic.Store;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchManagerTest {

	@InjectMocks
	private SearchManager manager;

	@Mock
	private SearchRepository repository;

	@Test
	void findAll() {
		Search search = Data.search(Store.WB, Data.user());
		SearchDao searchDao = Data.searchDao(Store.WB, Data.userDao());

		// Arrange
		when(repository.findAll()).thenReturn(List.of(searchDao));

		// Act
		List<Search> result = manager.findAll();

		// Assert
		assertEquals(1, result.size());
		compareSearch(search, result.get(0));
	}

	@Test
	void findAll_Empty() {
		// Arrange
		when(repository.findAll()).thenReturn(List.of());

		// Act
		List<Search> result = manager.findAll();

		// Assert
		assertEquals(0, result.size());
	}

	@Test
	void findAllForRequest() {
		Search search = Data.search(Store.WB, Data.user());
		SearchDao searchDao = Data.searchDao(Store.WB, Data.userDao());

		// Arrange
		when(repository.findAllReadyForRequest(any(Timestamp.class))).thenReturn(List.of(searchDao));

		// Act
		List<Search> result = manager.findAllForRequest();

		// Assert
		assertEquals(1, result.size());
		compareSearch(search, result.get(0));
	}

	@Test
	void findAllForRequest_Empty() {
		// Arrange
		when(repository.findAllReadyForRequest(any(Timestamp.class))).thenReturn(List.of());

		// Act
		List<Search> result = manager.findAllForRequest();

		// Assert
		assertEquals(0, result.size());
	}

	@Test
	void save() {
		Search search = Data.search(Store.WB, Data.user());
		SearchDao searchDao = Data.searchDao(Store.WB, Data.userDao());

		// Arrange
		when(repository.save(any(SearchDao.class))).thenReturn(searchDao);

		// Act
		Search result = manager.save(search);

		// Assert
		checkCall_Repository_Save(searchDao);
		compareSearch(search, result);
	}

	// Checks

	private void checkCall_Repository_Save(SearchDao searchDao) {
		ArgumentCaptor<SearchDao> captor = ArgumentCaptor.forClass(SearchDao.class);
		verify(repository, times(1)).save(captor.capture());
		compareSearchDao(searchDao, captor.getValue());
	}

	// Helpful methods

	private void compareSearchDao(SearchDao expected, SearchDao actual) {
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getUser().getId(), actual.getUser().getId());
		assertEquals(expected.getStore(), actual.getStore());
		assertEquals(expected.getKeyWords(), actual.getKeyWords());
		assertEquals(expected.getTargetPrice(), actual.getTargetPrice());
		assertEquals(expected.getLastRequestDate(), actual.getLastRequestDate());
		assertEquals(expected.getVersion(), actual.getVersion());
	}

	private void compareSearch(Search expected, Search actual) {
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getUser().getId(), actual.getUser().getId());
		assertEquals(expected.getStore().name(), actual.getStore().name());
		assertEquals(expected.getKeyWords(), actual.getKeyWords());
		assertEquals(expected.getTargetPrice(), actual.getTargetPrice());
		assertEquals(expected.getLastRequestDate(), actual.getLastRequestDate());
		assertEquals(expected.getVersion(), actual.getVersion());
	}
}
