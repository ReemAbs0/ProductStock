package main.test;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

import main.test.ProductStockTest;



@SuiteDisplayName("Test Methods")
@Suite
//@SelectPackages("main.test")
@SelectClasses({ProductStockTest.class})
@IncludeTags({"sanity","regression"})
public class TestSuite {

}
