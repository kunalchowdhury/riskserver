package com.quantlogic.marketdata;

import javassist.CannotCompileException;
import javassist.NotFoundException;

import java.io.IOException;

public class A implements IntA{
    B b = new B();
    public int fun(){
        try {
            System.out.println("INSIDE");
            b.foo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 1;

    }
}
