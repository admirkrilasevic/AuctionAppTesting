package test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
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

}
