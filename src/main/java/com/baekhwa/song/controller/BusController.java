package com.baekhwa.song.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.baekhwa.song.domain.dto.bus.BusInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class BusController {
	
	@GetMapping("/common/mybus")
	public String busInfo(Model model) throws IOException {
		String busNo="1151";
		String apiURL ="http://ws.bus.go.kr/api/rest/busRouteInfo/getBusRouteList?serviceKey=DCRpenShcj%2F5JNLCWK1r1a8ZUkRKLJdadpV2QEElBFmnfUxkM22YRgA3h1Kgae3EKhDd%2FC3qzOPcBng1mXqrvg%3D%3D&resultType=json&strSrch="+busNo;
		
		String response=get(apiURL);
		//System.out.println(response);
		
		ObjectMapper mapper = new ObjectMapper();
		BusInfo busInfo = mapper.readValue(response, BusInfo.class);
		System.out.println(">>>>>>>>>>>>>>>>>>>>");
		System.out.println(busInfo.getMsgBody().getItemList());
		System.out.println(">>>>>>>>>>>>>>>>>>>>");
		model.addAttribute("list", busInfo.getMsgBody().getItemList());
		
		return "bus/info";
	}
	
	private  String get(String apiUrl){
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            //con.setRequestProperty("Content-type", "application/json");
            //con.setRequestProperty("Accept", "application/json");
            //con.setDoOutput(true);

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private  HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private  String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();
            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }
}
