package test;

import java.time.Duration;

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
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import main.Setup;

import static org.junit.jupiter.api.Assertions.*;


class LoginAndRegistrationTest {

	private static WebDriver webDriver;
	private static WebDriverWait wait;
	private static Setup setup;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		//Before running ensure that the Chrome driver is placed in root folder
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
	void testLoginLinkOnRegisterPage() throws InterruptedException {
		WebElement register = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[1]/nav/p/a[2]"));
		register.click();
		Thread.sleep(2000);
		
		JavascriptExecutor js = (JavascriptExecutor) webDriver;
		js.executeScript("window.scrollBy(0,500)", "");
		Thread.sleep(2000);
		
		WebElement loginLink = webDriver.findElement(By.className("Forms_linkToLogin__VHCBU"));
		loginLink.click();
		Thread.sleep(2000);
		
		assertEquals("https://auctionapp.krilasevic.me/login", webDriver.getCurrentUrl());
	}

	@Test
	void testRegisterSuccessful() throws InterruptedException {
		WebElement register = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[1]/nav/p/a[2]"));
		register.click();
		Thread.sleep(2000);

		WebElement name = webDriver.findElement(By.name("name"));
		WebElement surname = webDriver.findElement(By.name("surname"));
		WebElement email = webDriver.findElement(By.name("email"));
		WebElement password = webDriver.findElement(By.name("password"));
		name.sendKeys("newName");
		surname.sendKeys("newSurname");
		email.sendKeys("newName.newSurname.test@gmail.com");
		password.sendKeys("123456789");
		Thread.sleep(2000);
		
		JavascriptExecutor js = (JavascriptExecutor) webDriver;
		js.executeScript("window.scrollBy(0,500)", "");
		Thread.sleep(2000);

		WebElement registerButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/form/button")));
		registerButton.click();
		Thread.sleep(2000);
		
		WebElement successAlert = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/form/div[6]/div"));
		assertEquals("User registered successfully!", successAlert.getText());
	}

	@Test
	void testRegisterDisabled() throws InterruptedException {
		WebElement register = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[1]/nav/p/a[2]"));
		register.click();
		Thread.sleep(2000);

		WebElement name = webDriver.findElement(By.name("name"));
		WebElement surname = webDriver.findElement(By.name("surname"));
		WebElement email = webDriver.findElement(By.name("email"));
		WebElement password = webDriver.findElement(By.name("password"));

		name.sendKeys("matej");
		surname.sendKeys("matej");
		email.sendKeys("matej");
		password.sendKeys("matej");
		
		Actions action = new Actions(webDriver);
		action.moveToElement(email).click().perform();
		
		WebElement emailAlert = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/form/div[4]/div/div"));
		WebElement passwordAlert = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/form/div[5]/div/div"));
		assertEquals("This is not a valid email.", emailAlert.getText());
		assertEquals("The password must be between 8 and 20 characters.", passwordAlert.getText());

		WebElement registerButton = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/form/button"));
		assertFalse(registerButton.isEnabled());
	}

	@Test
	void testLoginSuccessful() throws InterruptedException {
		WebElement login = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[1]/nav/p/a[1]"));
		login.click();
		Thread.sleep(2000);

		WebElement email = webDriver.findElement(By.name("email"));
		WebElement password = webDriver.findElement(By.name("password"));
		email.sendKeys("matej.mujezinovic@gmail.com");
		password.sendKeys("123456789");
		Thread.sleep(2000);

		WebElement loginButton = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/form/button"));
		loginButton.click();
		Thread.sleep(2000);

		WebElement logout = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[1]/nav/p/a"));
		assertEquals("Log Out", logout.getText());
		logout.click();
		Thread.sleep(2000);
	}

	@Test
	void testLoginDisabled() throws InterruptedException { 
		WebElement login = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[1]/nav/p/a[1]"));
		login.click();
		Thread.sleep(2000);

		WebElement email = webDriver.findElement(By.name("email"));
		WebElement password = webDriver.findElement(By.name("password"));
		email.sendKeys("bademail");
		password.sendKeys("wrongpassword");
		WebElement invalidEmailField = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/form/div[2]/div/div"));
		assertEquals("This is not a valid email.", invalidEmailField.getText());
		
		WebElement loginButton = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/form/button"));
		assertFalse(loginButton.isEnabled());
	}
	
	@Test
	void testLoginUnauthorized() throws InterruptedException {
		WebElement login = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[1]/nav/p/a[1]"));
		login.click();
		Thread.sleep(2000);

		WebElement email = webDriver.findElement(By.name("email"));
		WebElement password = webDriver.findElement(By.name("password"));
		email.sendKeys("matej@gmail.com");
		password.sendKeys("wrongpassword");
		
		WebElement loginButton = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/form/button"));
		loginButton.click();
		Thread.sleep(2000);
		
		WebElement unauthorizedAlert = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/form/div[4]/div"));
		assertEquals("Request failed with status code 401", unauthorizedAlert.getText());
	}

	@Test
	void testLogout() throws InterruptedException {
		WebElement login = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[1]/nav/p/a[1]"));
		assertEquals("Login", login.getText());
		login.click();
		Thread.sleep(2000);
		
		WebElement email = webDriver.findElement(By.name("email"));
		WebElement password = webDriver.findElement(By.name("password"));
		email.sendKeys("matej.mujezinovic@gmail.com");
		password.sendKeys("123456789");
		
		WebElement loginButton = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/form/button"));
		loginButton.click();
		Thread.sleep(2000);
		
		WebElement logout = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[1]/nav/p/a"));
		assertEquals("Log Out", logout.getText());
		logout.click();
		Thread.sleep(2000);
		
		WebElement newLogin = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[1]/nav/p/a[1]"));
		assertEquals("Login", newLogin.getText());
		Thread.sleep(2000);

		newLogin.click();
		assertEquals("https://auctionapp.krilasevic.me/login", webDriver.getCurrentUrl());
	}
}
