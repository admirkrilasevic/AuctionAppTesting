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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import main.Setup;

class ShopPageTest {

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
	void testCategoryFilters() throws InterruptedException {
		openShopPage();
		
		List<WebElement> categories = webDriver.findElements(By.className("Category_collapsibleCategory__3wERX"));
		
		for (int i = 0; i < categories.size(); i++) {
			WebElement currentCategory = categories.get(i);
			String categoryName = currentCategory.getText().split("\\s+")[0];
			currentCategory.click();
			
			WebElement categoryActiveFilter = webDriver.findElement(By.xpath("/html/body/div/div/div[3]/div/div/div[2]/div[1]/div/div/button"));
			String categoryFilterName = categoryActiveFilter.getText().split(" ")[0];
			assertEquals(categoryName, categoryFilterName);
			
			currentCategory.click();
			categoryActiveFilter.click();
		}
		
		assertThrows(NoSuchElementException.class, 
				() -> {webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div/div/div[2]/div[1]/div/div/p"));});
	}
	
	@Test
	void testSubcategoryFilters() throws InterruptedException {
		openShopPage();
		
		WebElement category = webDriver.findElement(By.className("Category_collapsibleCategory__3wERX"));
		String categoryName = category.getText().split("\\s+")[0];
		category.click();
		
		List<WebElement> subcategories = webDriver.findElements(By.className("Subcategory_subcategoryItem__1tROv"));
		
		for (int i = 0; i < subcategories.size(); i++) {
			WebElement currentSubcategory = subcategories.get(i);
			String subcategoryName = currentSubcategory.getText().split("\\s+")[0];
			String noOfItemsInSubcategoryString = currentSubcategory.getText().split("\\s+")[1];
			int noOfItemsInSubcategory = Integer.parseInt(noOfItemsInSubcategoryString.replaceAll("[^0-9]", ""));
			WebElement currentSubcategoryCheckbox = currentSubcategory.findElement(By.tagName("input"));
			currentSubcategoryCheckbox.click();
			
			WebElement categoryActiveFilter = webDriver.findElement(By.xpath("/html/body/div/div/div[3]/div/div/div[2]/div[1]/div/div/button"));
			String categoryFilterName = categoryActiveFilter.getText().split(" ")[0];
			assertEquals(categoryName + "/" + subcategoryName, categoryFilterName);
			
			if (noOfItemsInSubcategory > 0) {
				Thread.sleep(2000);
				List<WebElement> itemsInSubcategory = webDriver.findElements(By.className("Item_itemContainer__uYG6J"));
				assertEquals(noOfItemsInSubcategory, itemsInSubcategory.size());
			}
			
			categoryActiveFilter.click();
			assertFalse(currentSubcategoryCheckbox.isSelected());
		}
		
		assertThrows(NoSuchElementException.class, 
				() -> {webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div/div/div[2]/div[1]/div/div/p"));});
	}
	
	void openShopPage() throws InterruptedException {
		WebElement shopLink = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/div[3]/a[2]"));
		shopLink.click();
		Thread.sleep(2000);
	}

}
