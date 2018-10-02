package run;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.smas.dto.IpProxyDTO;
import com.smas.dto.SLoginDTO;
import com.smas.dto.STheNapDTO;
import com.smas.processor.SmassAuto; 

public class MainRunnable {
	

	public static void main(String[] args) {
		
		//Mã: 513977022024379  10000146337516
		//Mã: 115181618587922  10000145923782
//		Mã: 311195210790305  10000145947343
		String res = smasEduSite("hni_btlm_thcs_xuandinh", "123456aA@","311195210790305","10000145947343"); 
		System.out.println("request result = "+res);
	}

	private static String smasEduSite(String username, String password, String pin, String serial) {
		SLoginDTO loginDto = new SLoginDTO(username, password);
		List<STheNapDTO> theNapListDto = new ArrayList<>(); 
		//Mã: 119652619535309  10000145929445
//		Mã: 919085973615832  10000145945895
//		Mã: 210723429493684  10000147004927
		//Mã: 310247341484229  10000145920053
//Mã: 517797941334399  10000146332858
		//Mã: 512121685167242  10000147631199
//		Mã: 513089963137498  10000147409501
		//Mã: 513089963137498  10000147409501
		theNapListDto.add(new STheNapDTO(pin,serial));
		SmassAuto smasAuto = new SmassAuto(loginDto, theNapListDto);
		return smasAuto.execute();
	}
 
	@SuppressWarnings("unused")
	private static IpProxyDTO fakeProxyVN() throws IOException, JSONException {
		String url = "https://api.getproxylist.com/proxy?country[]=VN";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// optional default is GET
		con.setRequestMethod("GET");
		// add request header
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		int responseCode = con.getResponseCode();
		if (responseCode == 200) {
			System.out.println("connection getproxylist: OK");

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			System.out.println(response.toString());

			// Read JSON response and print
			JSONObject myResponse = new JSONObject(response.toString());
			String host = myResponse.getString("ip");
			Integer port = myResponse.getInt("port");
			if (StringUtils.isEmpty(host) || port == null) {
				return null;
			}

			IpProxyDTO ipProxyDto = new IpProxyDTO(host, port.intValue());

			return ipProxyDto;
		} else {
			System.out.println("connection getproxylist: OK");
			return null;
		}
	}

}
