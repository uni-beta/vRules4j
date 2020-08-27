package test.com.unibeta.vrules;

import java.util.ArrayList;
import java.util.Collection;

import com.unibeta.vrules.engines.dccimpls.interpreter.InterpreterUtils;

import bsh.Interpreter;

public class TestMain {

    static String hello;

    void call() {

    }

    static class SubClass {

        String test;
        public void sayHello() {
System.out.println(SubClass.class.getName());
            System.out.println(InterpreterUtils.getDeclaredFieldType(this.getClass().getPackage().getName()+".TestMain$"+this.getClass().getSimpleName(), "test"));
        }
    }

    public static void main(String[] args) {

        try {
            String className = Boolean.class.getCanonicalName();
            Collection<Object> c = new ArrayList();
            boolean b = true;
            c.add(b);
            
            //InterpreterUtils.getValueFromCollectionByClassName(c , className);
            Interpreter bsh = new Interpreter();
            InterpreterUtils.bshSet(bsh, "name", "jordan");
			Object o = InterpreterUtils.bshEval(bsh, "name is: #{name}, age is:#{1+1}");
            
            System.out.println(o);
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
