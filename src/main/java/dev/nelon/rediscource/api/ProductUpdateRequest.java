package dev.nelon.rediscource.api;

import java.math.BigDecimal;

public record ProductUpdateRequest(
	BigDecimal price,
	String description
) {
}

