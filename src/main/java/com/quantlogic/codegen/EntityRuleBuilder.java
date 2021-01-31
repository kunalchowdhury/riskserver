package com.quantlogic.codegen;

import javassist.ClassPool;
import javassist.CtClass;

public class EntityRuleBuilder {
    private final String clsName;
    public CtClass cc ;
    private ClassPool cp ;

    public EntityRuleBuilder(String clsName) {
        this.clsName = clsName;
    }
}
