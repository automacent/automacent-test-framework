package ${package}.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.automacent.fwk.annotations.Action;
import com.automacent.fwk.core.PageObject;

public class GoogleSearchPage extends PageObject {

	@Override
	public PageValidation pageValidation() {
		// TODO Auto-generated method stub
		return null;
	}

	@FindBy(name = "q")
	private WebElement searchField;

	@FindBy(name = "btnK")
	private WebElement googleSearchButton;

	
	// ----------------------------------------------------
	
	@Action
	public void enterSearchKeyword(String keyword) {
		searchField.sendKeys(keyword);
	}

	@Action
	public void clickGoogleSearchButton() {
		googleSearchButton.click();
	}

}