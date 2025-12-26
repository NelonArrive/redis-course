package dev.nelon.rediscource.api;

import dev.nelon.rediscource.domain.db.ProductEntity;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for {@link ProductEntity}
 */
public record ProductDto(
	Long id,
	String name,
	BigDecimal price,
	String description,
	Instant createdAt,
	Instant updatedAt
) {
}