package fr.ifremer.nemo.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
		{ 	
			// reinitialize resources (from Nemo such as NemoConfiguration, and external resources such as BODC vocabs and CSR)
			UpdateResourcesTest.class,
			// test on BODC checks
			BodcTest.class,
			// check Nemo conf
			MiscTest.class,
			// regression tests
			NemoBashTest.class 
			})
public class SuiteTests {

}
