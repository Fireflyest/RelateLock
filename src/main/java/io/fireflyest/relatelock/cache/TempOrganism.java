package io.fireflyest.relatelock.cache;

import org.bukkit.entity.Player;
import io.fireflyest.relatelock.bean.Lock;

/**
 * 临时权限
 * 
 * @author Fireflyest
 * @since 1.0
 */
public class TempOrganism extends AbstractOrganism<Player, Lock> {

    public TempOrganism(String name) {
        super(name);
    }
    
}
