package test.com.unibeta.vrules.engines.dccimpls;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import test.com.unibeta.vrules.BasicTester;

import com.unibeta.vrules.engines.dccimpls.DynamicCompiler;
import com.unibeta.vrules.engines.dccimpls.ValidationClassLoader;

public class DynamicCompilerTester extends BasicTester {

    public DynamicCompilerTester(String string) {

        super(string);
        // TODO Auto-generated constructor stub
    }

    public void Compile() throws Exception {

        String classesPath = "D:/eclipse/V-rules/doc/bin/";
        DynamicCompiler.compile("D:/eclipse/V-rules/doc/Test.java");
        // logger.info("file name is " + file.getAbsolutePath());

        try {
            File file = new File(classesPath);
            // Class c = Class.forName("test.com.unibeta.vrules.compiler.Test");
            ClassLoader loader = ValidationClassLoader.getSystemClassLoader();// c.getClassLoader();
            URLClassLoader urlLoader = new URLClassLoader(new URL[] { file
                    .toURL() }, loader);
            Class clazz = urlLoader
                    .loadClass("test.com.unibeta.vrules.compiler.Test");
            Object obj = clazz.newInstance();
            Method method = clazz.getMethod("hello", new Class[] {});
            method.invoke(obj, null);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void testFindWebInfPath() {

        // String path =
        // DynamicCompiler.findWebInfoPath("D:\\eclipse\\uni-beta\\build\\ROOT\\aboutUs\\");
        // logger.info("the file path is " + path);
    }

}
