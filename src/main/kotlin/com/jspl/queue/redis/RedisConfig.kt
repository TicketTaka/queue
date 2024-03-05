package com.jspl.queue.redis

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {
//    @Bean
//    fun redisQueueTemplate(
//        connectionFactory: RedisConnectionFactory,
//    ): RedisTemplate<String, String> {
//        val template = RedisTemplate<String, String>()
//
//        template.connectionFactory = connectionFactory
//        //트랜잭션 지원 활성화
////        template.setEnableTransactionSupport(true)
//
//        // 직렬화/역직렬화 설정. 기본적으로 JDK의 직렬화 사용
//        template.keySerializer = StringRedisSerializer()
//        template.valueSerializer = StringRedisSerializer()
////        template.valueSerializer = JdkSerializationRedisSerializer()
////        template.keySerializer = GenericToStringSerializer(Long::class.java)
////        template.valueSerializer = Jackson2JsonRedisSerializer(jacksonObjectMapper(), BattleInfo::class.java)
//
//        return template
//    }

}