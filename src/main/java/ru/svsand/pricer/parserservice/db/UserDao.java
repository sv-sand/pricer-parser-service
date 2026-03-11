package ru.svsand.pricer.parserservice.db;

import jakarta.persistence.*;
import lombok.Data;

/**
 * JPA entity mapped to the {@code users} table.
 * Represents the persistence form of a {@link ru.svsand.pricer.parserservice.logic.User}.
 *
 * @author sand <sve.snd@gmail.com>
 * @since 05.11.2025
 */
@Entity
@Data
@Table(name = "users")
public class UserDao {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "tg_id")
	private Long tgId;

	@Version
	private Long version;


}
