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
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import main.Setup;

class HomePageTest {

	private static WebDriver webDriver;
	private static WebDriverWait wait;
	private static Setup setup;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\yoga\\OneDrive\\Software Verification Validation and Testing\\chromedriver.exe");
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
	void testCategoriesSection() throws InterruptedException {
		Thread.sleep(2000);
		List<WebElement> categories = webDriver.findElements(By.className("CategoriesSection_categoryButton__2oY_0"));
		
		for (int i = 0; i < categories.size()-1; i++) {
			Thread.sleep(2000);
			
			List<WebElement> refreshedCategories = webDriver.findElements(By.className("CategoriesSection_categoryButton__2oY_0"));
			String categoryName = refreshedCategories.get(i).getText();
			refreshedCategories.get(i).click();
			
			Thread.sleep(2000);
			
			WebElement categoryActiveFilter = webDriver.findElement(By.xpath("/html/body/div/div/div[3]/div/div/div[2]/div[1]/div/div/button"));
			String categoryFilterName = categoryActiveFilter.getText().split(" ")[0];
			assertEquals(categoryName, categoryFilterName);
			
			webDriver.navigate().back();
		}
		
		Thread.sleep(2000);
		List<WebElement> refreshedCategories = webDriver.findElements(By.className("CategoriesSection_categoryButton__2oY_0"));
		refreshedCategories.get(refreshedCategories.size()-1).click();
		String allCategoriesUrl = webDriver.getCurrentUrl();
		
		WebElement shopLink = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/div[3]/a[2]"));
		shopLink.click();
		String shopUrl = webDriver.getCurrentUrl();
		
		assertEquals(shopUrl, allCategoriesUrl);
	}
	
	@Test
	void testTabSwitching() {
		WebElement newArrivalsTab = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/div/ol/li[1]"));
		WebElement lastChanceTab = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/div/ol/li[2]"));
		
		assertEquals("tab-list-item tab-list-active", newArrivalsTab.getAttribute("class"));
		
		lastChanceTab.click();
		lastChanceTab = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/div/ol/li[2]"));
		assertEquals("tab-list-item tab-list-active", lastChanceTab.getAttribute("class"));
		
		newArrivalsTab.click();
		newArrivalsTab = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/div/ol/li[1]"));
		assertEquals("tab-list-item tab-list-active", newArrivalsTab.getAttribute("class"));
		
	}
	
	@Test
	void testNewArrivals() throws InterruptedException {
		int[] hoursLeft = new int[3];
		WebElement timeLeftForItem;
		String timeLeftWithText;
		int timeLeftInHours;
		
		Thread.sleep(2000);
		WebElement firstItem = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/div/div/div/div/div/div/div[1]/div/a[1]/img"));
		firstItem.click();
		Thread.sleep(2000);
		timeLeftForItem = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/div/div/div[2]/div[1]/p[3]/span"));
		timeLeftWithText = timeLeftForItem.getText();
		timeLeftInHours = convertTimeLeftToHours(timeLeftWithText);
		hoursLeft[0] = timeLeftInHours;
		webDriver.navigate().back();
		
		Thread.sleep(2000);
		WebElement secondItem = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/div/div/div/div/div/div/div[2]/div/a[1]/img"));
		secondItem.click();
		Thread.sleep(2000);
		timeLeftForItem = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/div/div/div[2]/div[1]/p[3]/span"));
		timeLeftWithText = timeLeftForItem.getText();
		timeLeftInHours = convertTimeLeftToHours(timeLeftWithText);
		hoursLeft[1] = timeLeftInHours;
		webDriver.navigate().back();

		Thread.sleep(2000);
		WebElement thirdItem = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/div/div/div/div/div/div/div[3]/div/a[1]/img"));
		thirdItem.click();
		Thread.sleep(2000);
		timeLeftForItem = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/div/div/div[2]/div[1]/p[3]/span"));
		timeLeftWithText = timeLeftForItem.getText();
		timeLeftInHours = convertTimeLeftToHours(timeLeftWithText);
		hoursLeft[2] = timeLeftInHours;
	
		assertTrue(hoursLeft[0] > hoursLeft[1]);
		assertTrue(hoursLeft[1] > hoursLeft[2]);
	}
	
	@Test
	void testLastChance() throws InterruptedException {
		
		int[] hoursLeft = new int[3];
		
		clickLastChanceTab();
		Thread.sleep(2000);
		WebElement firstItem = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/div/div/div/div/div/div/div[1]/div/a[1]/img"));
		firstItem.click();
		hoursLeft[0] = getTimeLeft();
		webDriver.navigate().back();
		
		clickLastChanceTab();
		Thread.sleep(2000);
		WebElement secondItem = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/div/div/div/div/div/div/div[2]/div/a[1]/img"));
		secondItem.click();
		hoursLeft[1] = getTimeLeft();
		webDriver.navigate().back();
		
		clickLastChanceTab();
		Thread.sleep(2000);
		WebElement thirdItem = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/div/div/div/div/div/div/div[3]/div/a[1]/img"));
		thirdItem.click();
		Thread.sleep(2000);
		hoursLeft[2] = getTimeLeft();
	
		assertTrue(hoursLeft[0] < hoursLeft[1]);
		assertTrue(hoursLeft[1] < hoursLeft[2]);
	}
	
	void clickLastChanceTab() throws InterruptedException {
		Thread.sleep(2000);
		WebElement lastChanceTab = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/div/ol/li[2]"));
		lastChanceTab.click();
	}
	
	int getTimeLeft() throws InterruptedException {
		Thread.sleep(2000);
		WebElement timeLeftForItem = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/div/div/div[2]/div[1]/p[3]/span"));
		String timeLeftWithText = timeLeftForItem.getText();
		int timeLeftInHours = convertTimeLeftToHours(timeLeftWithText);
		return timeLeftInHours;
	}
	
	int convertTimeLeftToHours(String timeLeft) {
		String[] elements = timeLeft.split("\\s+");
		int hours;
		System.out.println(elements[1]);
		if (elements[1].equals("weeks")) {
			hours = Integer.parseInt(elements[0]) * 168 + Integer.parseInt(elements[2]) * 24;
			return hours;
		} else if (elements[1].equals("days")) {
			hours = Integer.parseInt(elements[0]) * 24 + Integer.parseInt(elements[2]);
			return hours;
		} else {
			hours = Integer.parseInt(elements[0]);
			return hours;
		} 
	}

}
