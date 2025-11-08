package ru.svsand.pricer.parserservice.db;

import jakarta.persistence.*;
import lombok.Data;

/**
 * @author sand <sve.snd@gmail.com>
 * @since 05.11.2025
 */

@Entity
@Data
@Table(name = "users")
public class UserEntity {
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
