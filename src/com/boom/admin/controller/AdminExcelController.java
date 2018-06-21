package com.boom.admin.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.boom.admin.service.AdminInSignService;
import com.boom.pojo.DbDay;
import com.boom.pojo.DbInSignCustomer;
import com.boom.pojo.DbTeacher;
import com.boom.utils.PageResult;

@CrossOrigin(methods = RequestMethod.POST)
@Controller
@RequestMapping("/excel")
public class AdminExcelController {
	
	@Autowired
	AdminInSignService adminInSignService; 
	
	/**
	 * 日表
	 * @param request
	 * @param response
	 * @param requestTime
	 * @throws Exception
	 */
	@RequestMapping("/downloadDayAllTeacher.action")
	public void dayExcel(HttpServletRequest request,
			HttpServletResponse response, String requestTime) throws Exception {
		//格式化当前日期
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String time = df.format(date);
		
		// 第一步，创建一个webbook，对应一个Excel文件    
        HSSFWorkbook wb = new HSSFWorkbook();    
       
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet    
        HSSFSheet sheet = wb.createSheet("签到情况表");
        //设置列宽
        sheet.setColumnWidth(1, 3000);
        sheet.setColumnWidth(2, 3000);
        sheet.setColumnWidth(3, 5000);
        
        //第三步，标题及标题样式
        HSSFRow firstHeaderRow=sheet.createRow(0);// 第一行
        HSSFCellStyle styleTitle = wb.createCellStyle();
        styleTitle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        styleTitle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        //生成一个字体
        HSSFFont font=wb.createFont();
        font.setColor(HSSFColor.BLACK.index);//HSSFColor.VIOLET.index //字体颜色
        font.setFontHeightInPoints((short)14);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);         //字体增粗
        styleTitle.setFont(font);
        //标题内容
        HSSFCell yearCell=firstHeaderRow.createCell(0);
        if(requestTime==null || requestTime.isEmpty()){
        	yearCell.setCellValue(time+"签到情况表");
        }else{
        	yearCell.setCellValue(requestTime+"签到情况表");
        }
        yearCell.setCellStyle(styleTitle); 
        sheet.addMergedRegion(new CellRangeAddress(0,1,0,4));//合并单元格
        
        // 第四步，在sheet中添加表头，并设置值表头样式   
        HSSFRow row = sheet.createRow((int) 2);           
        HSSFCellStyle style = wb.createCellStyle();            
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式  
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        //表头信息
        HSSFCell cell = row.createCell((short) 1);    
        cell.setCellValue("工号");    
        cell.setCellStyle(style);    
        cell = row.createCell((short) 2);    
        cell.setCellValue("姓名");    
        cell.setCellStyle(style);    
        cell = row.createCell((short) 3);    
        cell.setCellValue("签到时间");    
        cell.setCellStyle(style);       
    
        // 第五步，写入实体数据 实际应用中这些数据从数据库得到，    
        DbInSignCustomer dbInSignCustomer = new DbInSignCustomer();
        if(requestTime==null || requestTime.isEmpty()){
        	
        }else{
        	String[] str = requestTime.split("-");
        	String month = str[0]+"-"+str[1];
        	String day = str[2];
        	dbInSignCustomer.setImonth(month);
        	DbDay dbDay = new DbDay();
        	dbDay.setDname(day);
        	dbInSignCustomer.setDay(dbDay);
        }
        PageResult pg= adminInSignService.findTByMonthAndDayExcel(dbInSignCustomer);
        List<DbInSignCustomer> list = (List<DbInSignCustomer>) pg.getRows();
        if(null == list){
        	PrintWriter pw = response.getWriter();
        	pw.print("There is no data this month!");
        	return;
        }
        
    	int k = 0;
        for (int i = 0; i < list.size(); i++)    
        {   
        	DbInSignCustomer dsc = (DbInSignCustomer) list.get(i);
        	if(!dsc.getDbTeacher().getTnumber().equals("003")){	              	                        
	            if(dsc.getDbTeacher().getTuuid() != null){
	            	row = sheet.createRow((int) k + 3);
		            // 第四步，创建单元格，并设置值    
		            row.createCell((short) 1).setCellValue(dsc.getDbTeacher().getTnumber());    
		            row.createCell((short) 2).setCellValue(dsc.getDbTeacher().getTname());    
		            row.createCell((short) 3).setCellValue(dsc.getDbDay().get(0).getDtime());
		            k = k+1;
	            }
        	}
        }
        
