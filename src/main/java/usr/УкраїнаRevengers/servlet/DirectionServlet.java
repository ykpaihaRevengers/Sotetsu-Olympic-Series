package usr.УкраїнаRevengers.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dbmng.bean.CreateTemporaryTable;
import dbmng.bean.Delete;
import dbmng.bean.ExecuteQuery;
import dbmng.bean.Update;
import dbmng.servlet.DataBaseServlet;
import home.dao.MainDAO;
import home.dao.MainDAO.Operator;
import home.dao.err.DAOException;
import home.servlet.MainServlet;
import home.tool.FileUtils;
import home.tool.ScenarioUtil;
import usr.УкраїнаRevengers.utility.NameJudging.FPAIPAColumnname;

/**
 * Servlet implementation class DirectionServlet
 */
@WebServlet("/DirectionServlet")
public class DirectionServlet extends MainServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DirectionServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// 初期処理
		super.doGet(request, response);
		String tableName = getRequestParameter(request, "table_name");

		try (MainDAO dao = new MainDAO((String) session.getAttribute(DataBaseServlet.DATABASE_NAME))) {

			if (actionEquals(action, "record_copy")) {
				request.setAttribute("record_copy", "record_copy");
			}
			if (actionEquals(action, "createTemporaryTable")) {

				String code = getRequestParameter(request, "code");
				String hashcode = Integer.toString(code.hashCode()).substring(Integer.toString(code.hashCode()).length() - 5);
				String newCode = code.substring(0, code.length() - 7) + hashcode + code.substring(code.length() - 2);
				CreateTemporaryTable temporaryTable = new CreateTemporaryTable(tableName, dao, "code", getRequestParameter(request, "code"), newCode);
				String executeMessage = dao.createTemporaryTable(temporaryTable);
				if (executeMessage.equals("SUCCESS")) {

					int hour = 0;
					int minute = 0;
					if (checkRequestParameter(request, "hour")) {
						hour = Integer.parseInt(getRequestParameter(request, "hour"));
					}
					if (checkRequestParameter(request, "min")) {
						minute = Integer.parseInt(getRequestParameter(request, "min"));
						if (minute >= 60) {
							minute = minute % 60;
							hour = hour + minute / 60;
						}
					}

					String valueOfTime = Integer.toString(hour) + ":" + Integer.toString(minute);
					executeMessage = updateschedule(tableName, dao, getRequestParameter(request, "calc"), valueOfTime, newCode, "", "", true);

				}
				if (executeMessage.equals("SUCCESS")) {
					request.setAttribute("execute_msg", "レコードの複製が完了しました。");
				} else {
					request.setAttribute("execute_msg", executeMessage);
				}
			}
			if (actionEquals(action, "record_update")) {
				request.setAttribute("record_update", "record_update");
				request.setAttribute("decideColumnRange", FPAIPAColumnname.getScheduleColumnName(dao.showColumns(tableName)));
			}

			if (actionEquals(action, "record_replace")) {
				request.setAttribute("decideColumnRange", FPAIPAColumnname.getScheduleColumnName(dao.showColumns(tableName)));
			}

			if (actionEquals(action, "updateScedule")) {
				String startColumn = getRequestParameter(request, "start");
				System.out.println(startColumn);
				String endColumn = getRequestParameter(request, "end");
				System.out.println(endColumn + "End");

				int hour = 0;
				int minute = 0;
				if (checkRequestParameter(request, "hour")) {
					hour = Integer.parseInt(getRequestParameter(request, "hour"));
				}
				if (checkRequestParameter(request, "min")) {
					minute = Integer.parseInt(getRequestParameter(request, "min"));
					if (minute >= 60) {
						minute = minute % 60;
						hour = hour + minute / 60;
					}
				}
				String valueOfTime = Integer.toString(hour) + ":" + Integer.toString(minute);
				String executeMessage = updateschedule(tableName, dao, getRequestParameter(request, "calc"), valueOfTime, getRequestParameter(request, "code"), startColumn, endColumn,
						checkRequestParameter(request, "allcode"));

				if (executeMessage.equals("SUCCESS")) {
					request.setAttribute("execute_msg", "レコードの更新が完了しました。");
				} else {
					request.setAttribute("execute_msg", executeMessage);
				}

			}

			if (actionEquals(action, "replaceScedule")) {
				String sourceCode = getRequestParameter(request, "source");
				String targetCode = getRequestParameter(request, "target");
				if (ScenarioUtil.checkStringValue(sourceCode, targetCode)) {
					String startColumn = getRequestParameter(request, "start");
					System.out.println(startColumn);
					String endColumn = getRequestParameter(request, "end");
					System.out.println(endColumn + "End");
					List<String> sourceSchedule = FPAIPAColumnname.getScheduleColumnName(dao.showColumns(tableName));

					if (ScenarioUtil.checkStringValue(startColumn, endColumn)) {
						sourceSchedule = rangeSchedule(sourceSchedule, startColumn, endColumn);
					}

					ExecuteQuery selectSource = new ExecuteQuery(tableName, dao, sourceSchedule);
					selectSource.setFilter(FPAIPAColumnname.code.toString(), Operator.EQUALS, sourceCode);
					Map<String, Object> sourceData = dao.select(selectSource).get(0);
					Update updateTarget = new Update(tableName, dao);
					sourceData.entrySet().stream().forEachOrdered(map -> updateTarget.setSetting(map.getKey(), Operator.EQUALS, map.getValue()));

					updateTarget.setFilters(FPAIPAColumnname.code.toString(), Operator.EQUALS, targetCode);
					String executeMessage1 = dao.update(updateTarget);
					String executeMessage2 = "";
					if (checkRequestParameter(request, "hour") || checkRequestParameter(request, "min")) {
						int hour = 0;
						int minute = 0;
						if (checkRequestParameter(request, "hour")) {
							hour = Integer.parseInt(getRequestParameter(request, "hour"));
						}
						if (checkRequestParameter(request, "min")) {
							minute = Integer.parseInt(getRequestParameter(request, "min"));
							if (minute >= 60) {
								minute = minute % 60;
								hour = hour + minute / 60;
							}
						}
						String valueOfTime = Integer.toString(hour) + ":" + Integer.toString(minute);
						Update updateByCalc = new Update(tableName, dao);
						sourceData.entrySet().stream().forEachOrdered(map -> updateByCalc.setSetting(map.getKey(), getRequestParameter(request, "calc"), valueOfTime));
						updateByCalc.setFilters(FPAIPAColumnname.code.toString(), Operator.EQUALS, targetCode);
						executeMessage2 = dao.update(updateByCalc);
					}

					//メッセージの出力
					if (executeMessage2.equals("SUCCESS")) {
						request.setAttribute("execute_msg", "レコードの置換が完了しました。");

					} else if (executeMessage1.equals("SUCCESS")) {
						request.setAttribute("execute_msg", executeMessage2);

					} else {
						request.setAttribute("execute_msg", executeMessage1);
					}

				}

			}
			if (actionEquals(action, "deleteRecord")) {
				if (checkRequestParameter(request, "code")) {
					Delete delete = new Delete(tableName, dao);
					delete.setFilters(FPAIPAColumnname.code.toString(), Operator.EQUALS, getRequestParameter(request, "code"));
					String executeMessage = dao.delete(delete);

					try {
						Integer.parseInt(executeMessage);
						request.setAttribute("execute_msg", "レコードの削除が完了しました。");
					} catch (Exception e) {
						request.setAttribute("execute_msg", executeMessage);
					}

				}

			}
			if (actionEquals(action, "getCsv")) {
				request.setAttribute("decideColumnRange", FPAIPAColumnname.getScheduleColumnName(dao.showColumns(tableName)));
			}

			session.setAttribute("dbDataList", dao.select(new ExecuteQuery(tableName, dao)));

			if (actionEquals(action, "outputToCsv")) {
				String csvFileName = tableName + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";

				//文字コードと出力するCSVファイル名を設定
				response.setHeader("Content-Type", "text/csv; charset=UTF-8");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + csvFileName + "\"");

				//try-with-resources文を使うことでclose処理を自動化
				try (PrintWriter pw = response.getWriter()) {
					System.out.println("Servlet.service pw");

					//文字化け防止のため、BOM付きのファイルとして設定
					pw.write(0xFEFF);

					ExecuteQuery select = new ExecuteQuery(tableName, dao);
					if (checkRequestParameter(request, "orderBy")) {
						select.setOrderByColumn(getRequestParameter(request, tableName));
					}
					List<Map<String, Object>> selectedData = dao.select(select);
					if (ScenarioUtil.checkList(selectedData)) {

						for (String outPutColumn : selectedData.get(0).keySet()) {
							List<String> outAppending = new ArrayList<>();
							outAppending.add(outPutColumn);
							for (Map<String, Object> record : selectedData) {
								Object element = record.get(outPutColumn);
								String value = null;
								if (element instanceof Integer) {
									value = Integer.toString((Integer) element);
								} else if (element != null) {
									value = element.toString();
								}
								outAppending.add(value);
							}
							System.out.println("Servlet.service for");
							pw.print(String.join(",", outAppending) + FileUtils.CRLF);
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		} catch (DAOException e) {
			e.printStackTrace();
		}
		//ページへの転送
		gotoPage(request, response, page);
	}

	private String updateschedule(String tableName, MainDAO dao, String calc, String valueOfTime, String codeofUpdating, String startColumn, String endColumn, boolean isAllcode) throws DAOException {
		Update update = new Update(tableName, dao);
		List<String> updateColumns = FPAIPAColumnname.getScheduleColumnName(update.getColumns());

		if (ScenarioUtil.checkStringValue(startColumn, endColumn)) {
			updateColumns = rangeSchedule(updateColumns, startColumn, endColumn);
		}

		for (String updateColumn : updateColumns) {
			update.setSetting(updateColumn, calc, valueOfTime);
		}

		if (!isAllcode) {
			if (codeofUpdating.contains(",")) {
				update.setFilters(FPAIPAColumnname.code.toString(), ScenarioUtil.split(codeofUpdating, ","));
			} else {
				update.setFilters(FPAIPAColumnname.code.toString(), Operator.EQUALS, codeofUpdating);
			}
		}

		return dao.update(update);
	}

	private List<String> rangeSchedule(List<String> columnList, String startColumn, String endColumn) {
		List<String> filteredColumns = new ArrayList<>();
		boolean isRange = false;
		for (String columnName : columnList) {

			if (columnName.equals(startColumn)) {
				isRange = true;
			}
			if (isRange) {
				filteredColumns.add(columnName);
			}
			if (columnName.equals(endColumn)) {
				isRange = false;
			}
		}
		System.out.println("columnList" + columnList);
		System.out.println("filteredColumns" + filteredColumns);

		return filteredColumns;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
