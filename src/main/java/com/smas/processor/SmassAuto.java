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

	public SmassAuto(SLoginDTO loginDto, List<STheNapDTO> thenapListDto) {
		this.loginDto = loginDto;
		this.thenapListDto = thenapListDto;
	}

	public String execute() {
		WebDriver driver = null;
		PageUtils.offlogging();
		try {

			System.out.println("----BẮT ĐẦU-----");

			driver = PageUtils.initInstant();

			PageUtils.offlogging();

			// check loggin
			driver.get("https://smas.edu.vn/SMSEDUArea/Topup");
			String currentUrl = driver.getCurrentUrl();

			boolean isLogged = true;
			boolean hasVisitedLogin = false;
			if (StringUtils.isNotEmpty(currentUrl) && StringUtils.contains(currentUrl, "LogOn")) {
				System.out.println("1. ĐĂNG NHẬP");
				hasVisitedLogin = true;
				// execute login

				System.out.println("--TIẾN HÀNH ĐĂNG NHẬP");
				SmasLoginProcessor loginProcessor = new SmasLoginProcessor(driver, loginDto);
				isLogged = loginProcessor.execute();
				if (!isLogged) {
					System.out.println("--LỖI ĐĂNG NHẬP");
					return "ERROR: LOGIN FAILED";
				}
				System.out.println("--ĐĂNG NHẬP THÀNH CÔNG");

			}

			if (hasVisitedLogin) {
				// goto the nap
				driver.get("https://smas.edu.vn/SMSEDUArea/Topup");
			}

			// for the nap
			System.out.println("2. NẠP THẺ");
			for (STheNapDTO dto : thenapListDto) {
				System.out.println("--THẺ: " + dto.getMathe());
				SmasNapTheProcessor naptheProcessor = new SmasNapTheProcessor(driver, dto, hasVisitedLogin);
				String message = naptheProcessor.execute();
				System.out.println(message);
			}

			long startTime = System.nanoTime();
			long endTime = System.nanoTime();
			long duration = (endTime - startTime);
			double seconds = (double) duration / 1000000000.0;

			System.out.println("TỔNG: " + Math.round(seconds) + "s");
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

	public List<STheNapDTO> getThenapListDto() {
		return thenapListDto;
	}

	public void setThenapListDto(List<STheNapDTO> thenapListDto) {
		this.thenapListDto = thenapListDto;
	}

}
