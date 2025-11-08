package ru.svsand.pricer.parserservice.logic;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author sand <sve.snd@gmail.com>
 * @since 04.11.2025
 */

@Data
@Builder
@ToString(of = {"name", "id"})
public class User {
	private Long id;
	private String name;
	private Long tgId;

	private Long version;

	public boolean isNew() {
		return id == null;
	}
}
