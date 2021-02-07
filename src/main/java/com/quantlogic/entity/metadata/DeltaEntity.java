package com.quantlogic.entity.metadata;

import com.quantlogic.entity.Entity;

public interface DeltaEntity<T extends Entity> {
    String getEntityName();
    String getFieldName();
    String getFieldValue();
    int[] getFieldIndexes();
    String[] getFieldValues();
    boolean isFieldUpdate();
}
