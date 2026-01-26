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

import static org.junit.jupiter.api.Assertions.*;
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
		checkUser(user, result);
	}

	@Test
	void findByTgIdNull() {
		// Arrange
		when(repository.findByTgId(101L)).thenReturn(List.of());

		// Act
		User result = manager.findByTgId(any());

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
		checkUser(user, result);
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

		assertEquals(userDao.getId(), captor.getValue().getId());
		assertEquals(userDao.getTgId(), captor.getValue().getTgId());
		assertEquals(userDao.getName(), captor.getValue().getName());
	}

	private void checkUser(User expectedUser, User actualUser) {
		assertEquals(expectedUser.getId(), actualUser.getId());
		assertEquals(expectedUser.getTgId(), actualUser.getTgId());
		assertEquals(expectedUser.getName(), actualUser.getName());
	}
}