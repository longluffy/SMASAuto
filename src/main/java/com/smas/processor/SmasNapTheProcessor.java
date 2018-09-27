package com.smas.processor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.DeathByCaptcha.Captcha;
import com.service.CaptchaService;
import com.smas.dto.STheNapDTO;
import com.webelement.PageUtils;

public class SmasNapTheProcessor {
	private WebDriver driver;
	private STheNapDTO sTheNapDTO;
	private boolean reload;

	public SmasNapTheProcessor(WebDriver driver, STheNapDTO sTheNapDTO, boolean reload) {
		this.driver = driver;
		this.sTheNapDTO = sTheNapDTO;
		this.reload = reload;
	}

 
	public String execute() throws Exception {
		PageUtils.offlogging();
		if (this.reload) {
			driver.get("https://smas.edu.vn/SMSEDUArea/Topup");
		}
		try {
			int countRetry = 0;
			while (true) {
				countRetry = countRetry + 1;
				System.out.println("RETRY TIMES: " + countRetry);
				if (countRetry > 7) {
					System.out.println("too many retry ... quit after :" + countRetry);
					break;
				}

				long startTime = System.nanoTime();
				System.out.println(">> START NAPTHE<< : ");
				if (countRetry > 1) {
					driver.get("https://smas.edu.vn/SMSEDUArea/Topup");
				}

				(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
					public Boolean apply(WebDriver d) {
						long endTime = System.nanoTime();
						long duration = (endTime - startTime);
						double seconds = (double) duration / 1000000000.0;

						System.out.print(seconds + "s... | \t");
						return d.findElement(By.id("frmTopup")) != null;
					}
				});

				// get captcha
				String fileLocation = captureCaptcha(driver);
				String captchaText = getCaptchaText(fileLocation);

				if (StringUtils.isEmpty(captchaText)) {
					continue;
				}
				// send form
				WebElement formEl = driver.findElement(By.id("frmTopup"));
				WebElement pinCardEl = driver.findElement(By.id("PinCard"));
				WebElement cardSerialEl = driver.findElement(By.id("CardSerial"));
				WebElement confirmationCodeEl = driver.findElement(By.id("ConfirmationCode"));

				pinCardEl.sendKeys(sTheNapDTO.getMathe());
				cardSerialEl.sendKeys(sTheNapDTO.getSerial());
				confirmationCodeEl.sendKeys(captchaText);
				formEl.submit();

				WebElement errorEl = driver.findElement(By.className("message-of-error"));
				return errorEl.getText();
			}
		} catch (Exception e) { 
			throw new Exception("TIME OUT");
		}
		return "";

	}

	private String getCaptchaText(String fileLocation) {
		Captcha captcha = null;
		int retrycaptcha = 0;
		while (null == captcha || captcha.text.isEmpty()) {
			retrycaptcha += 1;
			if (retrycaptcha > 3) {
				break;
			}
			try {
				captcha = CaptchaService.requestCheckCaptcha(fileLocation);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String captchaText = captcha.text;
		System.out.println("got captcha : " + captchaText);
		return captchaText;
	}

	private String captureCaptcha(WebDriver driver) throws IOException {
		// get image captcha
		WebElement imgCaptchaEl = driver.findElement(By.id("captcha"));

		String linkImageUrlCaptcha = imgCaptchaEl.getAttribute("src");
		System.out.println("linkImageUrlCaptcha: " + linkImageUrlCaptcha);

		if (StringUtils.isNotEmpty(linkImageUrlCaptcha)) {

			// Get entire page screenshot
			File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			BufferedImage fullImg = ImageIO.read(screenshot);

			// Get the location of element on the page
			Point point = imgCaptchaEl.getLocation();

			// Get width and height of the element
			int eleWidth = imgCaptchaEl.getSize().getWidth();
			int eleHeight = imgCaptchaEl.getSize().getHeight();

			// Crop the entire page screenshot to get only element screenshot
			BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);
			ImageIO.write(eleScreenshot, "png", screenshot);

			// get file location
			String fileLocation = getPathFileCaptcha(linkImageUrlCaptcha);
			// Copy the element screenshot to disk
			File screenshotLocation = new File(fileLocation);
			FileUtils.copyFile(screenshot, screenshotLocation);

			return fileLocation;
		}
		return "";
	}

	private String getPathFileCaptcha(String linkImageSrc) {
		String fileName = getImageNameBySID(linkImageSrc);
		String folder = System.getProperty("user.dir") + "\\captcha\\";
		String destFile = folder + fileName;
		return destFile;
	}

	public String getImageNameBySID(String linkImageSrc) {
		String[] urlSegment = linkImageSrc.split("/");
		return urlSegment[urlSegment.length - 1];
	}

	public WebDriver getDriver() {
		return driver;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	public STheNapDTO getsTheNapDTO() {
		return sTheNapDTO;
	}

	public void setsTheNapDTO(STheNapDTO sTheNapDTO) {
		this.sTheNapDTO = sTheNapDTO;
	}

	public boolean isReload() {
		return reload;
	}

	public void setReload(boolean reload) {
		this.reload = reload;
	}

}
