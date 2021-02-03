package com.quantlogic.rules;

import javassist.*;

import java.util.function.Predicate;

public class RulePredicate implements Predicate {
    private String s ;
    private ClassPool cp ;

    public RulePredicate(String s) throws NotFoundException, CannotCompileException {
        this.s = s;
        this.cp = ClassPool.getDefault();
        CtClass ctClass = cp.get("com.quantlogic.rules.RulePredicate");
        CtMethod.make(s, ctClass);
    }

    @Override
    public boolean test(Object o) {
        return false;
    }
}
