package com.quantlogic.marketdatarepository;

import com.quantlogic.common.entity.CacheKey;
import com.quantlogic.common.entity.NamedTimedEntity;

public interface MarketDataDAO<T extends CacheKey, U extends NamedTimedEntity> {
    void save(T key, U value);
    U get(T key);
}
