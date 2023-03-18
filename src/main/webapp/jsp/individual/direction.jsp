<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>${table_name}のdirectionデータ一覧</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/matrixShuffle.css">
</head>
<body>
	<h3>${table_name}のdirectionデータ一覧</h3>
	<c:if test="${!empty execute_msg}">
		<p style="color: #666666;">${execute_msg}</p>
	</c:if>
	<table>
		<tr>
			<td>
				<form action="/base/DirectionServlet?action=record_copy" method="post">
					<input type="submit" value="複製" style="width: 90%; padding: 10px; font-size: 30px;"> <input type="hidden" name="page" value="individual/direction.jsp">
				</form>
			</td>
			<td>
				<form action="/base/DirectionServlet?action=record_update" method="post">
					<input type="submit" value="更新" style="width: 90%; padding: 10px; font-size: 30px;"> <input type="hidden" name="page" value="individual/direction.jsp">
				</form>
			</td>
			<td>
				<form action="/base/DirectionServlet?action=record_replace" method="post">
					<input type="submit" value="置換" style="width: 90%; padding: 10px; font-size: 30px;"> <input type="hidden" name="page" value="individual/direction.jsp">
				</form>
			</td>
			<td>
				<form action="/base/DirectionServlet?action=record_delete" method="post">
					<input type="submit" value="削除" style="width: 90%; padding: 10px; font-size: 30px;"> <input type="hidden" name="page" value="individual/direction.jsp">
				</form>
			</td>
			<td></td>
			<td></td>

			<td>
				<form action="/base/DirectionServlet?action=getCsv" method="post">
					<input type="submit" value="csvファイル出力" style="width: 90%; padding: 10px; font-size: 30px;"> <input type="hidden" name="page" value="individual/direction.jsp">
				</form>
			</td>
			<c:if test="${action eq 'getCsv'}">
				<td>
					<form name="download" action="/base/DirectionServlet?action=outputToCsv" method="post">
						<select name="orderBy">
							<option value=""></option>
							<c:forEach items="${decideColumnRange}" var="column">
								<option value="${column}">${column}</option>
							</c:forEach>
						</select> <input type="submit" value="csvダウンロード"> <input type="hidden" name="page" value="individual/direction.jsp">
					</form>
				</td>
			</c:if>
		</tr>
	</table>

	<form action="/base/FittingServlet?action=showTables" method="post">
		<p dir="rtl">
			<input type="hidden" name="table_name" value="${table_name}"> <input type="submit" value="テーブル一覧に戻る" style="text-align: right;"> <input type="hidden" name="page"
				value="individual/checkTableList.jsp"> <input type="hidden" name="selecting" value="selecting">
		</p>
	</form>

	<c:if test="${action eq 'select'}">
		<p>条件を絞って検索</p>
		<form action="/base/DirectionServlet?action=select" method="post">
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
			<input type="submit" style="color: #ffffff; background-color: #22ff79;" value="条件を指定して検索"> <input type="hidden" name="db_execute" value="${db_execute}"> <input type="hidden"
				name="page" value="individual/direction.jsp">
		</form>
	</c:if>
	<br />
	<c:if test="${table_name eq 'schedule_manuscript'}">
		<jsp:include page="/jsp/individual/checkTableList.jsp"></jsp:include>
	</c:if>
	<c:if test="${!empty record_update}">
	複数レコードを更新
	<form action="/base/DirectionServlet?action=updateScedule" method="post">
			<input type="text" style="width: 400px;" name="code" /> <input type="checkbox" name="allcode" value="allcode">全てのレコードを更新<br> <input type="text" pattern="^[1-9][0-9]*$"
				style="width: 16px;" name="hour" />:<input type="text" pattern="^[1-9][0-9]*$" style="width: 16px;" name="min" /> <input type="radio" name="calc" value="+" style="margin-left: 2px;" checked>加算<input
				type="radio" name="calc" style="margin-left: 2px;" value="-">減算 <input type="hidden" name="page" value="individual/direction.jsp"> <select name="start">
				<option value=""></option>
				<c:forEach items="${decideColumnRange}" var="column">
					<option value="${column}">${column}</option>
				</c:forEach>
			</select>～ <select name="end">
				<option value=""></option>
				<c:forEach items="${decideColumnRange}" var="column">
					<option value="${column}">${column}</option>
				</c:forEach>
			</select> <input type="submit" style="color: #ffffff; background-color: #ff6c22;" value="更新">
		</form>
	</c:if>
	<c:if test="${action eq 'record_replace'}">
		<form action="/base/DirectionServlet?action=replaceScedule" method="post">
			<p>
				<b>コピー元 </b> コード：<input type="text" name="source" /> 範囲：<select name="start">
					<option value=""></option>
					<c:forEach items="${decideColumnRange}" var="column">
						<option value="${column}">${column}</option>
					</c:forEach>
				</select>～ <select name="end">
					<option value=""></option>
					<c:forEach items="${decideColumnRange}" var="column">
						<option value="${column}">${column}</option>
					</c:forEach>
				</select>
			</p>

			<p>
				<b>貼り付け先</b> コード：<input type="text" name="target" /> 時刻の計算：<input type="text" pattern="^[1-9][0-9]*$" style="width: 16px;" name="hour" />:<input type="text" pattern="^[1-9][0-9]*$"
					style="width: 16px;" name="min" /> <input type="radio" name="calc" value="+" style="margin-left: 2px;" checked>加算<input type="radio" name="calc" style="margin-left: 2px;" value="-">減算
				<input type="submit" style="color: #ffffff; background-color: #ff6c22;" value="置換"> <input type="hidden" name="page" value="individual/direction.jsp">

			</p>


		</form>
	</c:if>

	<br />
	<div class="tableShuffle">
		<table border="1">
			<thead>
				<tr>
					<c:if test="${!empty record_copy}">
						<th class="tableaction" style="height: 44px;">複製レコードの選択</th>
					</c:if>
					<c:if test="${!empty record_update}">
						<th class="tableaction" style="height: 48px;">更新レコードの選択</th>
						<th class="tableaction" style="height: 20px;">開始範囲</th>
						<th class="tableaction" style="height: 20px;">終了範囲</th>
					</c:if>
					<c:if test="${action eq 'record_delete'}">
						<th class="tableaction" style="height: 25px;">削除レコードの選択</th>
					</c:if>
					<c:forEach items="${columns}" var="column">
						<th>${column.columnName}</th>
					</c:forEach>
				</tr>
			</thead>
			<tbody>

				<c:forEach items="${dbDataList}" var="record">
					<tr>
						<c:if test="${!empty record_copy}">
							<td class="tableaction" style="height: 44px;">

								<form action="/base/DirectionServlet?action=createTemporaryTable" method="post">
									<p style="margin-top: 00px; margin-bottom: 0px;">
										<input type="submit" style="color: #ffffff; background-color: #ff6c22; padding-left: 4px; padding-right: 4px;" value="複製"> <input type="text" pattern="^[1-9][0-9]*$"
											style="width: 16px;" name="hour" />:<input type="text" pattern="^[1-9][0-9]*$" style="width: 16px;" name="min" />
									</p>
									<p style="margin-top: 00px; margin-bottom: 0px;">
										<input type="radio" name="calc" value="+" style="margin-left: 2px;" checked>加算<input type="radio" name="calc" style="margin-left: 2px;" value="-">減算
									</p>

									<input type="hidden" name="code" value="${record.code}"> <input type="hidden" name="page" value="individual/direction.jsp">
								</form>
							</td>
						</c:if>
						<c:if test="${!empty record_update}">
							<td class="tableaction" style="height: 100px;">
								<form action="/base/DirectionServlet?action=updateScedule" method="post">
									<p style="margin-top: 00px; margin-bottom: 0px;">
										<input type="submit" style="color: #ffffff; background-color: #ff6c22; padding-left: 4px; padding-right: 4px;" value="更新"> <input type="text" pattern="^[1-9][0-9]*$"
											style="width: 16px;" name="hour" />:<input type="text" pattern="^[1-9][0-9]*$" style="width: 16px;" name="min" />
									</p>
									<p style="margin-top: 00px; margin-bottom: 0px;">
										<input type="radio" name="calc" value="+" style="margin-left: 2px;" checked>加算<input type="radio" name="calc" style="margin-left: 2px;" value="-">減算
									</p>
									<p style="margin-top: 00px; margin-bottom: 0px;">

										<input type="hidden" name="code" value="${record.code}"> <input type="hidden" name="page" value="individual/direction.jsp"> <select name="start" style="width: 102px;">
											<option value=""></option>
											<c:forEach items="${decideColumnRange}" var="column">
												<option value="${column}">${column}</option>
											</c:forEach>
										</select>
									</p>
									<p style="margin-top: 00px; margin-bottom: 0px;">
										<select name="end" style="width: 102px;">
											<option value=""></option>
											<c:forEach items="${decideColumnRange}" var="column">
												<option value="${column}">${column}</option>
											</c:forEach>
										</select>
									</p>
								</form>
							</td>
						</c:if>
						<c:if test="${action eq 'record_delete'}">
							<td class="tableaction" style="height: 25px;">
								<form action="/base/DirectionServlet?action=deleteRecord" method="post">
									<p style="margin-top: 00px; margin-bottom: 0px;">
										<input type="submit" style="color: #ffffff; background-color: #ff6c22; padding-left: 4px; padding-right: 4px;" value="削除">
									</p>
									<input type="hidden" name="code" value="${record.code}"> <input type="hidden" name="page" value="individual/direction.jsp">
								</form>
							</td>
						</c:if>
						<c:forEach items="${record}" var="element">
							<td class="text">${element.value}</td>
						</c:forEach>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>

</body>
</html>