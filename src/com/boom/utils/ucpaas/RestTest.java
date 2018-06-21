/**
 * @author Glan.duanyj
 * @date 2014-05-12
 * @project rest_demo
 */
package com.boom.utils.ucpaas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.boom.utils.ucpaas.rest.client.AbsRestClient;
import com.boom.utils.ucpaas.rest.client.JsonReqClient;
import com.boom.utils.ucpaas.rest.client.XmlReqClient;

public class RestTest {
	private String accountSid;
	private String authToken;
	
	public String getAccountSid() {
		return accountSid;
	}
	public void setAccountSid(String accountSid) {
		this.accountSid = accountSid;
	}
	public String getAuthToken() {
		return authToken;
	}
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
	static AbsRestClient InstantiationRestAPI(boolean enable) {
		if(enable) {
			return new JsonReqClient();
		} else {
			return new XmlReqClient();
		}
	}

	public static void testTemplateSMS(boolean json,String accountSid,String authToken,String appId,String templateId,String to,String param){
		try {
			String result=InstantiationRestAPI(json).templateSMS(accountSid, authToken, appId, templateId, to, param);
			System.out.println("Response content is: " + result);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 测试说明 参数顺序，请参照各具体方法参数名称
	 * 参数名称含义，请参考rest api 文档
	 * @author Glan.duanyj
	 * @date 2014-06-30
	 * @param params[]
	 * @return void
	 * @throws IOException 
	 * @method main
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("请输入参数，以空格隔开...");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String param=br.readLine();
		String [] params=param.split(" ");
		String method = params[0];
		boolean json=true;
		if (params[1].equals("xml")) {
			json=false;
		}
		if (method.equals("11")) { //短信验证码 
			String accountSid="d13972dfc25634bc5fb182a279592f87";
			String token="cd5fb91698643800eac25b22a05e6e76";
			String appId="c30b95a0586f41e0ac48982ab30c6030";
			String templateId="192941";
			String to="13572232330";
			String para="1234";
			//String result = new JsonReqClient().templateSMS(accountSid, token, appId, templateId, to, para);
			//System.out.println("Response content is: " + result);
			testTemplateSMS(json, accountSid,token,appId, templateId,to,para);//短信发送接口
		}
	}
}
