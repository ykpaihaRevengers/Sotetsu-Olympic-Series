<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>${table_name}のデータ削除</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/style.css">
</head>
<body>
	<h3>${table_name}のデータ削除</h3>
	<form>
		<input type="button" value="戻る" onClick="history.back()" dir="rtl">
	</form>
	<p>条件式から削除</p>
	<form action="/base/DataBaseServlet?action=delete" method="post">
		<input type="hidden" name="type" value="filter_by_column">
		<c:forEach var="i" begin="1" end="3">
			<ul>
				<li><select name="filterColumn${i}">
						<c:forEach items="${columns}" var="column">
							<option value="${column.columnName}">${column.columnName}</option>
						</c:forEach>
				</select> <select name="opel${i}">
						<option value=""></option>
						<option value="=">＝(完全一致)</option>
						<option value=">=">＞＝(以上)</option>
						<option value="<">＜(未満)</option>
						<option value="<=">＜＝(以下)</option>
						<option value=">">＞(！以下)</option>
						<option value="LIKE%">LIKE%(前方一致)</option>
						<option value="%LIKE">%LIKE(後方一致)</option>
						<option value="LIKE">LIKE(部分一致)</option>
						<option value="!=">!=(否定)</option>
						<option value="BETWEEN">BETWEEN(範囲)</option>
						<option value="IS_NULL">ISNULL(null値)</option>
				</select> <input type="text" name="value${i}"></li>
			</ul>
		</c:forEach>
		<input type="submit" style="color: #ffffff; background-color: #ff0000;" value="削除">
	</form>
	<p>表から選択して削除</p>
	<table border="1">
		<tr>
			<c:forEach items="${columns}" var="column">
				<th>${column.columnName}</th>
			</c:forEach>
		</tr>
		<tr>
			<c:forEach items="${columns}" var="column">
				<td></td>
			</c:forEach>
		</tr>
		<c:forEach items="${selected_data.dbDataList}" var="record">
			<tr>
				<c:forEach items="${record}" var="element">
					<td>${element.value}</td>
				</c:forEach>
				<td>
					<form action="/base/DataBaseServlet?action=delete" method="post">
						<input type="hidden" name="type" value="select_record">
						<c:forEach items="${record}" var="element" begin="0" varStatus="status">
							<input type="hidden" name="filterValue${status.count}" value="${element.value}">
						</c:forEach>
						<input type="submit" style="color: #ffffff; background-color: #ff0000;" value="削除">
					</form>
				</td>
			</tr>
		</c:forEach>
	</table>





</body>
</html>