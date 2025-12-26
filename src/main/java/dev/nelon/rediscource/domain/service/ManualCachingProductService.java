package dev.nelon.rediscource.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nelon.rediscource.api.ProductCreateRequest;
import dev.nelon.rediscource.api.ProductUpdateRequest;
import dev.nelon.rediscource.domain.ProductService;
import dev.nelon.rediscource.domain.db.ProductEntity;
import dev.nelon.rediscource.domain.db.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ManualCachingProductService implements ProductService {
	
	private final ProductRepository productRepository;
	private final StringRedisTemplate stringRedisTemplate;
	private final ObjectMapper objectMapper;
	
	private static final String CACHE_KEY_PREFIX ="product:";
	
	@Override
	public ProductEntity create(ProductCreateRequest createRequest) {
		log.info("Creating product in DB: {}", createRequest.name());
		ProductEntity product = ProductEntity.builder()
			.name(createRequest.name())
			.price(createRequest.price())
			.description(createRequest.description())
			.build();
		return productRepository.save(product);
	}
	
	@Override
	public ProductEntity update(Long id, ProductUpdateRequest updateRequest) {
		log.info("Updating product in DB: {}", id);
		ProductEntity product = productRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("Product not found: " + id));
		
		if (updateRequest.price() != null) {
			product.setPrice(updateRequest.price());
		}
		if (updateRequest.description() != null) {
			product.setDescription(updateRequest.description());
		}
		
		return productRepository.save(product);
	}
	
	@Override
	public ProductEntity getById(Long id) throws JsonProcessingException {
		log.info("Getting product: id={}", id);
		String cacheKey = CACHE_KEY_PREFIX + id;
		
		String objFromCache = stringRedisTemplate.opsForValue().get("product:" + id);
		
		if (objFromCache != null) {
			log.info("Product found in cache: id={}", id);
			return objectMapper.readValue(objFromCache, ProductEntity.class);
		}
		log.info("Product not found in cache: id={}", id);
		
		ProductEntity entityFromDb = productRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("Product not found: " + id));
		
		stringRedisTemplate.opsForValue()
			.set(cacheKey, objectMapper.writeValueAsString(entityFromDb));
		log.info("Product cached: id={}", id);
		
		return productRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("Product not found: " + id));
	}
	
	
	@Override
	public void delete(Long id) {
		log.info("Deleting product from DB: {}", id);
		if (!productRepository.existsById(id)) {
			throw new RuntimeException("Product not found: " + id);
		}
		productRepository.deleteById(id);
	}
	
}
