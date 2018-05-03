package com.imooc.drdc.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.struts2.ServletActionContext;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.opensymphony.xwork2.ActionSupport;

public class FileDownloadAction extends ActionSupport {
	
	private String templateId;
	
	private String templateName;

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	
	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}
	
	//��ȡ��������Ϣ
	public InputStream getInputStream() throws IOException{
		//���ɵ���ģ���ļ�
		createTemplate();
		String path = ServletActionContext.getServletContext().getRealPath("/template");
		String filepath = path +"\\" + templateName + ".xls";
		File file = new File(filepath);
		
		return FileUtils.openInputStream(file);
	}
	

	//��ȡ�����ļ���
	public String getDownloadFileName(){
		String downloadFileName = "";
		String filename = templateName + ".xls";
		try {
			//�����������
			downloadFileName = URLEncoder.encode(filename,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return downloadFileName;
	}
	
	private void createTemplate() {
		//��ȡ����xml�ļ�·��
		String path = ServletActionContext.getServletContext().getRealPath("/template");
		File file = new File(path,templateId+".xml");
		SAXBuilder builder = new SAXBuilder();
		try {
			//����xml�ļ�
			Document parse = builder.build(file);
			//����Excel
			HSSFWorkbook wb = new HSSFWorkbook();
			//����sheet
			HSSFSheet sheet = wb.createSheet("Sheet0");
			
			//��ȡxml�ļ����ڵ�
			Element root = parse.getRootElement();
			//��ȡģ������
			templateName = root.getAttribute("name").getValue();
			
			int rownum = 0;
			int column = 0;
			//�����п�
			Element colgroup = root.getChild("colgroup");
			setColumnWidth(sheet,colgroup);
			
			//���ñ���
			Element title = root.getChild("title");
			List<Element> trs = title.getChildren("tr");
			for (int i = 0; i < trs.size(); i++) {
				Element tr = trs.get(i);
				List<Element> tds = tr.getChildren("td");
				HSSFRow row = sheet.createRow(rownum);
				HSSFCellStyle cellStyle = wb.createCellStyle();
				cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				for(column = 0;column <tds.size();column ++){
					Element td = tds.get(column);
					HSSFCell cell = row.createCell(column);
					Attribute rowSpan = td.getAttribute("rowspan");
					Attribute colSpan = td.getAttribute("colspan");
					Attribute value = td.getAttribute("value");
					if(value !=null){
						String val = value.getValue();
						cell.setCellValue(val);
						int rspan = rowSpan.getIntValue() - 1;
						int cspan = colSpan.getIntValue() -1;
						
						//��������
						HSSFFont font = wb.createFont();
						font.setFontName("����_GB2312");
						font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//����Ӵ�
//						font.setFontHeight((short)12);
						font.setFontHeightInPoints((short)12);
						cellStyle.setFont(font);
						cell.setCellStyle(cellStyle);
						//�ϲ���Ԫ�����
						sheet.addMergedRegion(new CellRangeAddress(rspan, rspan, 0, cspan));
					}
				}
				rownum ++;
			}
			//���ñ�ͷ
			Element thead = root.getChild("thead");
			trs = thead.getChildren("tr");
			for (int i = 0; i < trs.size(); i++) {
				Element tr = trs.get(i);
				HSSFRow row = sheet.createRow(rownum);
				List<Element> ths = tr.getChildren("th");
				for(column = 0;column < ths.size();column++){
					Element th = ths.get(column);
					Attribute valueAttr = th.getAttribute("value");
					HSSFCell cell = row.createCell(column);
					if(valueAttr != null){
						String value =valueAttr.getValue();
						cell.setCellValue(value);
					}
				}
				rownum++;
			}
			
			//��������������ʽ
			Element tbody = root.getChild("tbody");
			Element tr = tbody.getChild("tr");
			int repeat = tr.getAttribute("repeat").getIntValue();
			
			List<Element> tds = tr.getChildren("td");
			for (int i = 0; i < repeat; i++) {
				HSSFRow row = sheet.createRow(rownum);
				for(column =0 ;column < tds.size();column++){
					Element td = tds.get(column);
					HSSFCell cell = row.createCell(column);
					setType(wb,cell,td);
				}
				rownum++;
			}
			
			//����Excel����ģ��
			File tempFile = new File(path, templateName + ".xls");
			tempFile.delete();
			tempFile.createNewFile();
			FileOutputStream stream = FileUtils.openOutputStream(tempFile);
			wb.write(stream);
			stream.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * ���Ե�Ԫ����ʽ
	 * @author David
	 * @param wb
	 * @param cell
	 * @param td
	 */
	private static void setType(HSSFWorkbook wb, HSSFCell cell, Element td) {
		Attribute typeAttr = td.getAttribute("type");
		String type = typeAttr.getValue();
		HSSFDataFormat format = wb.createDataFormat();
		HSSFCellStyle cellStyle = wb.createCellStyle();
		if("NUMERIC".equalsIgnoreCase(type)){
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			Attribute formatAttr = td.getAttribute("format");
			String formatValue = formatAttr.getValue();
			formatValue = StringUtils.isNotBlank(formatValue)? formatValue : "#,##0.00";
			cellStyle.setDataFormat(format.getFormat(formatValue));
		}else if("STRING".equalsIgnoreCase(type)){
			cell.setCellValue("");
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cellStyle.setDataFormat(format.getFormat("@"));
		}else if("DATE".equalsIgnoreCase(type)){
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			cellStyle.setDataFormat(format.getFormat("yyyy-m-d"));
		}else if("ENUM".equalsIgnoreCase(type)){
			CellRangeAddressList regions = 
				new CellRangeAddressList(cell.getRowIndex(), cell.getRowIndex(), 
						cell.getColumnIndex(), cell.getColumnIndex());
			Attribute enumAttr = td.getAttribute("format");
			String enumValue = enumAttr.getValue();
			//���������б�����
			DVConstraint constraint = 
				DVConstraint.createExplicitListConstraint(enumValue.split(","));
			//������Ч�Զ���
			HSSFDataValidation dataValidation = new HSSFDataValidation(regions, constraint);
			wb.getSheetAt(0).addValidationData(dataValidation);
		}
		cell.setCellStyle(cellStyle);
	}

	/**
	 * �����п�
	 * @author David
	 * @param sheet
	 * @param colgroup
	 */
	private static void setColumnWidth(HSSFSheet sheet, Element colgroup) {
		List<Element> cols = colgroup.getChildren("col");
		for (int i = 0; i < cols.size(); i++) {
			Element col = cols.get(i);
			Attribute width = col.getAttribute("width");
			String unit = width.getValue().replaceAll("[0-9,\\.]", "");
			String value = width.getValue().replaceAll(unit, "");
			int v=0;
			if(StringUtils.isBlank(unit) || "px".endsWith(unit)){
				v = Math.round(Float.parseFloat(value) * 37F);
			}else if ("em".endsWith(unit)){
				v = Math.round(Float.parseFloat(value) * 267.5F);
			}
			sheet.setColumnWidth(i, v);
		}
	}
}
