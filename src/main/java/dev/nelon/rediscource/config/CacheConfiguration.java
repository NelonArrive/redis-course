package dev.nelon.rediscource.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nelon.rediscource.domain.db.ProductEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class CacheConfiguration {
	
	@Bean
	public RedisTemplate<String, ProductEntity> redisProductTemplate(
		RedisConnectionFactory redisConnectionFactory,
		ObjectMapper objectMapper
	) {
		RedisTemplate<String, ProductEntity> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		
		var serializer = new Jackson2JsonRedisSerializer<>(objectMapper, ProductEntity.class);
		redisTemplate.setValueSerializer(serializer);
		
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}
	
	
}
