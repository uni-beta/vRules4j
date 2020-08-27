package test.com.unibeta.vrules.utils;

import java.util.ArrayList;
import java.util.List;

import com.unibeta.vrules.utils.CommonUtils;

import test.com.unibeta.vrules.BasicTester;


public class CommonUtilsTester extends BasicTester {

    public CommonUtilsTester(String string) {

        super(string);
        // TODO Auto-generated constructor stub
    }

    public void testFetchIncludesFileNames(){
       String str[]= CommonUtils.fetchIncludesFileNames("../doc1/FatherRules.xml", "D:/eclipse/V-rules/doc/validationRulesForDcc.xml");
       
       for(int i = 0; i<str.length;i++){
           logger.info(str[i]+",");
       }
    }
    
    public void testCommon(){
        logger.info(CommonUtils.isInterfaceOf(ArrayList.class, List.class)+"");
    }
    
    public void testFindFilesUnder(){
        List<String> list = CommonUtils.searchFilesUnder("D:\\eclipse\\vRules-3.x","WEB-INF" , "class", true);
        
        for(String s: list){
            System.out.println(s);
//            logger.info(s);
        }
    }
}
