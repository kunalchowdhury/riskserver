package com.quantlogic.marketdata;

import javassist.*;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class B {

    ClassPool pool;

    private Map<Pair<String, String>, Boolean> registeredMap = new HashMap<>();
    public B() {
        pool = new ClassPool(true);
        try {
            pool.insertClassPath("C:\\Users\\kunal\\IdeaProjects\\marketdata\\target\\classes\\");
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        pool.importPackage("com.quantlogic.marketdata");
    }

    void foo() throws IOException, CannotCompileException, NotFoundException, ClassNotFoundException, NoSuchMethodException {
       /* CtClass cc = pool.getAndRename("com.quantlogic.marketdata.Point", "com.quantlogic.marketdata.PointProxy");
        CtMethod m = cc.getDeclaredMethod("move");
        m.insertBefore("{ System.out.println($1); System.out.println($2); }");
        cc.writeFile();*/
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        boolean foundTarget = false;
        StackTraceElement targetStack = null;
        for (StackTraceElement stackTraceElement : stackTrace) {
            if (foundTarget) {
                targetStack = stackTraceElement;
                break;
            } else if (stackTraceElement.getClassName().equals(this.getClass().getName())) {
                foundTarget = true;
            }
        }

        System.out.println("Got into foo");
        Pair<String, String> pair = Pair.of(Objects.requireNonNull(targetStack).getClassName(), targetStack.getMethodName());
        if(registeredMap.containsKey(pair) && registeredMap.get(pair)){
            return;
        }
        Triple<IntA, Method, Object[]> methodInfo =
                MethodInvocationRegistry.getMethodInfo(Objects.requireNonNull(targetStack).getClassName(), targetStack.getMethodName());
        try {
            System.out.println("AND FINALLY ...");
            registeredMap.put(pair, true);
            methodInfo.getMiddle().invoke(methodInfo.getLeft(), methodInfo.getRight());

        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

      /*  URL resource = this.getClass().getResource(targetStack.getFileName().replace("java", "class"));
        CtClass cc = null;
        cc = pool.makeClass(new FileInputStream(resource.getPath()));

        Method declaredMethod = Class.forName(targetStack.getClassName()).getDeclaredMethod(targetStack.getMethodName());


        CtMethod m = null;
        try {
            m = Objects.requireNonNull(cc).getDeclaredMethod(targetStack.getMethodName());

        } catch (NotFoundException e) {
            e.printStackTrace();
        }

        try {
            Objects.requireNonNull(m).insertAt(targetStack.getLineNumber(), "{ System.out.println(); }");
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
        cc.debugWriteFile("C:\\Users\\kunal\\A.java");*/

    }
}
