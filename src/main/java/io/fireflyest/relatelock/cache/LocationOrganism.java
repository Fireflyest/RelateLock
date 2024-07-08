package io.fireflyest.relatelock.cache;

import javax.annotation.Nonnull;
import org.bukkit.Location;
import io.fireflyest.relatelock.util.StrUtils;
import io.fireflyest.relatelock.util.YamlUtils;

/**
 * 数据缓存组织实现类
 * 
 * @author Fireflyest
 * @since 1.0
 */
public final class LocationOrganism extends AbstractOrganism<Location, Location> {

    public LocationOrganism(String name) {
        super(name);
    }

    @Override
    public Location deserializeKey(@Nonnull String keyStr) {
        return YamlUtils.deserialize(StrUtils.base64Decode(keyStr), Location.class);
    }

    @Override
    public Location deserializeValue(@Nonnull String valueStr) {
        return YamlUtils.deserialize(StrUtils.base64Decode(valueStr), Location.class);
    }

    @Override
    public String serializeKey(@Nonnull Location key) {
        return StrUtils.base64Encode(YamlUtils.serialize(key));
    }

    @Override
    public String serializeValue(@Nonnull Location value) {
        return StrUtils.base64Encode(YamlUtils.serialize(value));
    }

}
