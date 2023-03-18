<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>${table_name}のデータ更新</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
	<h3>${table_name}のデータ更新</h3>
	<form>
		<input type="button" value="戻る" onClick="history.back()" dir="rtl">
	</form>
	<form action="/base/DataBaseServlet?action=update" method="post">
							<input type="hidden" name="type" value="update_form">
	
		<div style="display: flex;">
			<div>
				<p>値の更新</p>
				<c:forEach var="i" begin="1" end="5">
					<ul>
						<li><select name="setColumn${i}">
								<c:forEach items="${columns}" var="column">
									<option value="${column.columnName}">${column.columnName}</option>
								</c:forEach>
						</select>　<select name="culc${i}">
								<option value=""></option>
								<option value="=">=(指定値に更新)</option>
								<option value="+">+(加算)</option>
								<option value="-">-(減算)</option>
								<option value="*">*(乗算)</option>
								<option value="/">/(除算)</option>
								<option value="%">/(剰余)</option>
						</select>　 <input type="text" name="value${i}">　</li>
					</ul>
				</c:forEach>
			</div>
			<div>
				<p>複数カラムを一括更新</p>
				更新したいカラムを入れてください。
				<p>
					<textarea name="batch" cols="10" rows="10"></textarea>
				</p>
				<p>
					<select name="culcbatch">
						<option value=""></option>
						<option value="=">=(指定値に更新)</option>
						<option value="+">+(加算)</option>
						<option value="-">-(減算)</option>
						<option value="*">*(乗算)</option>
						<option value="/">/(除算)</option>
						<option value="%">/(剰余)</option>
					</select><input type="text" name="batchvalue">
				</p>
			</div>
			<div>
				<p>更新条件</p>
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
						</select> <input type="text" name="filterValue${i}"></li>
					</ul>
				</c:forEach>
			</div>
		</div>
		<input type="submit" style="color: #ffffff; background-color: #ff0000;" value="更新">
	</form>
	既存の値
		<table border="1">
			<tr>
				<c:forEach items="${columns}" var="column">
					<td>${column.columnName}</td>
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
				</tr>
			</c:forEach>
		</table>
</body>
</html>