package com.smas.processor;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
		 
		(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				long endTime = System.nanoTime();
				long duration = (endTime - startTime);
				double seconds = (double) duration / 1000000000.0;

				System.out.print("----loading: " + Math.round(seconds) + "s...\n");
				return d.findElement(By.id("UserName")) != null;
			}
		});
		System.out.println("----url: " + driver.getCurrentUrl());
		try { 
			WebElement formLoginEl = driver.findElement(By.tagName("form")); 
			WebElement userNameEl = driver.findElement(By.id("UserName")); 
			WebElement passwordEl = driver.findElement(By.id("Password"));
			  
			// sendkey to form
			userNameEl.sendKeys(loginDto.getUsername());
			passwordEl.sendKeys(loginDto.getPassword());
			 
			formLoginEl.submit();
			
			PageUtils.waitForLoad(driver);
			
			String currentUrl = driver.getCurrentUrl();
			System.out.println("----url: " + currentUrl);
			
			if(StringUtils.isNotEmpty(currentUrl) && StringUtils.contains(currentUrl, "AuthorizeAdmin")) { 
				System.out.println("----Vào quản lý Cấp 1"); 

				// click cap 1
				WebElement formEl = driver.findElement(By.tagName("form"));
				JavascriptExecutor jse = (JavascriptExecutor)driver;
				jse.executeScript("document.getElementById('RdbSelectLevel').setAttribute('value', '1');");
				
				formEl.submit(); 
				
				long endTime = System.nanoTime();
				long duration = (endTime - startTime);
				double seconds = (double) duration / 1000000000.0;
				System.out.println("----thời gian: " + Math.round(seconds) + "s");
				return true;
			} 
		} catch (Exception e) {
			//
		}
		return false;
	}
}
