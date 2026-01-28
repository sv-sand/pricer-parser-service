package ru.svsand.pricer.parserservice.db;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.svsand.pricer.parserservice.Data;
import ru.svsand.pricer.parserservice.logic.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserManagerTest {

	@InjectMocks
	private UserManager manager;

	@Mock
	private UserRepository repository;

	@Test
	void findByTgId() {
		User user = Data.user();
		UserDao userDao = Data.userDao();

		// Arrange
		when(repository.findByTgId(any())).thenReturn(List.of(userDao));

		// Act
		User result = manager.findByTgId(101L);

		// Assert
		checkCallRepositoryFindByTgId(101L);
		compareUser(user, result);
	}

	@Test
	void findByTgIdNull() {
		// Arrange
		when(repository.findByTgId(any())).thenReturn(List.of());

		// Act
		User result = manager.findByTgId(101L);

		// Assert
		checkCallRepositoryFindByTgId(101L);
		assertNull(result);
	}

	@Test
	void save() {
		User user = Data.user();
		UserDao userDao = Data.userDao();

		// Arrange
		when(repository.save(any())).thenReturn(userDao);

		// Act
		User result = manager.save(user);

		// Assert
		checkCallRepositorySave(userDao);
		compareUser(user, result);
	}

	// Checks

	private void checkCallRepositoryFindByTgId(Long tgId) {
		ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
		verify(repository, times(1)).findByTgId(captor.capture());
		assertEquals(tgId, captor.getValue());
	}

	private void checkCallRepositorySave(UserDao userDao) {
		ArgumentCaptor<UserDao> captor = ArgumentCaptor.forClass(UserDao.class);
		verify(repository, times(1)).save(captor.capture());

		compareUserDao(userDao, captor.getValue());
	}

	private void compareUser(User expected, User actual) {
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getTgId(), actual.getTgId());
		assertEquals(expected.getVersion(), actual.getVersion());
	}

	private void compareUserDao(UserDao expected, UserDao actual) {
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getTgId(), actual.getTgId());
		assertEquals(expected.getVersion(), actual.getVersion());
	}
}