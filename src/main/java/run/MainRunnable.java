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
		smasEduSite(); 
	}

	private static void smasEduSite() {
		SLoginDTO loginDto = new SLoginDTO("hni_btlm_thcs_xuandinh", "123456aA@");
		List<STheNapDTO> theNapListDto = new ArrayList<>(); 
		theNapListDto.add(new STheNapDTO("911433261313564","10000146004422"));
		SmassAuto smasAuto = new SmassAuto(loginDto, theNapListDto);
		smasAuto.execute();
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
