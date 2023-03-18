<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>カラム情報</title>
</head>
<body>
	<jsp:include page="/jsp/home.jsp"></jsp:include>
	<h3>テーブル「${table_name}」のカラム情報</h3>
	<hr>
	<table border="1">
		<tr>
			<td>No</td>
			<td>カラム名</td>
			<td>タイプ</td>
			<td>文字数</td>
			<td>Null 値の許容</td>
			<td>初期値</td>
			<td></td>
			<td>カラムの削除</td>
			<td>カラム名の変更</td>


		</tr>
		<c:forEach items="${table_attr_list}" var="table_attr">
			<tr>
				<td>${table_attr.ordinalPosition}</td>
				<td>${table_attr.columnName}</td>
				<td>${table_attr.dataType}</td>
				<td><c:if test="${table_attr.varcharLength ne 0}">
				${table_attr.varcharLength}
				</c:if></td>
				<td>${table_attr.isNullable}</td>
				<td>${table_attr.defaultValue}</td>
				<td></td>
				<td>
					<form action="/base/DataBaseServlet?action=alter_table" method="post">
						<input type="hidden" name="columnName" value="${table_attr.columnName}"> <input type="hidden" name="alterAction" value=" DROP COLUMN "> <input type="submit"
							style="color: #ffffff; background-color: #ff0000;" value="カラムを削除">
					</form>
				</td>
				<td>
					<form action="/base/DataBaseServlet?action=alter_table" method="post">
						<input type="hidden" name="columnName" value="${table_attr.columnName}"> <input type="hidden" name="alterAction" value=" RENAME COLUMN "> <input type="submit"
							style="color: #ffffff; background-color: #3cb371;" value="カラム名を変更">
					</form>
				</td>
			</tr>
		</c:forEach>
	</table>
	<jsp:include page="/jsp/dbmng/addColumn.jsp"></jsp:include>

</body>
</html>