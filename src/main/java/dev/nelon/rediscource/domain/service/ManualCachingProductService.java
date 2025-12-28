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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@AllArgsConstructor
public class ManualCachingProductService implements ProductService {
	
	private final ProductRepository productRepository;
	private final RedisTemplate<String, ProductEntity> redisTemplate;
	
	private static final String CACHE_KEY_PREFIX ="product:";
	private static final long CACHE_TTL_MINUTES = 1;
	
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
		
		var savedProduct = productRepository.save(product);
		
		String cacheKey = CACHE_KEY_PREFIX + id;
		redisTemplate.delete(cacheKey);
		log.info("Cache invalidated for updated product: id={}", id);
		
		return savedProduct;
	}
	
	@Override
	public ProductEntity getById(Long id) {
		log.info("Getting product: id={}", id);
		String cacheKey = CACHE_KEY_PREFIX + id;
		
		ProductEntity entityFromCache = redisTemplate.opsForValue().get(cacheKey);
		
		if (entityFromCache != null) {
			log.info("Product found in cache: id={}", id);
			return entityFromCache;
		}
		
		log.info("Product not found in cache: id={}", id);
		ProductEntity entityFromDb = productRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("Product not found: " + id));
		
		redisTemplate.opsForValue().set(cacheKey, entityFromDb, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
		log.info("Product cached: id={}", id);
		
		return entityFromDb;
	}
	
	
	@Override
	public void delete(Long id) {
		log.info("Deleting product from DB: {}", id);
		if (!productRepository.existsById(id)) {
			throw new RuntimeException("Product not found: " + id);
		}
		productRepository.deleteById(id);
		
		String cacheKey = CACHE_KEY_PREFIX + id;
		redisTemplate.delete(cacheKey);
		log.info("Cache invalidated for deleted product: id={}", id);
	}
	
}
