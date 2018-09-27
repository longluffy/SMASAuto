package com.smas.processor;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.smas.dto.SLoginDTO;
import com.webelement.PageUtils; 

public class SmasLoginProcessor {

	private WebDriver driver;
	private SLoginDTO loginDto;

	public SmasLoginProcessor(WebDriver driver, SLoginDTO loginDto) {
		this.driver = driver;
		this.loginDto = loginDto;
	}

	public boolean execute() {
		
		PageUtils.offlogging();
		driver.manage().deleteAllCookies();
		driver.get("https://smas.edu.vn/Home/LogOn?ReturnUrl=%2f");
		
		long startTime = System.nanoTime();
		System.out.println(">> GOTO LOGIN << : ");
		
		(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				long endTime = System.nanoTime();
				long duration = (endTime - startTime);
				double seconds = (double) duration / 1000000000.0;

				System.out.println("TIME SPENT: " + seconds + "s");
				return d.findElement(By.id("UserName")) != null;
			}
		});
 
		try { 
			WebElement formLoginEl = driver.findElement(By.tagName("form"));
			WebElement userNameEl = driver.findElement(By.id("UserName"));
			WebElement requestVerificationTokenEl = driver.findElement(By.id("UserName"));
			WebElement passwordEl = driver.findElement(By.id("Password"));
			
			String csrfString = requestVerificationTokenEl.getAttribute("value");
			System.out.println("csrfString: " + csrfString);
			// sendkey to form
			userNameEl.sendKeys(loginDto.getUsername());
			passwordEl.sendKeys(loginDto.getPassword());
			
			System.out.println("param typed");
			formLoginEl.submit();
			
			PageUtils.waitForLoad(driver);
			
			String currentUrl = driver.getCurrentUrl();
			if(StringUtils.isNotEmpty(currentUrl) && StringUtils.contains(currentUrl, "AuthorizeAdmin")) {
				return true;
			} 
		} catch (Exception e) {
			//
		}
		return false;
	}
}
