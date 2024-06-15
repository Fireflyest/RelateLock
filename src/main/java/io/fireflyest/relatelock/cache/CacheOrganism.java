package io.fireflyest.relatelock.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

public class CacheOrganism implements Organism<String> {

    private final Random random = new Random();
    protected final Map<String, CacheCell> cacheMap = new ConcurrentHashMap<>();

    @Override
    public void del(@Nonnull String key) {
        cacheMap.remove(key);
    }

    @Override
    public void expire(@Nonnull String key, int ms) {
        final CacheCell cell = cacheMap.get(key);
        if (cell != null && cell.get() != null) {
            cell.expire(ms);
        }
    }

    @Override
    public boolean exist(@Nonnull String key) {
        return cacheMap.containsKey(key) && cacheMap.get(key) != null;
    }

    @Override
    public void persist(@Nonnull String key) {
        final CacheCell cell = cacheMap.get(key);
        if (cell != null && cell.get() != null) {
            cell.persist();
        }
    }

    @Override
    public long ttl(@Nonnull String key) {
        final CacheCell cell = cacheMap.get(key);
        long ms = 0;
        if (cell != null && cell.get() != null) {
            ms = cell.ttl();
        }
        return ms;
    }

    @Override
    public void set(@Nonnull String key, String value) {
        cacheMap.put(key, new CacheCell(-1, value));
    }

    @Override
    public void set(@Nonnull String key, Set<String> valueSet) {
        cacheMap.put(key, new CacheCell(-1, valueSet));
    }

    @Override
    public void setex(@Nonnull String key, int ms, String value) {
        cacheMap.put(key, new CacheCell(ms, value));
    }

    @Override
    public void setex(@Nonnull String key, int ms, Set<String> valueSet) {
        cacheMap.put(key, new CacheCell(ms, valueSet));
    }

    @Override
    public String get(@Nonnull String key) {
        return cacheMap.containsKey(key) ? cacheMap.get(key).get() : null;
    }

    @Override
    public long age(@Nonnull String key) {
        return cacheMap.containsKey(key) ? cacheMap.get(key).age() : 0;
    }

    @Override
    public void sadd(@Nonnull String key, String value) {
        final CacheCell cell = cacheMap.get(key);
        Set<String> valueSet = null;
        if (cell != null && (valueSet = cell.getAll()) != null) {
            valueSet.add(value);
        } else {
            cacheMap.put(key, new CacheCell(-1, value));
        }
    }

    @Override
    public Set<String> smembers(@Nonnull String key) {
        return cacheMap.containsKey(key) ? cacheMap.get(key).getAll() : null;
    }

    @Override
    public void srem(@Nonnull String key, String value) {
        final CacheCell cell = cacheMap.get(key);
        Set<String> valueSet = null;
        if (cell != null && (valueSet = cell.getAll()) != null) {
            valueSet.remove(value);
        }
    }

    @Override
    public String spop(@Nonnull String key) {
        final CacheCell cell = cacheMap.get(key);
        Set<String> valueSet = null;
        String value = null;
        if (cell != null && (valueSet = cell.getAll()) != null) {
            int size;
            if ((size = valueSet.size()) == 0) {
                return value;
            }
            int randomInt = random.nextInt(size);
            Iterator<String> iterator = valueSet.iterator();
            while (iterator.hasNext()){
                value = iterator.next();
                if (randomInt-- == 0){
                    iterator.remove();
                    break;
                }
            }
        }
        return value;
    }

    @Override
    public int scard(@Nonnull String key) {
        final CacheCell cell = cacheMap.get(key);
        Set<String> valueSet = null;
        int size = 0;
        if (cell != null && (valueSet = cell.getAll()) != null) {
            size = valueSet.size();
        }
        return size;
    }
    
}
