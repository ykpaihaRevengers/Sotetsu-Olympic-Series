<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>DB更新前の確認</title>
</head>
<body>
	<p>以下の通りデータベースを更新します。</p>
	<form>
		<input type="button" value="戻る" onClick="history.back()">
	</form>
	操作タイプ ：${db_execute.executionType}
	<br> 操作テーブル名：${db_execute.tableName}
	<c:if test="${db_execute.executionType eq 'CREATE TABLE '}">
		<br> PRIMARY KEY ：${db_execute.primaryKey}<br>
	カラム一覧　　：
		<c:forEach items="${db_execute.columnAttr}" var="column_attr">
		${column_attr}<br>
		</c:forEach>
	</c:if>

	<c:if test="${db_execute.executionType eq 'ALTER TABLE '}">
		<p>
			<c:if test="${db_execute.action eq ' RENAME TO '}">
		テーブル名を以下の文字に変更します。
		</c:if>
			<c:if test="${db_execute.action eq ' RENAME COLUMN '}">
		カラム名を以下の文字に変更します。
		</c:if>
			<c:if test="${db_execute.action eq ' DROP COLUMN '}">
		${db_execute.replaceColumn}カラムを削除します。
		</c:if>
			<c:if test="${db_execute.action eq ' ADD '}">
		以下のカラムを追加します。
				<c:forEach items="${db_execute.addColumnList}" var="addColumn">
		${addColumn}<br>
				</c:forEach>
			</c:if>
		</p>
	</c:if>

	<c:if test="${db_execute.executionType eq 'INSERT INTO '}">
		<table border="1">
			<tr>
				<c:forEach items="${db_execute.insertColumns}" var="column">
					<td>${column.columnName}</td>
				</c:forEach>
			</tr>
			<tr>
				<c:forEach items="${db_execute.insertColumns}" var="column">
					<td>${column.dataType}</td>
				</c:forEach>
			</tr>
			<c:forEach items="${db_execute.insertValues}" var="insertValue">
				<tr>
					<c:forEach items="${insertValue}" var="value">
						<td><b>${value}</b></td>
					</c:forEach>
				</tr>
			</c:forEach>

		</table>

	</c:if>
	<c:if test="${db_execute.executionType eq 'DELETE FROM '}">
		<p>
			削除対象
			<c:if test="${type eq 'select_record'}">レコード</c:if>
			<c:if test="${type eq 'filter_by_column'}">条件</c:if>
		</p>
		<c:if test="${type eq 'select_record'}">
			<table border="1" style="color: #ff1500;">
				<tr>
					<c:forEach items="${db_execute.filters}" var="element">
						<td>${element.filterColumn}</td>
					</c:forEach>
				</tr>
				<tr>
					<c:forEach items="${db_execute.filters}" var="element">
						<td>${element.value}</td>
					</c:forEach>
				</tr>
			</table>
		</c:if>
		<c:if test="${type eq 'filter_by_column'}">
			<ul>
				<c:forEach items="${db_execute.filters}" var="element">
					<li>${element.filterColumn}${element.operator}${element.value}</li>
				</c:forEach>
			</ul>
		</c:if>
	</c:if>

	<c:if test="${db_execute.executionType eq 'UPDATE '}">
		<p>更新内容</p>
		<ul>
			<c:forEach items="${db_execute.setting}" var="element">
				<li>${element.setColumn}${element.calc}${element.theoryValue}</li>
			</c:forEach>
		</ul>

		<p>更新条件</p>
		<ul>
			<c:forEach items="${db_execute.filters}" var="element">
				<li>${element.filterColumn}${element.operator}${element.value}</li>
			</c:forEach>
		</ul>
	</c:if>

	<form action="/base/DataBaseServlet?action=executeUpdate" method="post">
		<c:if test="${db_execute.executionType eq 'ALTER TABLE '}">
			<c:if test="${db_execute.action eq ' RENAME TO ' or db_execute.action eq ' RENAME COLUMN '}">
				<input type="text" name="renamedName">
				<br>
				<br>
			</c:if>
		</c:if>
		<input type="submit" style="color: #ffffff; background-color: #ff1500;" value="SQLを実行する（元には戻せません）"> <input type="hidden" name="db_execute" value="${db_execute}"> <input type="hidden"
			name="page" value="${page}">
	</form>
	<form action="/base/DataBaseServlet?action=quit" method="post">
		<input type="hidden" name="db_execute" value="${db_execute}"> <input type="submit" style="color: #ffffff; background-color: #1129ee;" value="DB更新を取りやめる">
	</form>
</body>
</html>