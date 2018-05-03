package com.imooc.drdc.utils;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

public class ExportUtils {
	/**
	 * ����sheet��ͷ��Ϣ
	 * @author David
	 * @param headersInfo
	 * @param sheet
	 */
	public static void outputHeaders(String[] headersInfo,HSSFSheet sheet ){
		HSSFRow row = sheet.createRow(0);
		for (int i = 0; i < headersInfo.length; i++) {
			sheet.setColumnWidth(i, 4000);
			row.createCell(i).setCellValue(headersInfo[i]);
		}
	}
	
	public static void outputColumns(String[] headersInfo,
			List columnsInfo,HSSFSheet sheet,int rowIndex ){
		HSSFRow row ;
		int headerSize = headersInfo.length;
		int columnSize = columnsInfo.size();
		//ѭ�����������
		for (int i = 0; i < columnsInfo.size(); i++) {
			row = sheet.createRow(rowIndex+i);
			Object obj = columnsInfo.get(i);
			//ѭ��ÿ�ж�����
			for (int j = 0; j < headersInfo.length; j++) {
				Object value = getFieldValueByName(headersInfo[j],obj);
				row.createCell(j).setCellValue(value.toString());
			}
		}
		
	}
	/**
	 * ���ݶ�������Ի�ȡֵ
	 * @author David
	 * @param string
	 * @param obj
	 * @return
	 */
	private static Object getFieldValueByName(String fieldName, Object obj) {
		String firstLetter = fieldName.substring(0,1).toUpperCase();
		String getter = "get" +firstLetter + fieldName.substring(1);
		try {
			Method method = obj.getClass().getMethod(getter, new Class[]{});
			Object value = method.invoke(obj, new Object[]{});
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("���Բ����ڣ�");
			return null;
		} 
	}
}
