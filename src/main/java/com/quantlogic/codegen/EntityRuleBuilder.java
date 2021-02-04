package com.quantlogic.codegen;

import com.quantlogic.builder.Builder;
import com.quantlogic.entity.Entity;
import com.quantlogic.entity.EntityRule;
import com.quantlogic.rules.EntityRuleSet;
import javassist.*;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class EntityRuleBuilder<T extends Entity, U extends Entity> implements Builder<EntityRule<T, U>> {
    protected final EntityRuleSet<T, U> entityRuleSet;
    protected final AtomicInteger ruleId = new AtomicInteger();
    private final ClassPool cp;
    private CtClass cc;


    public EntityRuleBuilder(EntityRuleSet<T, U> entityRuleSet) {
        this.cp = ClassPool.getDefault();
        cp.importPackage("com.quantlogic.entity.*");
        cp.importPackage("com.quantlogic.entity.Entity");
        cp.importPackage("java.util.function.Predicate");
        cp.importPackage("com.quantlogic.rules.FunctionPredicate");
        this.entityRuleSet = entityRuleSet;
    }

    public EntityRuleBuilder<T, U> withSourceAndTarget(Pair<String, String> src, Pair<String, String> target) throws CannotCompileException {
        CtField f = CtField.make("public " + src.getKey() + " " + src.getValue() + " ;", cc);
        cc.addField(f);
        f = CtField.make("public " + target.getKey() + " " + target.getValue() + " ;", cc);
        cc.addField(f);
        String meth = "public " + src.getKey() + " getSource(){return " + src.getValue() + " ;}";
        CtMethod method = CtNewMethod.make(meth, cc);
        cc.addMethod(method);

        meth = "public " + target.getKey() + " getDestination(){return " + target.getValue() + " ;}";
        method = CtNewMethod.make(meth, cc);
        cc.addMethod(method);

        meth = "public void from( com.quantlogic.entity.Entity  t , com.quantlogic.entity.Entity  u ) { this." + src.getValue() + " = ("+src.getKey()+")t ;" + " this." + target.getValue() + " = ("+target.getKey()+")u ;      }";
        method = CtNewMethod.make(meth, cc);
        cc.addMethod(method);

        return this;
    }

    public EntityRuleBuilder<T, U> withRuleName(String ruleName) throws NotFoundException {
        String clsName = ruleName.replaceAll("_", "");
        String classname = "com.quantlogic.codegen.rules.Rule" + clsName;
        this.cc = cp.makeClass(classname);
        this.cc.setInterfaces(new CtClass[]{cp.get("com.quantlogic.entity.EntityRule")});
        return this;
    }

    public EntityRuleBuilder<T, U> withRuleId(String ruleId) throws CannotCompileException {
        CtField f = CtField.make("public int ruleId = "  + ruleId + " ;", cc);
        cc.addField(f);
        String meth = "public int getRuleIdentifier(){return ruleId;}";
        CtMethod method = CtNewMethod.make(meth, cc);
        cc.addMethod(method);
        return this;
    }

    public EntityRuleBuilder<T, U> withRuleWeight(String ruleWeight) throws CannotCompileException {
        CtField f = CtField.make("public int ruleWeight = " +  ruleWeight   + ";", cc);
        cc.addField(f);
        String meth = "public int getRuleWeight(){return ruleWeight;}";
        CtMethod method = CtNewMethod.make(meth, cc);
        cc.addMethod(method);
        return this;
    }

    public EntityRuleBuilder<T, U> withPredicate(String predicate, String object) throws Exception {
        CtField f = CtField.make("public com.quantlogic.rules.FunctionPredicate fpredicate ;", cc);
        cc.addField(f);

        CtMethod method = CtMethod.make("public com.quantlogic.rules.FunctionPredicate getFunctionPredicate() {return new com.quantlogic.rules.FunctionPredicate("+ predicate+ " , " + object+" );}", cc);
        cc.addMethod(method);

        String meth = "public java.util.function.Predicate getPredicate(){ return getFunctionPredicate().getPredicate() ;} ";
        method = CtMethod.make(meth, cc);
        cc.addMethod(method);

        meth = "public java.util.function.Function getKeyTransform(){ return getFunctionPredicate().getFunction() ;} ";
        method = CtMethod.make(meth, cc);
        cc.addMethod(method);


        return this;
    }

    public void writeFile(){
        try {
            cc.writeFile("target/classes");
        } catch (CannotCompileException | IOException e) {
            e.printStackTrace();
        }
    }

    protected void addRuleToSet(EntityRule<T, U> rule) {
        this.entityRuleSet.addEntityRule(rule);
    }

    @Override
    public EntityRule<T, U> build()  {
        EntityRule rule = null;
        try {
            rule = (EntityRule) this.cc.toClass().newInstance();
        } catch (InstantiationException | IllegalAccessException | CannotCompileException e) {
            e.printStackTrace();
        }
        addRuleToSet(rule);
        return rule;
    }

}
