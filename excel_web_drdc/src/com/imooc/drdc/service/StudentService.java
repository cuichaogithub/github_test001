package com.imooc.drdc.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.imooc.drdc.model.ImportData;
import com.imooc.drdc.model.Student;
import com.imooc.drdc.utils.DB;


public class StudentService {
	/**
	 * ��ѯѧ���б���Ϣ
	 * @author David
	 * @param currentPage
	 * @param pageSize
	 * @param sort
	 * @param order
	 * @return
	 */
	public List<Student> list(int currentPage, int pageSize,String sort,String order) {
		Connection conn = DB.createConn();
		String sql = "select * from t_student where 1=1 ";
		if(StringUtils.isNotBlank(sort)){
			sql += "order by " + sort ;
		}
		if(StringUtils.isNotBlank(order)){
			sql += " " + order ;
		}
		if(currentPage>0 && pageSize >0){
			sql += " limit " + (currentPage-1)*pageSize +" , "  + pageSize ;
		}
		
		PreparedStatement ps = DB.prepare(conn, sql);
		List<Student> slist = new ArrayList<Student>();
		try {
			ResultSet rs = ps.executeQuery();
			Student s = null;
			while(rs.next()) {
				s = new Student();
				s.setSid(rs.getInt("sid"));
				s.setStunum(rs.getString("stunum"));
				s.setStuname(rs.getString("stuname"));
				s.setStuage(rs.getString("stuage"));
				s.setStusex(rs.getString("stusex"));
				s.setStubirthday(rs.getString("stubirthday"));
				s.setStuhobby(rs.getString("stuhobby"));
				
				slist.add(s);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		DB.close(ps);
		DB.close(conn);
		return slist;
	}
	
	public int getTotal(){
		Connection conn = DB.createConn();
		int total = 0;
		String sql = "select count(*) from t_student";
		PreparedStatement ps = DB.prepare(conn, sql);
		try {
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				total = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		DB.close(ps);
		DB.close(conn);
		return total;
	}
}
