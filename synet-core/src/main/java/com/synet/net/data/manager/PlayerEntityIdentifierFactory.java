package com.synet.net.data.manager;

import com.synet.net.data.context.StateEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * 玩家id标识
 *
 * 默认采用不存在的时候使用playerId,如果指定则优先使用指定的字段名
 *
 */
public class PlayerEntityIdentifierFactory {

    private final String DEFAULT_PLAYER_IDENTIFIER = "palyerId";

    private Map<Class<? extends StateEntity>, String> identifiers;

    public PlayerEntityIdentifierFactory(Map<Class<? extends StateEntity>, String> identifiers) {
        this.identifiers = identifiers;
    }

    /**
     * 获取identifier
     *
     * @param clazz
     * @return
     */
    public String getIdentifier(Class<? extends StateEntity> clazz) {
        String identifier = identifiers.get(clazz);
        return StringUtils.isBlank(identifiers.get(clazz)) ? DEFAULT_PLAYER_IDENTIFIER : identifier;
    }

}
