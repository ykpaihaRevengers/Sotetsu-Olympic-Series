<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>カラムの追加</title>
</head>
<body>
	<form action="/base/DataBaseServlet?action=alter_table" method="post">
		<input type="hidden" name="alterAction" value=" ADD ">
		<p>カラムの追加</p>
		<table>
			<tr>
				<th>カラム名</th>
				<th>DBタイプ</th>
				<th>文字数の指定</th>
				<th>NOT NULL</th>
				<th>デフォルト値</th>
			</tr>
			<c:forEach var="i" begin="1" end="10" step="1">
				<tr>
					<td><input type="text" style="width: 120px;" name="column_name${i}"></td>
					<td><select name="data_type${i}">
							<option value=""></option>
							<option value="SERIAL">SERIAL</option>
							<option value="INTEGER">INTEGER</option>
							<option value="TEXT">TEXT</option>
							<option value="VARCHAR">VARCHAR</option>
							<option value="CHAR">CHAR</option>
							<option value="DATE">DATE</option>
							<option value="TIME">TIME</option>
							<option value="TIMESTAMP">TIMESTAMP</option>

					</select></td>
					<td><input type="text" pattern="^[1-9][0-9]*$" style="width: 20px;" name="max_length${i}" /></td>
					<td><input type="checkbox" name="not_null${i}" value=" NOT NULL"></td>
					<td><input type="text" style="width: 100px;" name="default_value${i}"></td>
				</tr>
			</c:forEach>
		</table>
		<input type="submit" style="color: #ffffff; background-color: #2287ff;" value="カラムの追加">

	</form>
</body>
</html>