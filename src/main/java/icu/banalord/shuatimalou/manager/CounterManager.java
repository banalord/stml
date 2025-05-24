package icu.banalord.shuatimalou.manager;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.IntegerCodec;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * 通用计数器类（可用于实现频率统计、限流、封禁等）
 * 本项目用于频率统计
 */
@Slf4j
@Component
public class CounterManager {
    @Resource
    private RedissonClient redissonClient;

    public long incrAndGetCounter(String key) {
        return incrAndGetCounter(key, Duration.ofSeconds(60));
    }

    public long incrAndGetCounter(String key, int timeInterval, TimeUnit timeUnit) {
        int expirationTimeInSeconds = switch (timeUnit) {
            case SECONDS -> timeInterval;
            case MINUTES -> timeInterval * 60;
            case HOURS -> timeInterval * 60 * 60;
            default -> throw new IllegalArgumentException("Unsupported TimeUnit. Use SECONDS, MINUTES, or HOURS.");
        };

        return incrAndGetCounter(key, timeInterval, timeUnit, expirationTimeInSeconds);
    }

    public long incrAndGetCounter(String key, Duration intervalDuration) {
        // 自动以时间窗口大小作为 Redis 过期时间
        long expirationTimeInSeconds = intervalDuration.getSeconds();
        return incrAndGetCounter(key, intervalDuration, expirationTimeInSeconds);
    }

    /**
     * 增加并返回计数
     *
     * @param key                     缓存键
     * @param timeInterval            时间间隔
     * @param timeUnit                时间间隔单位
     * @param expirationTimeInSeconds 计数器缓存过期时间
     * @return
     */
    public long incrAndGetCounter(String key,
                                  int timeInterval, TimeUnit timeUnit,
                                  long expirationTimeInSeconds) {
        // 判空 Key
        if (StrUtil.isBlank(key)) {
            return 0L;
        }
        long timeFactor = switch (timeUnit) {
            case SECONDS -> Instant.now().getEpochSecond() / timeInterval;
            case MINUTES -> Instant.now().getEpochSecond() / (timeInterval * 60L);
            case HOURS -> Instant.now().getEpochSecond() / (timeInterval * 60L * 60L);
            default -> throw new IllegalArgumentException("Unsupported TimeUnit. Use SECONDS, MINUTES, or HOURS.");
        };
        String RedisKey = key + ":" + timeFactor;

        // Lua 脚本
        final String luaScript = """
                if redis.call('exists', KEYS[1]) == 1 then
                  return redis.call('incr', KEYS[1]);
                else
                  redis.call('set', KEYS[1], 1);
                  redis.call('expire', KEYS[1], ARGV[1]);
                  return 1;
                end;
                """;
        RScript script = redissonClient.getScript(IntegerCodec.INSTANCE);
        Object eval = script.eval(
                RScript.Mode.READ_WRITE,
                luaScript,
                RScript.ReturnType.INTEGER,
                Collections.singletonList(RedisKey),
                expirationTimeInSeconds
        );
        // 根据时间粒度生成 RedisKey
        return (Long) eval;
    }

    /**
     * 增加并返回计数
     *
     * @param key                     缓存键
     * @param intervalDuration        时间片长度
     * @param expirationTimeInSeconds 计数器缓存过期时间
     * @return
     */
    public long incrAndGetCounter(String key, Duration intervalDuration,
                                  long expirationTimeInSeconds) {
        // 判空 Key
        if (StrUtil.isBlank(key)) {
            return 0L;
        }

        // 获取当前时间戳（秒）
        long nowEpochSeconds = Instant.now().getEpochSecond();

        // 计算时间因子（窗口编号）
        long timeFactor = nowEpochSeconds / intervalDuration.getSeconds();

        // 构造 Redis Key
        String RedisKey = key + ":" + timeFactor;

        // Lua 脚本
        final String luaScript = """
                if redis.call('exists', KEYS[1]) == 1 then
                  return redis.call('incr', KEYS[1]);
                else
                  redis.call('set', KEYS[1], 1);
                  redis.call('expire', KEYS[1], ARGV[1]);
                  return 1;
                end;
                """;

        RScript script = redissonClient.getScript(IntegerCodec.INSTANCE);
        Object eval = script.eval(
                RScript.Mode.READ_WRITE,
                luaScript,
                RScript.ReturnType.INTEGER,
                Collections.singletonList(RedisKey),
                expirationTimeInSeconds
        );
        // 根据时间粒度生成 RedisKey
        return (Long) eval;
    }


}
