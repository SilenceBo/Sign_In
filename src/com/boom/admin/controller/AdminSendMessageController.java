package com.boom.admin.controller;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.boom.interceptor.Role;
import com.boom.utils.Res;
import com.boom.utils.Result;
import com.boom.utils.ucpaas.rest.client.JsonReqClient;

@CrossOrigin(methods = RequestMethod.POST)
@Controller
@RequestMapping("/SendMessage")
public class AdminSendMessageController {
	
	@RequestMapping("/checkCode.action")
	@ResponseBody
	@Role(role=Role.ROLE_ADMIN)
	public Result SendMessage(String to){
		String accountSid="d13972dfc25634bc5fb182a279592f87";
		String token="cd5fb91698643800eac25b22a05e6e76";
		String appId="271d5a06f41942e28de3e4b77ede52a7";
		String templateId="227021";
		//String to="15702950602";
		int param = (int) (Math.random() * 1000000);
		
		String result = new JsonReqClient()
			.templateSMS(accountSid, token, appId, templateId, to, String.valueOf(param));
		System.out.println("Response content is: " + result);
		
		JSONObject jsonObject=JSONObject.fromObject(result);
		Res res=(Res)JSONObject.toBean(jsonObject, Res.class);
		if(res.getResp().getRespCode().equals("000000")){
			return Result.build(200, "消息发送成功", String.valueOf(param));
		}else{		
			return Result.build(500, "消息发送失败");
		}
	}
}
