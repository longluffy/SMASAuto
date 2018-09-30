package com.webelement;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.ProtocolHandshake;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PageUtils {

	private final static String PATH_TO_EXE_SELENIUM = "C:\\Tool\\phantomjs\\phantomjs.exe";
	
	public static void offlogging() {
		Logger.getLogger(PhantomJSDriverService.class.getName()).setLevel(Level.OFF);
		Logger.getLogger(ProtocolHandshake.class.getName()).setLevel(Level.OFF);
	}

	public static WebElement getLogoutLinkElement(WebDriver driver) {
		try {
			return driver.findElement(By.xpath("//a[@href='/dang-xuat']"));
		} catch (Exception e) {
			return null;
		}
	}

	public static WebElement getLoginLinkElement(WebDriver driver) {
		try {
			return driver.findElement(By.xpath("//a[@href='/dang-nhap']"));
		} catch (Exception e) {
			return null;
		}
	}

	public static void waitForLoad(WebDriver driver) {
		ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
			}
		};
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(pageLoadCondition);
	}

	public static WebDriver initInstant() {
		// options
		DesiredCapabilities capabilities = new DesiredCapabilities();

		// off log
		String[] phantomArgs = new String[] { "--webdriver-loglevel=NONE" };
		capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomArgs);

		capabilities.setJavascriptEnabled(true);

		capabilities.setCapability("phantomjs.page.settings.userAgent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
		capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, PATH_TO_EXE_SELENIUM);
 
		return new PhantomJSDriver(capabilities);
	}
}
