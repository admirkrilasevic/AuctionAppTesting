package test;

import static org.junit.jupiter.api.Assertions.*;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
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
	
	@Test
	void testPriceFilter() throws InterruptedException {
		openShopPage();
		selectPrices();
        
		WebElement minPriceFilterInput = webDriver.findElement(By.className("PriceMenu_minPriceInput__2GC2p"));
		WebElement maxPriceFilterInput = webDriver.findElement(By.className("PriceMenu_maxPriceInput__2RsAW"));
		double minPrice = Double.parseDouble(minPriceFilterInput.getAttribute("value"));
		double maxPrice = Double.parseDouble(maxPriceFilterInput.getAttribute("value"));
		
		WebElement priceRange = webDriver.findElement(By.className("PriceMenu_priceRange__3eMWN"));
		String expectedPriceRange = "$" + minPrice + "-$" + maxPrice;
		assertEquals(expectedPriceRange, priceRange.getText());
		
		WebElement averagePrice = webDriver.findElement(By.className("PriceMenu_priceAverage__1lTg9"));
		double avgPriceDouble = ((minPrice + maxPrice) / 2);
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		String avgPrice = "The average price is $" + decimalFormat.format(avgPriceDouble);
		assertEquals(avgPrice, averagePrice.getText());
		
		WebElement priceActiveFilter = webDriver.findElement(By.xpath("/html/body/div/div/div[3]/div/div/div[2]/div[1]/div/div/button"));
		String priceFilterValue = priceActiveFilter.getText().split(" ")[0];
		assertEquals(expectedPriceRange, priceFilterValue);
		
		Select sortOptions = new Select(webDriver.findElement(By.className("ShopPageItems_sortDropdown__2zRR6")));
		sortOptions.selectByVisibleText("Price: Low to High");
		
		Thread.sleep(2000);
		List<WebElement> listedItems = webDriver.findElements(By.className("Item_itemContainer__uYG6J"));
		listedItems.get(0).click();
		Thread.sleep(2000);
		WebElement startingPrice = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/div/div/div[2]/p/span"));
		assertTrue(Double.parseDouble(startingPrice.getText().substring(1)) >= minPrice);
		
		webDriver.navigate().back();
		Thread.sleep(2000);
		
		selectPrices();
		maxPriceFilterInput = webDriver.findElement(By.className("PriceMenu_maxPriceInput__2RsAW"));
		maxPrice = Double.parseDouble(maxPriceFilterInput.getAttribute("value"));
		
		sortOptions = new Select(webDriver.findElement(By.className("ShopPageItems_sortDropdown__2zRR6")));
		sortOptions.selectByVisibleText("Price: Low to High");
		Thread.sleep(2000);
		
		listedItems = webDriver.findElements(By.className("Item_itemContainer__uYG6J"));
		listedItems.get(listedItems.size()-1).click();
		Thread.sleep(2000);
		startingPrice = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/div/div/div[2]/p/span"));
		assertTrue(Double.parseDouble(startingPrice.getText().substring(1)) <= maxPrice);
	}
	
	@Test
	void testMultipleFiltersAndClearAll() throws InterruptedException {
		openShopPage();
		
		List<WebElement> categories = webDriver.findElements(By.className("Category_collapsibleCategory__3wERX"));
		String categoryName1 = categories.get(0).getText().split("\\s+")[0];
		String categoryName2 = categories.get(1).getText().split("\\s+")[0];
		categories.get(0).click();
		categories.get(1).click();
		
		WebElement subcategory = webDriver.findElement(By.className("Subcategory_subcategoryItem__1tROv"));
		String subcategoryName = subcategory.getText().split("\\s+")[0];
		WebElement subcategoryCheckbox = subcategory.findElement(By.tagName("input"));
		subcategoryCheckbox.click();
		
		JavascriptExecutor js = (JavascriptExecutor) webDriver;
		js.executeScript("window.scrollBy(0,300)", "");
		Thread.sleep(2000);
		
		selectPrices();
		WebElement minPriceFilterInput = webDriver.findElement(By.className("PriceMenu_minPriceInput__2GC2p"));
		WebElement maxPriceFilterInput = webDriver.findElement(By.className("PriceMenu_maxPriceInput__2RsAW"));
		double minPrice = Double.parseDouble(minPriceFilterInput.getAttribute("value"));
		double maxPrice = Double.parseDouble(maxPriceFilterInput.getAttribute("value"));
		
		js.executeScript("window.scrollBy(0,-300)", "");
		Thread.sleep(2000);
		
		List<WebElement> allActiveFilters = webDriver.findElements(By.className("ActiveFilters_filter__xHUuV"));
		String[] activeFilterNames = new String[3];
		for (int i = 0; i < allActiveFilters.size(); i++) {
			activeFilterNames[i] = allActiveFilters.get(i).getText().split(" ")[0];
		}
		
		String expectedCategoryFilterName = categoryName2;
		String expectedSubcategoryFilterName = categoryName1 + "/" + subcategoryName;
		String expectedPriceFilterName = "$" + minPrice + "-$" + maxPrice;
		
		assertEquals(expectedCategoryFilterName, activeFilterNames[0]);
		assertEquals(expectedSubcategoryFilterName, activeFilterNames[1]);
		assertEquals(expectedPriceFilterName, activeFilterNames[2]);
		
		WebElement clearAllButton = webDriver.findElement(By.className("ActiveFilters_clearAllButton__3lJdT"));
		clearAllButton.click();
		
		assertThrows(NoSuchElementException.class, 
				() -> {webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div/div/div[2]/div[1]/div/div/p"));});
	}
	
	void selectPrices() {
		WebElement minPriceSlider = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div/div/div[1]/div[2]/span/span[3]"));
		Actions moveMinSlider = new Actions(webDriver);
        moveMinSlider
	        .moveToElement(minPriceSlider)
	        .clickAndHold(minPriceSlider)
	        .moveByOffset(10, 0)
	        .release().perform();
        
		WebElement maxPriceSlider = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div/div/div[1]/div[2]/span/span[4]"));
		Actions moveMaxSlider = new Actions(webDriver);
        moveMaxSlider
	        .moveToElement(maxPriceSlider)
	        .clickAndHold(maxPriceSlider)
	        .moveByOffset(-150, 0)
	        .release().perform();
	}
	
	void openShopPage() throws InterruptedException {
		WebElement shopLink = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/div[3]/a[2]"));
		shopLink.click();
		Thread.sleep(2000);
	}

}
