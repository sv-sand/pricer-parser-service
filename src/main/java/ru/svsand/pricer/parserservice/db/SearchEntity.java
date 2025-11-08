package ru.svsand.pricer.parserservice.db;

import jakarta.persistence.*;
import lombok.Data;

/**
 * @author sand <sve.snd@gmail.com>
 * @since 28.10.2025
 */

@Entity
@Data
@Table(name = "searches")
public class SearchEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private UserEntity user;

	private String store;

	@Column(name = "key_words")
	private String keyWords;

	@Column(name = "target_price")
	private Double targetPrice;

	@Version
	private Long version;
}
