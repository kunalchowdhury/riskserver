package com.quantlogic.codegen;

import javassist.*;

public class EntityRuleBuilder {
    private String clsName;
    public CtClass cc ;
    private ClassPool cp ;

    public EntityRuleBuilder() {
        this.cp = ClassPool.getDefault();
        cp.importPackage("com.quantlogic.entity");
    }

    public EntityRuleBuilder withRuleName(String ruleName) throws NotFoundException {
        this.clsName = ruleName.replaceAll("_", "");
        this.cc = cp.makeClass("com.quantlogic.codegen.rules.Rule"+clsName);
        this.cc.setInterfaces(new CtClass[]{cp.get("com.quantlogic.entity.EntityRule")});
        return this;
    }

    public EntityRuleBuilder withRuleId(String ruleId) throws CannotCompileException {
        String ruleIdent = "public int getRuleIdentifier(){ return "+ruleId+" ; " ;
        CtMethod method = CtNewMethod.make(ruleIdent, cc);
        cc.addMethod(method);
        return this;
    }

    public EntityRuleBuilder withRuleWeight(String ruleWeight) throws CannotCompileException {
        String ruleWeightMeth = "public int getRuleWeight(){ return "+ruleWeight+" ; " ;
        CtMethod method = CtNewMethod.make(ruleWeightMeth, cc);
        cc.addMethod(method);
        return this;
    }

}
