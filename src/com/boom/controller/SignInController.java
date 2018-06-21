package com.boom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.boom.admin.service.AdminWaysignservice;
import com.boom.pojo.DbTeacher;
import com.boom.service.SignInService;
import com.boom.utils.Result;

/**
 * 签到访问口
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/InSign")
public class SignInController {
	
	@Autowired
	private SignInService signInService;
	@Autowired
	private AdminWaysignservice adminWaysignservice;
	
	@ResponseBody
	@RequestMapping("/Into.action")
	public Result Into(DbTeacher dbTeacher) {
		//System.out.println(dbTeacher);
		return signInService.updateOk(dbTeacher);
	}
	
	@ResponseBody
	@RequestMapping("/getWay.action")
	public Result getWay() {
		return adminWaysignservice.findAll();
	}
}
