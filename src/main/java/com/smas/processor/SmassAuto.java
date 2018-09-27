package com.smas.processor;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.smas.dto.SLoginDTO;
import com.smas.dto.STheNapDTO;
import com.webelement.PageUtils;

public class SmassAuto {
	private SLoginDTO loginDto;
	private List<STheNapDTO> thenapListDto;
	private String pathExe;

	public SmassAuto(String pathExe, SLoginDTO loginDto, List<STheNapDTO> thenapListDto) {
		this.loginDto = loginDto;
		this.pathExe = pathExe;
		this.thenapListDto = thenapListDto;
	}
	 
	public String execute() {
		WebDriver driver = null;
		PageUtils.offlogging();
		try {

			// options
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setJavascriptEnabled(true);

			capabilities.setCapability("phantomjs.page.settings.userAgent",
					"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
			capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, pathExe);

			System.out.println("----STARTING-----");

			driver = new PhantomJSDriver(capabilities);

			PageUtils.offlogging();

			// check loggin
			System.out.println(">> GOTO TOPUP <<");
			driver.get("https://smas.edu.vn/SMSEDUArea/Topup");
			String currentUrl = driver.getCurrentUrl();
			System.out.println("currentUrl: " + currentUrl + " : " + StringUtils.contains(currentUrl, "LogOn"));

			boolean isLogged = true;
			boolean hasVisitedLogin = false;
			if (StringUtils.isNotEmpty(currentUrl) && StringUtils.contains(currentUrl, "LogOn")) {
				hasVisitedLogin = true;
				// execute login
				SmasLoginProcessor loginProcessor = new SmasLoginProcessor(driver, loginDto);
				isLogged = loginProcessor.execute();
				if (!isLogged) {
					System.out.println("LOGIN FAILED");
					return "ERROR: LOGIN FAILED";
				}

			}

			if (hasVisitedLogin) {
				// goto the nap
				driver.get("https://smas.edu.vn/SMSEDUArea/Topup");
			}

			// for the nap
			for (STheNapDTO dto : thenapListDto) {
				SmasNapTheProcessor naptheProcessor = new SmasNapTheProcessor(driver, dto, hasVisitedLogin);
				String message = naptheProcessor.execute();
				System.out.println(message);
			}

			long startTime = System.nanoTime();
			long endTime = System.nanoTime();
			long duration = (endTime - startTime);
			double seconds = (double) duration / 1000000000.0;

			System.out.println("TIME SPENT: " + seconds + "s");
			driver.quit();
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("INTERNAL SERVER ERROR");
			if (driver != null) {
				driver.quit();
			}
			return "SERVER ERROR: " + e.getMessage();
		}
	}

	public SLoginDTO getLoginDto() {
		return loginDto;
	}

	public void setLoginDto(SLoginDTO loginDto) {
		this.loginDto = loginDto;
	}

	public String getPathExe() {
		return pathExe;
	}

	public void setPathExe(String pathExe) {
		this.pathExe = pathExe;
	}

	public List<STheNapDTO> getThenapListDto() {
		return thenapListDto;
	}

	public void setThenapListDto(List<STheNapDTO> thenapListDto) {
		this.thenapListDto = thenapListDto;
	}

}
