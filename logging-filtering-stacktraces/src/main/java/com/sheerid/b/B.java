package com.sheerid.b;

import com.sheerid.c.C;

public class B {
    public static void doit() {
        try {
            C.doit();
        } catch (Exception e) {
            throw new RuntimeException("error calling C.doit", e);
        }
    }
}
