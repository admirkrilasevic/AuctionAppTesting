package test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import main.Setup;

class ItemPageTest {

	private static WebDriver webDriver;
	private static WebDriverWait wait;
	private static Setup setup;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
		webDriver = new ChromeDriver();
		webDriver.manage().window().maximize();
		wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
		setup = new Setup(webDriver, wait);
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		webDriver.close();
	}

	@BeforeEach
	void setUp() throws Exception {
		setup.startApplication();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testItemHeaderAndTitle() throws InterruptedException {
		WebElement shopButton = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/div[3]/a[2]"));
		shopButton.click();
		assertEquals("https://auctionapp.krilasevic.me/shop/0", webDriver.getCurrentUrl());
		Thread.sleep(2000);


		List<WebElement> itemList = webDriver.findElements(By.className("Item_itemContainer__uYG6J"));
		System.out.println(itemList.size());

		List<WebElement> products = new ArrayList<>();

		for(int i = 1; i < itemList.size() + 1; i++) {
			String xpath = String.format("/html/body/div/div/div[3]/div/div/div[2]/div[3]/div[%d]", i);
			webDriver.findElement(By.xpath(xpath)).click();
			Thread.sleep(1000);
			WebElement headerName = webDriver.findElement(By.xpath("/html/body/div[1]/div/div[3]/div[1]/p"));
			WebElement itemName = webDriver.findElement(By.xpath("/html/body/div[1]/div/div[3]/div[2]/div/div/div[2]/h3"));
			System.out.println(headerName.getText() + " " + itemName.getText());
			assertEquals(headerName.getText(), itemName.getText());
			Thread.sleep(1000);
			webDriver.navigate().back();
			Thread.sleep(1000);
		}
	}
}
