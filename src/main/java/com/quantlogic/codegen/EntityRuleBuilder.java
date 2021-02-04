package com.quantlogic.codegen;

import com.quantlogic.builder.Builder;
import com.quantlogic.entity.Entity;
import com.quantlogic.entity.EntityRule;
import com.quantlogic.entity.Instrument;
import com.quantlogic.entity.Volatility;
import com.quantlogic.rules.DefaultInstrumentVolatilityRuleSet;
import com.quantlogic.rules.EntityRuleSet;
import javassist.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class EntityRuleBuilder<T extends Entity, U extends Entity> implements Builder<EntityRule<T, U>> {
    protected final EntityRuleSet<T, U> entityRuleSet;
    protected final AtomicInteger ruleId = new AtomicInteger();
    private final ClassPool cp;
    public CtClass cc;


    public EntityRuleBuilder(EntityRuleSet<T, U> entityRuleSet) {
        this.cp = ClassPool.getDefault();
        cp.importPackage("com.quantlogic.entity.*");
        cp.importPackage("com.quantlogic.entity.Entity");
        cp.importPackage("java.util.function.Predicate");
        cp.importPackage("com.quantlogic.rules.FunctionPredicate");
        this.entityRuleSet = entityRuleSet;
    }

    public static void main(String[] args) throws Exception {
        EntityRuleBuilder<Instrument, Volatility> entityRuleBuilder = new EntityRuleBuilder<Instrument, Volatility>(new DefaultInstrumentVolatilityRuleSet(1)) {
            @Override
            protected EntityRule<Instrument, Volatility> buildRule() {
                return null;
            }
        };
        entityRuleBuilder.withRuleName("Test100");
        entityRuleBuilder.withRuleId("100");
        entityRuleBuilder.withRuleWeight("2");
        entityRuleBuilder.withSourceAndTarget(Pair.of("com.quantlogic.entity.VanillaOption", "instrument"),
                Pair.of("com.quantlogic.entity.BlackVarianceVolatilitySurface", "volatility"));
        String s = "getSource().getTickerSymbol().equals(String.valueOf(\"OTC123\"))";
        String s1 = "com.quantlogic.util.VolKeygenUtil.getFlatVolKey(String.valueOf(\"QQQ\"),String.valueOf(\"20201202\"),String.valueOf(\".SPX\"))";
        entityRuleBuilder.withPredicate(s, s1);
        entityRuleBuilder.cc.writeFile("target/classes");

        EntityRule er = (EntityRule) entityRuleBuilder.cc.toClass().newInstance();
    }

    public EntityRuleBuilder<T, U> withSourceAndTarget(Pair<String, String> src, Pair<String, String> target) throws CannotCompileException {
        Predicate p = new Predicate() {
            @Override
            public boolean test(Object o) {
                return false;
            }
        };
        String type = src.getKey();
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

        meth = "public void from( Entity  t , Entity  u ) { this." + src.getValue() + " = ("+src.getKey()+")t ;" + " this." + target.getValue() + " = ("+target.getKey()+")u ;      }";
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

    protected void addRuleToSet(EntityRule<T, U> rule) {
        this.entityRuleSet.addEntityRule(rule);
    }

    @Override
    public EntityRule<T, U> build() {
        EntityRule<T, U> rule = buildRule();
        addRuleToSet(rule);
        return rule;
    }

    protected abstract EntityRule<T, U> buildRule();

    static class MyF implements Function{
        @Override
        public Object apply(Object o) {
            return null;
        }
    }
}
