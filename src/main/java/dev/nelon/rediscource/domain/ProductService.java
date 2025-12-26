package dev.nelon.rediscource.domain;


import com.fasterxml.jackson.core.JsonProcessingException;
import dev.nelon.rediscource.api.ProductCreateRequest;
import dev.nelon.rediscource.api.ProductUpdateRequest;
import dev.nelon.rediscource.domain.db.ProductEntity;

public interface ProductService {
	ProductEntity create(ProductCreateRequest createRequest);
	
	ProductEntity update(Long id, ProductUpdateRequest updateRequest);
	
	ProductEntity getById(Long id) throws JsonProcessingException;
	
	void delete(Long id);
}
