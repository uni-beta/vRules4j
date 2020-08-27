package test.com.unibeta.vrules.utils;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

import test.cases.Father;
import test.cases.Son;
import test.com.unibeta.vrules.BasicTester;
import test.com.unibeta.vrules.test.ObjectCloneFactory;



public class ObjectCloneFactoryTester extends BasicTester {

    public ObjectCloneFactoryTester(String string) {

        super(string);
        // TODO Auto-generated constructor stub
    }

    public void testCloneObject() throws Exception, InvocationTargetException{
        ObjectCloneFactory cloneFactory = new ObjectCloneFactory();
        Son son = new Son();
        son.setName("jordan");
        
        //cloneFactory.set(son);
        //Son s = (Son)cloneFactory.get();
        BeanUtils.copyProperties(new Son(), father);
     
        
    }
}
