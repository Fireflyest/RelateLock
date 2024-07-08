package io.fireflyest.relatelock.cache;

import javax.annotation.Nonnull;
import org.bukkit.Location;
import io.fireflyest.relatelock.bean.Lock;
import io.fireflyest.relatelock.util.StrUtils;
import io.fireflyest.relatelock.util.YamlUtils;

/**
 * 数据缓存组织实现类
 * 
 * @author Fireflyest
 * @since 1.0
 */
public final class LockOrganism extends AbstractOrganism<Location, Lock> {

    public LockOrganism(String name) {
        super(name);
    }

    @Override
    public Location deserializeKey(@Nonnull String keyStr) {
        return YamlUtils.deserialize(StrUtils.base64Decode(keyStr), Location.class);
    }

    @Override
    public Lock deserializeValue(@Nonnull String valueStr) {
        return StrUtils.jsonToObj(StrUtils.base64Decode(valueStr), Lock.class);
    }

    @Override
    public String serializeKey(@Nonnull Location key) {
        return StrUtils.base64Encode(YamlUtils.serialize(key));
    }

    @Override
    public String serializeValue(@Nonnull Lock value) {
        return StrUtils.base64Encode(StrUtils.toJson(value));
    }

}
