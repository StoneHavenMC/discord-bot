package fr.stonehaven.discord.bot.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheEvictScheduler {

    private final CacheManager cacheManager;

    @Scheduled(fixedRate = 1_800_000) // 1 hour
    public void evictAllCachesAtIntervals() {
        for(String cacheName : cacheManager.getCacheNames()) {
            cacheManager.getCache(cacheName);
        }
    }
}