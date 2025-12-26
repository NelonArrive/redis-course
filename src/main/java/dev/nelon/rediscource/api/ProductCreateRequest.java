package dev.nelon.rediscource.api;

import java.math.BigDecimal;

public record ProductCreateRequest(
	String name,
	BigDecimal price,
	String description
) {
}
