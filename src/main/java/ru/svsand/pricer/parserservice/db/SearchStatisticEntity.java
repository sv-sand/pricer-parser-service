package ru.svsand.pricer.parserservice.db;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author sand <sve.snd@gmail.com>
 * @since 07.11.2025
 */

@Entity
@Data
@Table(name = "search_statistic")
public class SearchStatisticEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "timestamp")
	private Timestamp timestamp;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "search_id")
	private SearchEntity search;

	@Column(name = "status_code")
	private int statusCode;

	@Column(name = "status_description")
	private String statusDescription;

	@Column(name = "count")
	private int count;

	@Version
	private Long version;
}
