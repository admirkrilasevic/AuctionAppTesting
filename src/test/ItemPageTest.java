package test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.*;
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
	@Order(1)
	void testItemHeaderAndTitle() throws InterruptedException {
		ClickShop();
		List<WebElement> itemList = webDriver.findElements(By.className("Item_itemContainer__uYG6J"));
		List<WebElement> products = new ArrayList<>();

		for(int i = 1; i < itemList.size() + 1; i++) {
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
	@Order(2)
	void testBid() throws InterruptedException{
		ClickShop();
		List<WebElement> itemList = webDriver.findElements(By.className("Item_itemContainer__uYG6J"));
		List<WebElement> products = new ArrayList<>();

		for(int i = 1; i < itemList.size() + 1; i++) {
			String xpath = String.format("/html/body/div/div/div[3]/div/div/div[2]/div[3]/div[%d]", i);
			webDriver.findElement(By.xpath(xpath)).click();
			Thread.sleep(1000);
			WebElement bidButton = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[3]/div/div[1]/div[2]/div[2]/div[2]/button"));
			assertNotEquals("PLACE BID", bidButton.getText());
			webDriver.navigate().back();
			Thread.sleep(1000);
		}

		ClickShop();
		WebElement login = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[1]/nav/p/a[1]"));
		login.click();
		Thread.sleep(1000);

		WebElement email = webDriver.findElement(By.name("email"));
		WebElement password = webDriver.findElement(By.name("password"));
		email.sendKeys("matej.mujezinovic@gmail.com");
		password.sendKeys("123456789");
		Thread.sleep(1000);

		ClickShop();
		for(int i = 1; i < itemList.size() + 1; i++) {
			String xpath = String.format("/html/body/div/div/div[3]/div/div/div[2]/div[3]/div[%d]", i);
			webDriver.findElement(By.xpath(xpath)).click();
			Thread.sleep(1000);
			WebElement bidButton = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[3]/div/div[1]/div[2]/div[2]/div[2]/button"));
			assertEquals("PLACE BID", bidButton.getText());
			webDriver.navigate().back();
			Thread.sleep(1000);
		}
	}

	@Test
	@Order(3)
	void testBidPrices() throws InterruptedException{
		ClickShop();
		List<WebElement> itemList = webDriver.findElements(By.className("Item_itemContainer__uYG6J"));
		List<WebElement> products = new ArrayList<>();

		for(int i = 1; i < itemList.size() + 1; i++) {
			String xpath = String.format("/html/body/div/div/div[3]/div/div/div[2]/div[3]/div[%d]", i);
			webDriver.findElement(By.xpath(xpath)).click();
			Thread.sleep(1000);

			Double startBid = Double.valueOf(Integer.parseInt(webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/div/div[1]/div[2]/p/span")).getText().replaceAll("[^0-9]", "")));
			Double highestBid = Double.valueOf(Integer.parseInt(webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/div/div[1]/div[2]/div[1]/p[1]/span")).getText().replaceAll("[^0-9]", "")));
			Double enterBid = Double.valueOf(Integer.parseInt(webDriver.findElement(By.cssSelector("input.ItemOverview_bidInput__3ic45")).getAttribute("value").replaceAll("[^0-9]", "")));
			Boolean verifyStartBid;

			if(enterBid > startBid && enterBid > highestBid){
				verifyStartBid = true;
			}

			else{
				verifyStartBid = false;
			}

			assertTrue(verifyStartBid);
			webDriver.navigate().back();
			Thread.sleep(1000);
		}
	}

	@Test
	@Order(4)
	void testBiddingProcess() throws InterruptedException{
		ClickShop();
		List<WebElement> itemList = webDriver.findElements(By.className("Item_itemContainer__uYG6J"));
		List<WebElement> products = new ArrayList<>();

		for(int i = 1; i < itemList.size() + 1; i++) {
			String xpath = String.format("/html/body/div/div/div[3]/div/div/div[2]/div[3]/div[%d]", i);
			webDriver.findElement(By.xpath(xpath)).click();
			Thread.sleep(3000);

			Double startBid = Double.valueOf(Integer.parseInt(webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/div/div[1]/div[2]/p/span")).getText().replaceAll("[^0-9]", "")));
			Double highestBid = Double.valueOf(Integer.parseInt(webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/div/div[1]/div[2]/div[1]/p[1]/span")).getText().replaceAll("[^0-9]", "")));
			Double valueToUse = GetBiggerDouble(startBid, highestBid);
			Double lesserBid = valueToUse - 0.1;
			Double higherBid = valueToUse + 0.1;

			WebElement bidPlaceholder = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/div/div[1]/div[2]/div[2]/div[1]/input"));
			WebElement bidButton = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[3]/div/div[1]/div[2]/div[2]/div[2]/button"));

			bidPlaceholder.sendKeys(String.valueOf(lesserBid));
			bidButton.click();
			Thread.sleep(3000);

			WebElement failAlert = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]"));
			assertEquals("There are higher bids than yours. You could give a second try!", failAlert.getText());
			Thread.sleep(3000);

			bidPlaceholder.sendKeys(String.valueOf(higherBid));
			bidButton.click();

			WebElement alertSuccess = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]"));
			assertEquals("Congrats! You are the highest bidder!", alertSuccess.getText());
			webDriver.navigate().back();
			Thread.sleep(1000);
		}
	}

	void ClickShop(){
		WebElement shopButton = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/div[3]/a[2]"));
		shopButton.click();
	}

	Double GetBiggerDouble(Double a, Double b){
		if(a > b){
			return a;
		}

		else{
			return b;
		}
	}
}
