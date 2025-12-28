package dev.nelon.rediscource.domain;


import dev.nelon.rediscource.api.ProductCreateRequest;
import dev.nelon.rediscource.api.ProductUpdateRequest;
import dev.nelon.rediscource.domain.db.ProductEntity;

public interface ProductService {
	ProductEntity create(ProductCreateRequest createRequest);
	
	ProductEntity update(Long id, ProductUpdateRequest updateRequest);
	
	ProductEntity getById(Long id);
	
	void delete(Long id);
}
