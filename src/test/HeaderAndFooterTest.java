package test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import main.Setup;

class HeaderAndFooterTest {
	
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
		webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
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
	void testHeaderLinks() throws InterruptedException {
		WebElement loginLink = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[1]/nav/p/a[1]"));
		loginLink.click();
		assertEquals("https://auctionapp.krilasevic.me/login", webDriver.getCurrentUrl());
		
		WebElement registerLink = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[1]/nav/p/a[2]"));
		registerLink.click();
		assertEquals("https://auctionapp.krilasevic.me/register", webDriver.getCurrentUrl());
	}
	
	@Test
	void testNavbarLinks() throws InterruptedException {
		WebElement logoLink = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/div[1]/a/img"));
		logoLink.click();
		assertEquals("https://auctionapp.krilasevic.me/home", webDriver.getCurrentUrl());
		
		WebElement homeLink = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/div[3]/a[1]"));
		homeLink.click();
		assertEquals("https://auctionapp.krilasevic.me/home", webDriver.getCurrentUrl());
		
		WebElement shopLink = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/div[3]/a[2]"));
		shopLink.click();
		assertEquals("https://auctionapp.krilasevic.me/shop/0", webDriver.getCurrentUrl());
		
		WebElement accountLink = webDriver.findElement(By.xpath("/html/body/div/div/div[2]/div/div[3]/div/button/a"));
		accountLink.click();
		assertEquals("https://auctionapp.krilasevic.me/register", webDriver.getCurrentUrl());
		
		Actions action = new Actions(webDriver);
		action.moveToElement(accountLink).perform();
		Thread.sleep(1000);
		
		WebElement accountProfileLink = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/div[3]/div/div/a[1]/a"));
		accountProfileLink.click();
		assertEquals("https://auctionapp.krilasevic.me/register", webDriver.getCurrentUrl());
		
		action.moveToElement(accountLink).perform();
		Thread.sleep(1000);
		
		WebElement accountSellerLink = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/div[3]/div/div/a[2]/a"));
		accountSellerLink.click();
		assertEquals("https://auctionapp.krilasevic.me/register", webDriver.getCurrentUrl());
		
		action.moveToElement(accountLink).perform();
		Thread.sleep(1000);
		
		WebElement accountBidsLink = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/div[3]/div/div/a[3]/a"));
		accountBidsLink.click();
		assertEquals("https://auctionapp.krilasevic.me/register", webDriver.getCurrentUrl());
		
		action.moveToElement(accountLink).perform();
		Thread.sleep(1000);
		
		WebElement accountSettingsLink = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/div[3]/div/div/a[4]/a"));
		accountSettingsLink.click();
		assertEquals("https://auctionapp.krilasevic.me/register", webDriver.getCurrentUrl());
	}
	
	@Test
	void testFooterLinks() throws InterruptedException {
		WebElement aboutUsLink = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[4]/div/div/div[1]/div/a[1]"));
		aboutUsLink.click();
		assertEquals("https://auctionapp.krilasevic.me/about", webDriver.getCurrentUrl());
		
		WebElement termsAndConditionsLink = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[4]/div/div/div[1]/div/a[2]"));
		termsAndConditionsLink.click();
		assertEquals("https://auctionapp.krilasevic.me/terms", webDriver.getCurrentUrl());
		
		WebElement privacyAndPolicyLink = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[4]/div/div/div[1]/div/a[3]"));
		privacyAndPolicyLink.click();
		assertEquals("https://auctionapp.krilasevic.me/privacy", webDriver.getCurrentUrl());
	}
	
}
