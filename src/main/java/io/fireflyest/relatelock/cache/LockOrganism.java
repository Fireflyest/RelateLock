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
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import io.fireflyest.relatelock.bean.Lock;
import io.fireflyest.relatelock.cache.api.Organism;
import io.fireflyest.relatelock.util.StringUtils;
import io.fireflyest.relatelock.util.YamlUtils;

/**
 * 数据缓存组织实现类
 * 
 * @author Fireflyest
 * @since 1.0
 */
public class LockOrganism implements Organism<Location, Lock> {

    private final String name;
    private final Random random = new Random();
    protected final Map<Location, LockCell> cacheMap = new ConcurrentHashMap<>();

    /**
     * 数据组织构造函数
     * @param name 作为保存时的文件名称
     */
    public LockOrganism(String name) {
        this.name = name;
    }

    @Override
    public void del(@Nonnull Location key) {
        cacheMap.remove(key);
    }

    @Override
    public void expire(@Nonnull Location key, int ms) {
        final LockCell cell = cacheMap.get(key);
        if (cell != null && cell.get() != null) {
            cell.expire(ms);
        }
    }

    @Override
    public boolean exist(@Nonnull Location key) {
        return cacheMap.containsKey(key) && cacheMap.get(key) != null;
    }

    @Override
    public void persist(@Nonnull Location key) {
        final LockCell cell = cacheMap.get(key);
        if (cell != null && cell.get() != null) {
            cell.persist();
        }
    }

    @Override
    public long ttl(@Nonnull Location key) {
        final LockCell cell = cacheMap.get(key);
        long ms = 0;
        if (cell != null && cell.get() != null) {
            ms = cell.ttl();
        }
        return ms;
    }

    @Override
    public void set(@Nonnull Location key, Lock value) {
        cacheMap.put(key, new LockCell(-1, value));
    }

    @Override
    public void set(@Nonnull Location key, Set<Lock> valueSet) {
        cacheMap.put(key, new LockCell(-1, valueSet));
    }

    @Override
    public void setex(@Nonnull Location key, int ms, Lock value) {
        cacheMap.put(key, new LockCell(ms, value));
    }

    @Override
    public void setex(@Nonnull Location key, int ms, Set<Lock> valueSet) {
        cacheMap.put(key, new LockCell(ms, valueSet));
    }

    @Override
    public Lock get(@Nonnull Location key) {
        return cacheMap.containsKey(key) ? cacheMap.get(key).get() : null;
    }

    @Override
    public long age(@Nonnull Location key) {
        return cacheMap.containsKey(key) ? cacheMap.get(key).age() : 0;
    }

    @Override
    public void sadd(@Nonnull Location key, Lock value) {
        final LockCell cell = cacheMap.get(key);
        Set<Lock> valueSet = null;
        if (cell != null && (valueSet = cell.getAll()) != null) {
            valueSet.add(value);
        } else {
            cacheMap.put(key, new LockCell(-1, value));
        }
    }

    @Override
    public Set<Lock> smembers(@Nonnull Location key) {
        return cacheMap.containsKey(key) ? cacheMap.get(key).getAll() : null;
    }

    @Override
    public void srem(@Nonnull Location key, Lock value) {
        final LockCell cell = cacheMap.get(key);
        Set<Lock> valueSet = null;
        if (cell != null && (valueSet = cell.getAll()) != null) {
            valueSet.remove(value);
        }
    }

    @Override
    public Lock spop(@Nonnull Location key) {
        final LockCell cell = cacheMap.get(key);
        Set<Lock> valueSet = null;
        Lock value = null;
        if (cell != null && (valueSet = cell.getAll()) != null) {
            final int size;
            if ((size = valueSet.size()) == 0) {
                return value;
            }
            int randomInt = random.nextInt(size);
            final Iterator<Lock> iterator = valueSet.iterator();
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
    public int scard(@Nonnull Location key) {
        final LockCell cell = cacheMap.get(key);
        Set<Lock> valueSet = null;
        int size = 0;
        if (cell != null && (valueSet = cell.getAll()) != null) {
            size = valueSet.size();
        }
        return size;
    }

    @Override
    public Set<Location> keySet() {
        return cacheMap.keySet();
    }

    /**
     * 保存格式为
     * key(String) born(long) deadline(long) count(int) [obj(String)]
     */
    @Override
    public void save(@Nonnull Plugin plugin) {
        final String fileName = String.format("%s.cache", name);
        final File cacheFile = new File(plugin.getDataFolder(), fileName);
        try (FileOutputStream fStream = new FileOutputStream(cacheFile);
                DataOutputStream dStream = new DataOutputStream(fStream)) {
            
            final Iterator<Entry<Location, LockCell>> iterator = cacheMap.entrySet().iterator(); 
            // 拼接数据   
            while (iterator.hasNext()) {
                final Entry<Location, LockCell> entry = iterator.next();
                final LockCell cacheCell = entry.getValue();
                final Set<Lock> valueSet = cacheCell.getAll();
                final Instant deadline = cacheCell.getDeadline();
                // 已失效的不保存
                if (valueSet == null) {
                    iterator.remove();
                    continue;
                }
                // 数据信息拼接
                dStream.writeUTF(YamlUtils.serialize(entry.getKey())); // key
                dStream.writeLong(cacheCell.getBorn().toEpochMilli()); // 起始时间
                dStream.writeLong(deadline == null ? 0 : deadline.toEpochMilli()); // 失效时间
                dStream.writeInt(valueSet.size()); // 数据数量
                // 数据集拼接
                for (Lock value : valueSet) {
                    dStream.writeUTF(StringUtils.toJson(value)); // 数据
                }
            }
            dStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load(@Nonnull Plugin plugin) {
        final String fileName = String.format("%s.cache", name);
        final File cacheFile = new File(plugin.getDataFolder(), fileName);
        if (!cacheFile.exists()) {
            return;
        }
        try (FileInputStream fStream = new FileInputStream(cacheFile);
                DataInputStream dStream = new DataInputStream(fStream)) {
            
            while (dStream.available() > 0) {
                final String locationKey = dStream.readUTF();
                final Instant born = Instant.ofEpochMilli(dStream.readLong());
                final long dl = dStream.readLong();
                final Instant deadline = dl == 0 ? null : Instant.ofEpochMilli(dl);
                final int count = dStream.readInt();
                final Set<Lock> valueSet = new HashSet<>();
                for (int i = 0; i < count; i++) {
                    valueSet.add(StringUtils.jsonToObj(dStream.readUTF(), Lock.class));
                }
                final Location key = YamlUtils.deserialize(locationKey, Location.class);
                cacheMap.put(key, new LockCell(born, deadline, valueSet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }    
    }

}