        /*// 第六步，将文件存到指定位置    
        try    
        {    
            FileOutputStream fout = new FileOutputStream("D:/Members.xls");    
            wb.write(fout);    
            fout.close();    
        }    
        catch (Exception e)    
        {    
            e.printStackTrace();    
        }*/
        
        //第六步，下载excel  
        OutputStream out = null;    
        try {        
            out = response.getOutputStream();
            String fileName;
            if(requestTime==null || requestTime.isEmpty()){
            	fileName = time+".xls";// 文件名    
            }else{
            	fileName = requestTime+".xls";// 文件名    
            }
            response.setContentType("application/x-msdownload");    
            response.setHeader("Content-Disposition", "attachment; filename="    
                                                    + URLEncoder.encode(fileName, "UTF-8"));    
            wb.write(out);    
        } catch (Exception e) {    
            e.printStackTrace();    
        } finally {      
            try {       
                out.close();      
            } catch (IOException e) {      
                e.printStackTrace();    
            }      
        }  
	}
	
	/**
	 * 月表
	 * @param request
	 * @param response
	 * @param requestMonth
	 * @throws Exception
	 */
	@RequestMapping("/downloadMonthAllTeacher.action")
	public void monthExcel(HttpServletRequest request,
			HttpServletResponse response, String requestMonth) throws Exception {
		//格式化当前日期
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
		String time = df.format(date);
		
		// 第一步，创建一个webbook，对应一个Excel文件    
        HSSFWorkbook wb = new HSSFWorkbook();    
       
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet    
        HSSFSheet sheet = wb.createSheet("签到情况表");
        //设置列宽
        sheet.setColumnWidth(1, 3000);
        sheet.setColumnWidth(2, 3000);
        sheet.setColumnWidth(3, 3000);
        sheet.setColumnWidth(4, 3000);
        //第三步，标题及标题样式
        HSSFRow firstHeaderRow=sheet.createRow(0);// 第一行
        HSSFCellStyle styleTitle = wb.createCellStyle();
        styleTitle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        styleTitle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        //生成一个字体
        HSSFFont font=wb.createFont();
        font.setColor(HSSFColor.BLACK.index);//HSSFColor.VIOLET.index //字体颜色
        font.setFontHeightInPoints((short)14);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);         //字体增粗
        styleTitle.setFont(font);
        //标题内容
        HSSFCell yearCell=firstHeaderRow.createCell(0);
        if(requestMonth==null || requestMonth.isEmpty()){
        	yearCell.setCellValue(time+"签到情况表");
        }else{
        	yearCell.setCellValue(requestMonth+"签到情况表");
        }
        yearCell.setCellStyle(styleTitle); 
        sheet.addMergedRegion(new CellRangeAddress(0,1,0,5));//合并单元格
        
        // 第四步，在sheet中添加表头，并设置值表头样式   
        HSSFRow row = sheet.createRow((int) 2);           
        HSSFCellStyle style = wb.createCellStyle();            
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式  
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        //表头信息
        HSSFCell cell = row.createCell((short) 1);    
        cell.setCellValue("工号");    
        cell.setCellStyle(style);    
        cell = row.createCell((short) 2);    
        cell.setCellValue("姓名");    
        cell.setCellStyle(style);    
        cell = row.createCell((short) 3);    
        cell.setCellValue("应签次数");    
        cell.setCellStyle(style);
        cell = row.createCell((short) 4);    
        cell.setCellValue("实签次数");    
        cell.setCellStyle(style);
    
        // 第五步，写入实体数据 实际应用中这些数据从数据库得到，    
        DbInSignCustomer dbInSignCustomer = new DbInSignCustomer();
        /*DbTeacher dbTeacher = new DbTeacher();
        dbTeacher.setTname(teacherName);
        dbInSignCustomer.setDbTeacher(dbTeacher);*/
        
        if(requestMonth==null || requestMonth.isEmpty()){
        	dbInSignCustomer.setImonth(time);
        }else{
        	dbInSignCustomer.setImonth(requestMonth);       	
        }
        PageResult pg= adminInSignService.findTByMonthAndDayExcel(dbInSignCustomer);
        List<DbInSignCustomer> list = (List<DbInSignCustomer>) pg.getRows();
        if(null == list){
        	PrintWriter pw = response.getWriter();
        	pw.print("There is no data this month!");
        	return;
        }
        
        int k = 0;
        for (int i = 0; i < list.size(); i++)    
        {   
        	DbInSignCustomer dsc = (DbInSignCustomer) list.get(i);
        	if(!dsc.getDbTeacher().getTnumber().equals("003")){	              	                        
	            if(dsc.getDbTeacher().getTuuid() != null){
	            	row = sheet.createRow((int) k + 3);
		            // 第四步，创建单元格，并设置值    
	            	row.createCell((short) 1).setCellValue(dsc.getDbTeacher().getTnumber());    
		            row.createCell((short) 2).setCellValue(dsc.getDbTeacher().getTname());    
		            row.createCell((short) 3).setCellValue(dsc.getIshouldSign());
		            row.createCell((short) 4).setCellValue(dsc.getIrealitySign());
		            k = k+1;
	            }
        	}        	
        }
        
        /*// 第六步，将文件存到指定位置    
        try    
        {    
            FileOutputStream fout = new FileOutputStream("D:/Members.xls");    
            wb.write(fout);    
            fout.close();    
        }    
        catch (Exception e)    
        {    
            e.printStackTrace();    
        }*/
        
        //第六步，下载excel  
        OutputStream out = null;    
        try {        
            out = response.getOutputStream();
            String fileName;
            if(requestMonth==null || requestMonth.isEmpty()){
            	fileName = time+".xls";// 文件名    
            }else{
            	fileName = requestMonth+".xls";// 文件名    
            }
            response.setContentType("application/x-msdownload");    
            response.setHeader("Content-Disposition", "attachment; filename="    
                                                    + URLEncoder.encode(fileName, "UTF-8"));    
            wb.write(out);    
        } catch (Exception e) {    
            e.printStackTrace();    
        } finally {      
            try {       
                out.close();      
            } catch (IOException e) {      
                e.printStackTrace();    
            }      
        }  
	}
	
	/**
	 * 月详细表
	 * @param request
	 * @param response
	 * @param requestTime
	 * @throws Exception
	 */
	@RequestMapping("/downloadTeacherAll.action")
	public void monthAllExcel(HttpServletRequest request,
			HttpServletResponse response, String requestTime) throws Exception {
		//格式化当前日期
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
		String time = df.format(date);
		
		// 第一步，创建一个webbook，对应一个Excel文件    
        HSSFWorkbook wb = new HSSFWorkbook();    
       
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet    
        HSSFSheet sheet = wb.createSheet("签到情况表");
        //设置列宽
        sheet.setColumnWidth(1, 3000);
        sheet.setColumnWidth(2, 3000);
        sheet.setColumnWidth(3, 3000);
        sheet.setColumnWidth(4, 3000);
        sheet.setColumnWidth(5, 5000);
        
        //第三步，标题及标题样式
        HSSFRow firstHeaderRow=sheet.createRow(0);// 第一行
        HSSFCellStyle styleTitle = wb.createCellStyle();
        styleTitle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        styleTitle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        //生成一个字体
        HSSFFont font=wb.createFont();
        font.setColor(HSSFColor.BLACK.index);//HSSFColor.VIOLET.index //字体颜色
        font.setFontHeightInPoints((short)14);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);         //字体增粗
        styleTitle.setFont(font);
        //标题内容
        HSSFCell yearCell=firstHeaderRow.createCell(0);
        if(requestTime==null || requestTime.isEmpty()){
        	yearCell.setCellValue(time+"月签到详细情况表");
        }else{
        	yearCell.setCellValue(requestTime+"月签到详细情况表");
        }
        yearCell.setCellStyle(styleTitle); 
        sheet.addMergedRegion(new CellRangeAddress(0,1,0,5));//合并单元格
        
        // 第四步，在sheet中添加表头，并设置值表头样式   
        HSSFRow row = sheet.createRow((int) 2);           
        HSSFCellStyle style = wb.createCellStyle();            
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式  
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        
        //生成一个字体
        HSSFFont font2 =wb.createFont();
        font.setColor(HSSFColor.BLACK.index);//HSSFColor.VIOLET.index //字体颜色
        font.setFontHeightInPoints((short)16);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);         //字体增粗
        //把字体应用到当前的样式
        style.setFont(font2);
        
        
        //表头信息
        HSSFCell cell = row.createCell((short) 1);    
        cell.setCellValue("姓名");    
        cell.setCellStyle(style);    
        cell = row.createCell((short) 4);    
        cell.setCellValue("日期");    
        cell.setCellStyle(style);    
        cell = row.createCell((short) 5);    
        cell.setCellValue("签到时间");    
        cell.setCellStyle(style);
        cell = row.createCell((short) 2);    
        cell.setCellValue("应签次数");    
        cell.setCellStyle(style);
        cell = row.createCell((short) 3);    
        cell.setCellValue("实签次数");    
        cell.setCellStyle(style);
    
        // 第五步，写入实体数据 实际应用中这些数据从数据库得到，    
        DbInSignCustomer dbInSignCustomer = new DbInSignCustomer();
        if(requestTime==null || requestTime.isEmpty()){
        	
        }else{
        	dbInSignCustomer.setImonth(requestTime);
        }
        PageResult pg= adminInSignService.findTByMonthAndDayExcel2(dbInSignCustomer);
        List<DbInSignCustomer> list = (List<DbInSignCustomer>) pg.getRows();
        
        if(null == list){
        	PrintWriter pw = response.getWriter();
        	pw.print("There is no data this month!");
        	return;
        }
        
    	int k = 0;
        for (int i = 0; i < list.size(); i++)    
        {   
        	DbInSignCustomer dsc = (DbInSignCustomer) list.get(i);
        	if(!dsc.getDbTeacher().getTnumber().equals("003")){	              	                        
	            if(dsc.getDbTeacher().getTuuid() != null){
	            	for(int j=0; j<31; j++){
	            		if(j+1 > dsc.getDbDay().size()){
	            			continue;
	            		}else{
	            			row = sheet.createRow((int) k + 3);
				            // 第四步，创建单元格，并设置值        
				            row.createCell((short) 4).setCellValue(dsc.getDbDay().get(j).getImonth() + "-"+ dsc.getDbDay().get(j).getDname());    
				            row.createCell((short) 5).setCellValue(dsc.getDbDay().get(j).getDtime());
				            if(j == 0){
				            	row.createCell((short) 1).setCellValue(dsc.getDbTeacher().getTname());
				            	row.createCell((short) 2).setCellValue(dsc.getIshouldSign());
				            	row.createCell((short) 3).setCellValue(dsc.getIrealitySign());
				            }else{
				            	row.createCell((short) 1).setCellValue("");
				            	row.createCell((short) 2).setCellValue("");
				            	row.createCell((short) 3).setCellValue("");
				            }
				            k = k+1;
	            		}
	            	}
	            }
        	}
        }
        
        /*// 第六步，将文件存到指定位置    
        try    
        {    
            FileOutputStream fout = new FileOutputStream("D:/Members.xls");    
            wb.write(fout);    
            fout.close();    
        }    
        catch (Exception e)    
        {    
            e.printStackTrace();    
        }*/
        
        //第六步，下载excel  
        OutputStream out = null;    
        try {        
            out = response.getOutputStream();
            String fileName;
            if(requestTime==null || requestTime.isEmpty()){
            	fileName = time+".xls";// 文件名    
            }else{
            	fileName = requestTime+".xls";// 文件名    
            }
            response.setContentType("application/x-msdownload");    
            response.setHeader("Content-Disposition", "attachment; filename="    
                                                    + URLEncoder.encode(fileName, "UTF-8"));    
            wb.write(out);    
        } catch (Exception e) {    
            e.printStackTrace();    
        } finally {      
            try {       
                out.close();      
            } catch (IOException e) {      
                e.printStackTrace();    
            }      
        }  
	}
}
