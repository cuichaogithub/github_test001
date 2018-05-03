create table student(  
    sid int auto_increment not null primary key,
    stunum varchar(20), 
    stuname varchar(20),  
    stuage varchar(20),  
    stusex varchar(20),
    stubirthday varchar(20),
    stuhobby varchar(50)  
);  
http://localhost:8080/excel_web_drdc/jsp/studentList.jsp
http://localhost:8080/excel_web_drdc/jsp/layout.jsp
http://localhost:8080/excel_web_drdc/jsp/importList.jsp
http://localhost:8080/excel_web_drdc/index.jsp

create table book(  
    b_number int primary key,  
    b_name varchar(20),  
    b_auther varchar(20),  
    b_publish varchar(20),  
    b_price double,  
    b_stock int  
);  
  
insert into t_book(b_number,b_name,b_auther,b_publish,b_price,b_stock)  
values(1001,'Javaweb开发技术','李晓蕾','清华大学出版社', 44.50,10);  
  
insert into t_book(b_name,b_auther,b_publish,b_price,b_stock)  
values('Javaweb程序设计','陈培强','人民邮电出版社', 48.00,10);