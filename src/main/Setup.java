package main;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Setup {
	
	private static WebDriver webDriver;
	private static WebDriverWait wait;
	
	public Setup (WebDriver webDriver, WebDriverWait wait) {
		Setup.webDriver = webDriver;
		Setup.wait = wait;
		
	}
	
	//App server needs to be started, before working with the app itself
	public void startApplication() {
		webDriver.get("https://auction-app-abh-server.herokuapp.com");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/h1")));
		webDriver.get("https://auctionapp.krilasevic.me");
	}

}
