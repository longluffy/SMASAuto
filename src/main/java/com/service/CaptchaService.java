package com.service;

import java.io.FileInputStream;
import java.io.IOException;

import com.DeathByCaptcha.Captcha;
import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.Exception;
import com.DeathByCaptcha.HttpClient;

public class CaptchaService {

	private static final String USER_NAME_VAL = "longluffy";
	private static final String PASSWORD_VAL = "Chopper2791";

	public static Captcha requestCheckCaptcha(String fileLocal) {
		Client client = (Client) (new HttpClient(USER_NAME_VAL, PASSWORD_VAL));
		client.isVerbose = true;

		try {
			try {
				System.out.println("----Your balance: " + client.getBalance() + " US cents");
			} catch (IOException e) {
				System.out.println("----Failed fetching balance: " + e.toString());
				return null;
			}

			Captcha captcha = null;

			try {
				FileInputStream imagefile = new FileInputStream(fileLocal);
				captcha = client.decode(imagefile, 120);
				if (captcha != null) {
					System.out.println("-----------------------------------");
					System.out.println("----correct: " + captcha.isCorrect());
					System.out.println("----solved: " + captcha.isSolved());
					System.out.println("----captcha: " + captcha.text);
					System.out.println("-----------------------------------");
				} else {
					System.out.println("----captcha: null");
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("----Failed uploading CAPTCHA");
				return captcha;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (null != captcha) {
				System.out.println("----CAPTCHA " + captcha.id + " solved: " + captcha.text);
				return captcha;

			} else {
				System.out.println("----Failed solving CAPTCHA");
			}
		} catch (com.DeathByCaptcha.Exception e) {
			System.out.println("----" + e.getMessage());
		}
		return null;
	}

	public static void reportIncorectCaptcha(Captcha captcha) {

		Client client = (Client) (new HttpClient(USER_NAME_VAL, PASSWORD_VAL));
		client.isVerbose = true;

		try {
			if (client.report(captcha)) {
				System.out.println("Reported as incorrectly solved");
			} else {
				System.out.println("Failed reporting incorrectly solved CAPTCHA");
			}
		} catch (IOException e) {
			System.out.println("Failed reporting incorrectly solved CAPTCHA: " + e.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
