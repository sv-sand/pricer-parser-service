package ru.svsand.pricer.parserservice.db;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

/**
 * JPA entity mapped to the {@code searches} table.
 * Represents the persistence form of a {@link ru.svsand.pricer.parserservice.logic.Search}.
 *
 * @author sand <sve.snd@gmail.com>
 * @since 28.10.2025
 */
@Entity
@Data
@Table(name = "searches")
public class SearchDao {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private UserDao user;

	private String store;

	@Column(name = "key_words")
	private String keyWords;

	@Column(name = "target_price")
	private Double targetPrice;

	@Column(name = "last_request_date")
	private Timestamp lastRequestDate;

	@Version
	private Long version;
}
