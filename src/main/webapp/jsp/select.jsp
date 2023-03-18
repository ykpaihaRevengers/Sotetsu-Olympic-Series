<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>${table_name}のデータ一覧</title>
</head>
<body>
	<h3>${table_name}のデータ一覧</h3>
	<c:if test="${!empty db_execute_msg}">
		<p style="color: #666666;">${db_execute_msg}</p>
	</c:if>
	<table>
		<tr>
			<td>
				<form action="/base/DataBaseServlet?action=insert" method="post">
					<input type="submit" value="挿入" style="width: 90%; padding: 10px; font-size: 30px;"> <input type="hidden" name="gotoPage" value="dbmng/insert.jsp">
				</form>
			</td>
			<td>
				<form action="/base/DataBaseServlet?action=update" method="post">
					<input type="submit" value="更新" style="width: 90%; padding: 10px; font-size: 30px;"> <input type="hidden" name="gotoPage" value="dbmng/update.jsp">
				</form>
			</td>
			<td>
				<form action="/base/DataBaseServlet?action=delete" method="post">
					<input type="submit" value="削除" style="width: 90%; padding: 10px; font-size: 30px;"> <input type="hidden" name="gotoPage" value="dbmng/delete.jsp">
				</form>
			</td>
		</tr>
	</table>
	<p dir="rtl">
		<a href="/base/DataBaseServlet" style="text-align: right;">テーブル一覧に戻る</a>
	</p>

	<br>
	<p>条件を絞って検索</p>

	<form action="/base/DataBaseServlet?action=select" method="post">
		<c:if test="${!empty filter_msg}">
		条件：<c:forEach items="${filter_msg}" var="message">
		${message}　
		</c:forEach>
		</c:if>
		<p>
			カラムを絞って検索<input type="text" name="select_columns" style="width: 800px;">
		</p>
		<div style="display: inline-flex">
			<c:forEach var="i" begin="1" end="3">
			条件式${i}
				<select name="filter_column${i}">
					<option value=""></option>
					<c:forEach items="${columns}" var="column">
						<option value="${column.columnName}">${column.columnName}</option>
					</c:forEach>
				</select>
				<select name="opel${i}">
					<option value=""></option>
					<option value="=">=(完全一致)</option>
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
				</select>
				<input type="text" name="filter_value${i}">
			</c:forEach>
		</div>
		<c:if test="${!empty sort_msg}">
		ソート基準カラム：${message}：${asc_desc}<br>
		</c:if>
		<div style="display: inline-flex">
			基準カラム <select name="order_by">
				<option value=""></option>
				<c:forEach items="${columns}" var="column">
					<option value="${column.columnName}">${column.columnName}</option>
				</c:forEach>
			</select> 順序 <select name="desc">
				<option value=""></option>
				<option value="">昇順</option>
				<option value="DESC">降順</option>
			</select>
		</div>
		<input type="submit" style="color: #ffffff; background-color: #22ff79;" value="条件を指定して検索"> <input type="hidden" name="db_execute" value="${db_execute}">

	</form>

	<br />
	<c:if test="${table_name eq 'schedule_manuscript'}">
		<jsp:include page="/jsp/individual/checkTableList.jsp"></jsp:include>
	</c:if>
	<br />
	<table border="1">
		<tr>
			<c:forEach items="${columns}" var="column">
				<td>${column.columnName}</td>
			</c:forEach>
		</tr>
		<tr>
			<c:forEach items="${dbDataList}" var="record">
				<tr>

					<c:forEach items="${record}" var="element">
						<td>${element.value}</td>
					</c:forEach>
				</tr>
			</c:forEach>
	</table>
	<input type="button" value="csvダウンロード" onclick="location.href='GenerateCsvServlet'">


</body>
</html>