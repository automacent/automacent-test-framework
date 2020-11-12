package ${package}.test;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.automacent.fwk.annotations.Steps;
import com.automacent.fwk.core.BaseTest;
import com.automacent.fwk.core.BaseTestSelenium;
import ${package}.step.GoogleSearchSteps;

public class GoogleSearchTest extends BaseTestSelenium {

	@Steps
	private GoogleSearchSteps googleSearchSteps;

	@BeforeTest
	public void setupBrowserAndOpenUrl() {
		startBrowser();
		getActiveDriver().get(BaseTest.getTestObject().getBaseUrl());
	}

	@AfterTest
	public void stopBrowserAndCleanup() {
		quitBrowser();
	}

	@Test
	@Parameters({ "keyword" })
	public void googleSearchTest(String keyword) {
		googleSearchSteps.performGoogleSearch(keyword);
	}

}