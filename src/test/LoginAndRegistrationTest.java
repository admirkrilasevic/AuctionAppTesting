package test;

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
import org.openqa.selenium.support.ui.WebDriverWait;

import main.Setup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


class LoginAndRegistrationTest {

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
	void test() throws InterruptedException {
		
	}
	@Test
	void testRegisterPositive() throws InterruptedException {
		WebElement register = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[1]/nav/p/a[2]"));
		register.click();
		Thread.sleep(2000);

		String urlToCheck = "https://auctionapp.krilasevic.me/register";
		assertEquals("https://auctionapp.krilasevic.me/register", urlToCheck);

		WebElement name = webDriver.findElement(By.name("name"));
		WebElement surname = webDriver.findElement(By.name("surname"));
		WebElement email = webDriver.findElement(By.name("email"));
		WebElement password = webDriver.findElement(By.name("password"));
		name.sendKeys("newName");
		surname.sendKeys("newSurname");
		email.sendKeys("newName.newSurname@gmail.com");
		password.sendKeys("123456789");
		Thread.sleep(2000);

		WebElement registerButton = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/form/button"));
		registerButton.click();
		Thread.sleep(2000);
		WebElement successAlert = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/form/div[6]/div"));
		assertEquals("User registered successfully!", successAlert.getText());
	}

	@Test
	void testRegisterNegative() throws InterruptedException{
		WebElement register = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[1]/nav/p/a[2]"));
		register.click();
		Thread.sleep(2000);

		String urlToCheck = "https://auctionapp.krilasevic.me/register";
		assertEquals("https://auctionapp.krilasevic.me/register", urlToCheck);

		WebElement name = webDriver.findElement(By.name("name"));
		WebElement surname = webDriver.findElement(By.name("surname"));
		WebElement email = webDriver.findElement(By.name("email"));
		WebElement password = webDriver.findElement(By.name("password"));

		name.sendKeys("matej!");
		surname.sendKeys("muje%inovic");
		WebElement specialCharacterFieldName = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/form/div[5]/div/div"));
		WebElement specialCharacterFieldSurname = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/form/div[5]/div/div"));
		assertNotEquals("Name cannot contain any special characters", specialCharacterFieldName.getText());
		assertNotEquals("Surname cannot contain any special characters", specialCharacterFieldName.getText());
		Thread.sleep(2000);

		/**
		WebElement registerButton = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/form/button"));
		registerButton.click();
		Thread.sleep(2000);

		WebElement failAlert = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/form/div[6]/div"));
		assertEquals("Request failed with status code 400", failAlert.getText());
		  */
	}

	@Test
	void testLoginPositive() throws InterruptedException{
		WebElement login = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[1]/nav/p/a[1]"));
		login.click();
		Thread.sleep(2000);

		String urlToCheck = "https://auctionapp.krilasevic.me/login";
		assertEquals("https://auctionapp.krilasevic.me/login", urlToCheck);

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
	}

	@Test
	void testLoginNegative() throws InterruptedException{
		WebElement login = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[1]/nav/p/a[1]"));
		login.click();
		Thread.sleep(2000);

		assertEquals("https://auctionapp.krilasevic.me/login", webDriver.getCurrentUrl());

		WebElement email = webDriver.findElement(By.name("email"));
		WebElement password = webDriver.findElement(By.name("password"));
		email.sendKeys("bademail");
		password.sendKeys("wrongpassword");
		WebElement invalidEmailField = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[3]/div[2]/form/div[2]/div/div"));
		assertEquals("This is not a valid email.", invalidEmailField.getText());
		
		//extend
	}

	@Test
	void testLoginAfterLogout() throws InterruptedException{
		WebElement login = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[1]/nav/p/a[1]"));
		assertEquals("Login", login.getText());
		Thread.sleep(2000);

		WebElement logout = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[1]/nav/p/a"));
		logout.click();
		Thread.sleep(2000);

		WebElement newLogin = webDriver.findElement(By.xpath("//*[@id=\"root\"]/div/div[1]/nav/p/a[1]"));
		newLogin.click();
		assertEquals("https://auctionapp.krilasevic.me/login", webDriver.getCurrentUrl());
	}
}
