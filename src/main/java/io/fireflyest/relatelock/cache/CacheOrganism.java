package io.fireflyest.relatelock.cache;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.Instant;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import org.bukkit.plugin.Plugin;
import io.fireflyest.relatelock.cache.api.StringOrganism;

/**
 * 数据缓存组织实现类
 * 
 * @author Fireflyest
 * @since 1.0
 */
public class CacheOrganism implements StringOrganism {

    private final String name;
    private final Random random = new Random();
    protected final Map<String, CacheCell> cacheMap = new ConcurrentHashMap<>();

    /**
     * 数据组织构造函数
     * @param name 作为保存时的文件名称
     */
    public CacheOrganism(String name) {
        this.name = name;
    }

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
            while (iterator.hasNext()) {
                value = iterator.next();
                if (randomInt-- == 0) {
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

    @Override
    public Set<String> keySet() {
        return cacheMap.keySet();
    }

    /**
     * 保存格式为
     * key(String) born(long) deadline(long) count(int) [obj(String)]
     */
    @Override
    public void save(@Nonnull Plugin plugin) {
        String fileName = String.format("%s.cache", name);
        File cacheFile = new File(plugin.getDataFolder(), fileName);
        try (FileOutputStream fStream = new FileOutputStream(cacheFile);
                DataOutputStream dStream = new DataOutputStream(fStream)) {
            
            final Iterator<Entry<String, CacheCell>> iterator = cacheMap.entrySet().iterator(); 
            // 拼接数据   
            while (iterator.hasNext()) {
                final Entry<String, CacheCell> entry = iterator.next();
                final CacheCell cacheCell = entry.getValue();
                final Set<String> valueSet = cacheCell.getAll();
                final Instant deadline = cacheCell.getDeadline();
                // 已失效的不保存
                if (valueSet == null) {
                    iterator.remove();
                    continue;
                }
                // 数据信息拼接
                dStream.writeUTF(entry.getKey()); // key
                dStream.writeLong(cacheCell.getBorn().toEpochMilli()); // 起始时间
                dStream.writeLong(deadline == null ? 0 : deadline.toEpochMilli()); // 失效时间
                dStream.writeInt(valueSet.size()); // 数据数量
                // 数据集拼接
                for (String value : valueSet) {
                    dStream.writeUTF(value); // 数据
                }
            }
            dStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load(@Nonnull Plugin plugin) {
        String fileName = String.format("%s.cache", name);
        File cacheFile = new File(plugin.getDataFolder(), fileName);
        try (FileInputStream fStream = new FileInputStream(cacheFile);
                DataInputStream dStream = new DataInputStream(fStream)) {
            while (dStream.available() > 0) {
                String key = dStream.readUTF();
                Instant born = Instant.ofEpochMilli(dStream.readLong());
                long dl = dStream.readLong();
                Instant deadline = dl == 0 ? null : Instant.ofEpochMilli(dl);
                int count = dStream.readInt();
                Set<String> valueSet = new HashSet<>();
                for (int i = 0; i < count; i++) {
                    valueSet.add(dStream.readUTF());
                }
                CacheCell cacheCell = new CacheCell(born, deadline, valueSet);
                cacheMap.put(key, cacheCell);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }    
    }

}
