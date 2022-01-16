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
	void testExploreMore() throws InterruptedException {
		openShopPage();
		
		List<WebElement> listedItems = webDriver.findElements(By.className("Item_itemContainer__uYG6J"));
		int noOfItemsBefore = listedItems.size();
		
		JavascriptExecutor js = (JavascriptExecutor) webDriver;
		js.executeScript("window.scrollBy(0,500)", "");
		Thread.sleep(2000);
		
		WebElement exploreMoreButton = webDriver.findElement(By.className("ShopPageItems_exploreMoreButton__1nQKr"));
		exploreMoreButton.click();
		Thread.sleep(2000);
		
		listedItems = webDriver.findElements(By.className("Item_itemContainer__uYG6J"));
		int noOfItemsAfter = listedItems.size();
		
		assertTrue(noOfItemsBefore < noOfItemsAfter);
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
		WebElement startingPriceContainerFirst = listedItems.get(0).findElement(By.className("Item_startPrice__1QWpu"));
		WebElement startingPriceContainerLast = listedItems.get(listedItems.size()-1).findElement(By.className("Item_startPrice__1QWpu"));
		Double startingPriceFirst = Double.parseDouble(startingPriceContainerFirst.getText().substring(12));
		Double startingPriceLast = Double.parseDouble(startingPriceContainerLast.getText().substring(12));
		
		assertTrue(startingPriceFirst >= minPrice);
		assertTrue(startingPriceLast <= maxPrice);
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
	
	@Test
	void testMultipleFiltersAndSorting() throws InterruptedException {
		openShopPage();
		selectMultipleFilters();
		
		String[] names = new String[3];
		List<WebElement> listedItems = webDriver.findElements(By.className("Item_itemContainer__uYG6J"));
		for (int i = 0; i < 3; i++) {
			WebElement item = listedItems.get(i);
			WebElement nameContainer = item.findElement(By.className("Item_title__3so5b"));
			names[i] = nameContainer.getText();
		}
		
		assertTrue(names[0].compareTo(names[1]) < 0);
		assertTrue(names[1].compareTo(names[2]) < 0);
		
		Select sortOptions = new Select(webDriver.findElement(By.className("ShopPageItems_sortDropdown__2zRR6")));
		sortOptions.selectByVisibleText("Price: Low to High");
		Thread.sleep(2000);
		
		Double[] prices = new Double[3];
		listedItems = webDriver.findElements(By.className("Item_itemContainer__uYG6J"));
		for (int i = 0; i < 3; i++) {
			WebElement item = listedItems.get(i);
			WebElement startingPriceContainer = item.findElement(By.className("Item_startPrice__1QWpu"));
			prices[i] = Double.parseDouble(startingPriceContainer.getText().substring(12));
		}
		
		assertTrue(prices[0] < prices[1]);
		assertTrue(prices[1] < prices[2]);
		
		sortOptions.selectByVisibleText("Price: High to Low");
		Thread.sleep(2000);
		
		prices = new Double[3];
		listedItems = webDriver.findElements(By.className("Item_itemContainer__uYG6J"));
		for (int i = 0; i < 3; i++) {
			WebElement item = listedItems.get(i);
			WebElement startingPriceContainer = item.findElement(By.className("Item_startPrice__1QWpu"));
			prices[i] = Double.parseDouble(startingPriceContainer.getText().substring(12));
		}
		
		assertTrue(prices[0] > prices[1]);
		assertTrue(prices[1] > prices[2]);
	}
	
	@Test
	void testMultipleFiltersAndGridListSwitching() throws InterruptedException {
		openShopPage();
		selectMultipleFilters();
		
		List<WebElement> listedItemsGridView = webDriver.findElements(By.className("Item_itemContainer__uYG6J"));
		WebElement firstItemGridView = listedItemsGridView.get(0);
		WebElement secondItemGridView = listedItemsGridView.get(1);
		String firstItemGridViewName = firstItemGridView.findElement(By.className("Item_title__3so5b")).getText();
		String secondItemGridViewName = secondItemGridView.findElement(By.className("Item_title__3so5b")).getText();
		
		WebElement gridViewButton = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div/div/div[2]/div[2]/div/button[1]"));
		WebElement listViewButton = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div/div/div[2]/div[2]/div/button[2]"));
		
		assertEquals("ShopPageItems_gridListButtonActive__26AAN", gridViewButton.getAttribute("class"));
		
		listViewButton.click();
		assertEquals("ShopPageItems_gridListButtonActive__26AAN", listViewButton.getAttribute("class"));
		
		List<WebElement> listedItemsListView = webDriver.findElements(By.className("ListItem_itemContainer__2s8p_"));
		WebElement firstItemListView = listedItemsListView.get(0);
		WebElement secondItemListView = listedItemsListView.get(1);
		String firstItemListViewName = firstItemListView.findElement(By.className("ListItem_title__1l8Nk")).getText();
		String secondItemListViewName = secondItemListView.findElement(By.className("ListItem_title__1l8Nk")).getText();
		
		assertEquals(firstItemGridViewName, firstItemListViewName);
		assertEquals(secondItemGridViewName, secondItemListViewName);
		
		WebElement description = firstItemListView.findElement(By.className("ListItem_description__3ZsnJ"));
		WebElement bidButton = firstItemListView.findElement(By.className("ListItem_bidButton__3b21V"));
		
		assertTrue(description.isDisplayed());
		assertTrue(bidButton.isDisplayed());
	}
	
	void selectMultipleFilters() throws InterruptedException {
		List<WebElement> categories = webDriver.findElements(By.className("Category_collapsibleCategory__3wERX"));
		categories.get(0).click();
		categories.get(1).click();
		
		WebElement subcategory = webDriver.findElement(By.className("Subcategory_subcategoryItem__1tROv"));
		WebElement subcategoryCheckbox = subcategory.findElement(By.tagName("input"));
		subcategoryCheckbox.click();
		
		JavascriptExecutor js = (JavascriptExecutor) webDriver;
		js.executeScript("window.scrollBy(0,300)", "");
		Thread.sleep(2000);
		
		selectPrices();
		js.executeScript("window.scrollBy(0,-300)", "");
		Thread.sleep(2000);
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
