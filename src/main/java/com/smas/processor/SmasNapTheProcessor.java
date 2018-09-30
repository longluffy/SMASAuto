package com.smas.processor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
		long startTime = System.nanoTime();

		try {
			int countRetry = 0;
			while (true) {
				countRetry = countRetry + 1;
				System.out.println("----retry lần: " + countRetry);
				if (countRetry > 5) {
					System.out.println("----QUIT: retry quá 5 lần");
					break;
				}

				driver.get("https://smas.edu.vn/SMSEDUArea/Topup");
				System.out.println("----url: " + driver.getCurrentUrl());

				(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
					public Boolean apply(WebDriver d) {
						long endTime = System.nanoTime();
						long duration = (endTime - startTime);
						double seconds = (double) duration / 1000000000.0;

						System.out.print("----loading: " + Math.round(seconds) + "s...\n");
						return d.findElement(By.id("frmTopup")) != null && d.findElement(By.id("captcha")) != null;
					}
				});

				WebElement contentEl = driver.findElement(By.id("content"));
				((JavascriptExecutor) driver).executeScript("arguments[0].style.display = 'block';", contentEl);

				// get captcha
				String fileImage = captureCaptcha(sTheNapDTO.getMathe(), driver);
				String captchaText = getCaptchaText(fileImage);

				if (StringUtils.isEmpty(captchaText)) {
					System.out.println("----captcha empty");
					continue;
				} else {
					System.out.println("----captchaText: " + captchaText);
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

				// capture screen
				contentEl = driver.findElement(By.id("content"));
				((JavascriptExecutor) driver).executeScript("arguments[0].style.display = 'block';", contentEl);
				File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				String fileDoneImage = getPathFileResult(sTheNapDTO.getMathe());
				FileUtils.copyFile(screenshot, new File(fileDoneImage));

				long endTime = System.nanoTime();
				long duration = (endTime - startTime);
				double seconds = (double) duration / 1000000000.0;
				System.out.println("----thời gian: " + Math.round(seconds) + "s");

				WebElement errorEl = driver.findElement(By.className("message-of-error"));
				if (errorEl != null && StringUtils.isNotEmpty(errorEl.getText())) {
					return "RESULT: " + errorEl.getText();
				} 
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("TIME OUT");
		}
		return "RESULT: SUCCESS";
	}

	private String captureCaptcha(String mathe, WebDriver driver) throws IOException {
		// get image captcha
		WebElement imgCaptchaEl = driver.findElement(By.id("captcha"));

		String linkImageUrlCaptcha = imgCaptchaEl.getAttribute("src");
		System.out.println("------captcha: " + linkImageUrlCaptcha);

		if (StringUtils.isNotEmpty(linkImageUrlCaptcha)) {

			Point point = imgCaptchaEl.getLocation();

			String naturalWidth = imgCaptchaEl.getAttribute("naturalWidth");
			String naturalHeight = imgCaptchaEl.getAttribute("naturalHeight");
			int eleWidth = Integer.parseInt(naturalWidth);
			int eleHeight = Integer.parseInt(naturalHeight);

			// Get entire page screenshot
			File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			BufferedImage fullImg = ImageIO.read(screenshot);

			// // Crop the entire page screenshot to get only element screenshot
			BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);
			ImageIO.write(eleScreenshot, "png", screenshot);

			// Copy the element screenshot to disk
			String fileImage = getPathFileCaptcha(mathe);
			File screenshotLocation = new File(fileImage);
			FileUtils.copyFile(screenshot, screenshotLocation);

			return fileImage;
		}
		return "";
	}

	private String getCaptchaText(String fileLocation) {
		Captcha captcha = null;
		int retrycaptcha = 0;
		while (null == captcha || captcha.text.isEmpty()) {
			retrycaptcha += 1;
			System.out.println("------retry captcha: " + retrycaptcha);
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
		return captchaText;
	}

	private String getPathFileCaptcha(String maThe) {
		String fileName = maThe + "-captcha.png";
		String folder = System.getProperty("user.dir") + "\\capture\\";
		String destFile = folder + fileName;
		return destFile;
	}

	private String getPathFileResult(String maThe) {
		String fileName = maThe + "-result.png";
		String folder = System.getProperty("user.dir") + "\\capture\\";
		String destFile = folder + fileName;
		return destFile;
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
