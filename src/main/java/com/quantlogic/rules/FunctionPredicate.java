package com.quantlogic.rules;

import java.util.function.Function;
import java.util.function.Predicate;

public class FunctionPredicate {

    private final SimpleFunction function;
    private final SimplePredicate predicate;

    public FunctionPredicate(boolean val, Object o) {
        this.function = new SimpleFunction(o);
        this.predicate = new SimplePredicate(val);
    }

    public SimpleFunction getFunction() {
        return function;
    }

    public SimplePredicate getPredicate() {
        return predicate;
    }

    private static class SimpleFunction implements Function{
        private Object o;
        public SimpleFunction(Object o) {
            this.o = o;
        }
        @Override
        public Object apply(Object o) {
            return this.o;
        }
    }

    private static class SimplePredicate implements Predicate{
        private boolean val;

        private SimplePredicate(boolean val) {
            this.val = val;
        }
        @Override
        public boolean test(Object o) {
            return this.val;
        }
    }
}
