package com.quantlogic.entity.metadata;

import com.quantlogic.entity.BlackVarianceVolatilitySurface;

public class BlackVarianceVolSurfaceDelta implements DeltaEntity<BlackVarianceVolatilitySurface>{
    private final String fieldName;
    private final String fieldValue;
    private final int[] fieldIndexes ;
    private final String[] fieldValues;
    private final boolean fieldUpdate;

    public BlackVarianceVolSurfaceDelta(String fieldName, String fieldValue, int[] fieldIndexes, String[] fieldValues, boolean fieldUpdate) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.fieldIndexes = fieldIndexes;
        this.fieldValues = fieldValues;
        this.fieldUpdate = fieldUpdate;
    }

    @Override
    public String getEntityName() {
        return "com.quantlogic.entity.BlackVarianceVolatilitySurface";
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String getFieldValue() {
        return fieldValue;
    }

    @Override
    public int[] getFieldIndexes() {
        return fieldIndexes;
    }

    @Override
    public String[] getFieldValues() {
        return fieldValues;
    }

    public boolean isFieldUpdate() {
        return fieldUpdate;
    }
}
