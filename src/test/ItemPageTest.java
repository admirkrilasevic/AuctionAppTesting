package test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
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
		setup.startServer();
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
		openShopPage();
		List<WebElement> itemList = webDriver.findElements(By.className("Item_itemContainer__uYG6J"));

		for (int i = 1; i < itemList.size() + 1; i++) {
			//Locate xpath of next element in iteration
			String xpath = String.format("/html/body/div/div/div[3]/div/div/div[2]/div[3]/div[%d]", i);
			webDriver.findElement(By.xpath(xpath)).click();
			Thread.sleep(1000);

			WebElement headerName = webDriver.findElement(By.xpath("/html/body/div[1]/div/div[3]/div[1]/p"));
			WebElement itemName = webDriver.findElement(By.xpath("/html/body/div[1]/div/div[3]/div[2]/div/div/div[2]/h3"));
			assertEquals(headerName.getText(), itemName.getText());
			webDriver.navigate().back();
			Thread.sleep(1000);
		}
	}

	@Test
	void testIfUserCanPlaceBid() throws InterruptedException{
		openShopPage();
		List<WebElement> itemList = webDriver.findElements(By.className("Item_itemContainer__uYG6J"));

		for (int i = 1; i < itemList.size() + 1; i++) {
			String xpath = String.format("/html/body/div/div/div[3]/div/div/div[2]/div[3]/div[%d]", i);
			webDriver.findElement(By.xpath(xpath)).click();
			Thread.sleep(1000);
			assertThrows(NoSuchElementException.class,
					() -> {webDriver.findElement(By.xpath("/html/body/div/div/div[3]/div[2]/div/div[1]/div[2]/div[2]/div[2]/button"));});
			webDriver.navigate().back();
			Thread.sleep(1000);
		}

		doLogin();
		openShopPage();
		
		for(int i = 1; i < itemList.size() + 1; i++) {
			String xpath = String.format("/html/body/div/div/div[3]/div/div/div[2]/div[3]/div[%d]", i);
			webDriver.findElement(By.xpath(xpath)).click();
			Thread.sleep(1000);
			WebElement bidButton = webDriver.findElement(By.xpath("/html/body/div/div/div[3]/div[2]/div/div[1]/div[2]/div[2]/div[2]/button"));
			assertEquals("PLACE BID", bidButton.getText());
			webDriver.navigate().back();
			Thread.sleep(1000);
		}
		doLogout();
	}

	@Test
	void testBidPlaceholder() throws InterruptedException{
		doLogin();
		openShopPage();
		List<WebElement> itemList = webDriver.findElements(By.className("Item_itemContainer__uYG6J"));

		for(int i = 1; i < itemList.size() + 1; i++) {
			String xpath = String.format("/html/body/div/div/div[3]/div/div/div[2]/div[3]/div[%d]", i);
			webDriver.findElement(By.xpath(xpath)).click();
			Thread.sleep(1000);

			Double startBid = Double.parseDouble(webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/div/div[1]/div[2]/p/span")).getText().substring(1));
			Double highestBid = Double.parseDouble(webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/div/div[1]/div[2]/div[1]/p[1]/span")).getText().substring(1));
			Double placeholderBid = Double.parseDouble(webDriver.findElement(By.cssSelector("input.ItemOverview_bidInput__3ic45")).getAttribute("placeholder").split(" ")[1].substring(1));
			Boolean verifyStartBid;

			if (placeholderBid > startBid && placeholderBid > highestBid) {
				verifyStartBid = true;
			} else {
				verifyStartBid = false;
			}

			assertTrue(verifyStartBid);
			webDriver.navigate().back();
			Thread.sleep(1000);
		}
		doLogout();
	}

	@Test
	void testBiddingProcess() throws InterruptedException{
		doLogin();
		openShopPage();
		List<WebElement> itemList = webDriver.findElements(By.className("Item_itemContainer__uYG6J"));

		for(int i = 1; i < itemList.size() + 1; i++) {
			String xpath = String.format("/html/body/div/div/div[3]/div/div/div[2]/div[3]/div[%d]", i);
			webDriver.findElement(By.xpath(xpath)).click();
			Thread.sleep(3000);

			Double startBid = Double.parseDouble(webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/div/div[1]/div[2]/p/span")).getText().substring(1));
			Double highestBid = Double.parseDouble(webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/div/div[1]/div[2]/div[1]/p[1]/span")).getText().substring(1));
			Double valueToUse = getBiggerDouble(startBid, highestBid);
			Double lesserBid = valueToUse - 0.1;
			Double higherBid = valueToUse + 0.1;

			WebElement bidPlaceholder = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/div/div[1]/div[2]/div[2]/div[1]/input"));
			WebElement bidButton = webDriver.findElement(By.xpath("/html/body/div/div/div[3]/div[2]/div/div[1]/div[2]/div[2]/div[2]/button"));

			bidPlaceholder.sendKeys(String.valueOf(lesserBid));
			bidButton.click();
			Thread.sleep(3000);

			WebElement failAlert = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]"));
			assertEquals("There are higher bids than yours. You could give a second try!", failAlert.getText());
			Thread.sleep(3000);
			
			bidPlaceholder.clear();
			bidPlaceholder.sendKeys(String.valueOf(higherBid));
			bidButton.click();
			Thread.sleep(3000);

			WebElement alertSuccess = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]"));
			assertEquals("Congrats! You are the highest bidder!", alertSuccess.getText());
			webDriver.navigate().back();
			Thread.sleep(1000);
		}
		doLogout();
	}
	
	@Test
	void testBreadcrumbs() throws InterruptedException {
		Thread.sleep(2000);
		WebElement homePageItem = webDriver.findElement(By.className("Item_itemContainer__uYG6J"));
		homePageItem.click();
		Thread.sleep(2000);
		
		WebElement previousPageInBreadcrumbs = webDriver.findElement(By.className("ItemOverview_breadcrumbsLink__1bkCB"));
		assertEquals("Home", previousPageInBreadcrumbs.getText());
		
		openShopPage();
		
		WebElement shopPageItem = webDriver.findElement(By.className("Item_itemContainer__uYG6J"));
		shopPageItem.click();
		Thread.sleep(2000);
		
		previousPageInBreadcrumbs = webDriver.findElement(By.className("ItemOverview_breadcrumbsLink__1bkCB"));
		assertEquals("Shop", previousPageInBreadcrumbs.getText());
	}

	void openShopPage() throws InterruptedException {
		WebElement shopLink = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/div[3]/a[2]"));
		shopLink.click();
		Thread.sleep(2000);
	}

	Double getBiggerDouble(Double a, Double b) {
		if(a > b) {
			return a;
		} else {
			return b;
		}
	}
	
	void doLogin() throws InterruptedException {
		WebElement login = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[1]/nav/p/a[1]"));
		login.click();
		Thread.sleep(2000);
		WebElement email = webDriver.findElement(By.name("email"));
		WebElement password = webDriver.findElement(By.name("password"));
		email.sendKeys("matej.mujezinovic@gmail.com");
		password.sendKeys("123456789");
		WebElement loginButton = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/form/button"));
		loginButton.click();
		Thread.sleep(2000);
	}
	
	void doLogout() throws InterruptedException {
		WebElement logout = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[1]/nav/p/a[1]"));
		logout.click();
		Thread.sleep(2000);
	}
}
