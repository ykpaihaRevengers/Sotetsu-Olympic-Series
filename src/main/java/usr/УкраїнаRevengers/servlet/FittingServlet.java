package usr.УкраїнаRevengers.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dbmng.bean.CreateTable;
import dbmng.bean.CreateTable.ColumnTypes;
import dbmng.bean.Delete;
import dbmng.bean.ExecuteQuery;
import dbmng.bean.Insert;
import dbmng.servlet.DataBaseServlet;
import filemng.bean.FileList;
import home.dao.MainDAO;
import home.dao.MainDAO.CurrentDateTimeStamp;
import home.dao.MainDAO.Operator;
import home.dao.err.DAOException;
import home.servlet.MainServlet;
import home.tool.ScenarioUtil;
import usr.УкраїнаRevengers.bean.ManuscriptScedule;
import usr.УкраїнаRevengers.bean.ManuscriptScedule.DirectionBaseColumns;
import usr.УкраїнаRevengers.utility.NameJudging;
import usr.УкраїнаRevengers.utility.NameJudging.CodeHeader;
import usr.УкраїнаRevengers.utility.NameJudging.Direction;
import usr.УкраїнаRevengers.utility.NameJudging.FPAIPAColumnname;
import usr.УкраїнаRevengers.utility.NameJudging.Terminus;

/**
 * Servlet implementation class FittingServlet
 */
@WebServlet("/FittingServlet")
public class FittingServlet extends MainServlet {
	private static final long serialVersionUID = 1L;
	public static final String HEADER = "FPAIHA";
	public static final String ＿ = "_";
	public static final String DIRECTION = "direction";
	public static final String NAMELIST = "namelist";
	public static final String ARR = "着";
	public static final String DEP = "発";

	public static final String TABLE_NAME_DEPLOYED_SCHEDULES = "deployed_schedules";
	public static final String TABLE_NAME_SCHEDULE_MANUSCRIPT = "schedule_manuscript";
	public static final String TABLE_NAME_SCHEDULE_MANUSCRIPT_INSERTED = "schedule_manuscript_inserted";
	private static final String MODEL_DIR = "C:\\pleiades\\nioFile\\model";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FittingServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// 初期処理
		super.doGet(request, response);
		String tableName = getRequestParameter(request, "table_name");

		try (MainDAO dao = new MainDAO((String) session.getAttribute(DataBaseServlet.DATABASE_NAME))) {

			if (actionEquals(action, "manuscriptInsert")) {
				tableName = "schedule_manuscript";
				FileList fileList = (FileList) session.getAttribute("file_list");
				List<ManuscriptScedule> schedules = ScenarioUtil.mappingArrayList(fileList.getBookList(), book -> book.arrangeTextBook());

				Insert insert = new Insert(tableName, dao);
				schedules.stream().map(schedule -> schedule.createInsertValues()).flatMap(list -> list.stream()).forEachOrdered(insert::setInsertValue);
				String result = dao.insert(insert);
				request.setAttribute("execute_msg", "DBへの反映が" + result + "件反映されました。");
			}

			if (actionEquals(action, "showTables")) {
				request.setAttribute("table_name", "schedule_manuscript");
				request.setAttribute("table_names", dao.showTableList(HEADER.toLowerCase(), Operator.STARTSWITH));
				request.setAttribute("deployed_schedule", "deployed_schedules");
				ExecuteQuery select = new ExecuteQuery(TABLE_NAME_DEPLOYED_SCHEDULES, dao);
				select.setOrderByColumn("deployed_timestamp");
				request.setAttribute("deployed_schedule_table_name", "deployed_schedules");
				request.setAttribute("deployed_schedule_list", dao.select(select));
			}

			if (actionEquals(action, "createTableNameList")) {

				dao.truncateTable(TABLE_NAME_SCHEDULE_MANUSCRIPT);

				FileList modelFileList = new FileList(MODEL_DIR);
				//登録した編集方法でファイルを編集
				modelFileList.editFile(dao);

				Insert insert = new Insert(TABLE_NAME_SCHEDULE_MANUSCRIPT, dao);
				ScenarioUtil.mappingArrayList(modelFileList.getBookList(), book -> book.arrangeTextBook())
						.stream().map(schedule -> schedule.createInsertValues()).flatMap(list -> list.stream()).forEachOrdered(insert::setInsertValue);
				dao.insert(insert);

				for (CodeHeader header : CodeHeader.values()) {

					String nameListTableName = NAMELIST + ＿ + header.toString();

					//テーブルの削除
					dao.dropTable(nameListTableName);

					//テーブルの作成
					CreateTable nameListtable = new CreateTable(nameListTableName, "name");
					nameListtable.setColumnAttr("num", ColumnTypes.SERIAL, 0, true, "");
					nameListtable.setColumnAttr("name", ColumnTypes.VARCHAR, 40, true, "");
					String msgNameListtable = dao.createTable(nameListtable);

					ExecuteQuery select = new ExecuteQuery(TABLE_NAME_SCHEDULE_MANUSCRIPT, dao, Arrays.asList("name"));
					select.setFilter("code", Operator.STARTSWITH, header.toString());
					select.setOrderByColumn("num");
					//値の挿入
					List<String> nameList = new ArrayList<String>(
							new LinkedHashSet<>(ScenarioUtil.mappingArrayList(dao.select(select), record -> (String) record.get("name"))));
					Insert inserttoNameList = new Insert(nameListTableName, dao);
					nameList.stream().forEachOrdered(name -> inserttoNameList.setInsertValue(Arrays.asList(name)));
					String messageInsertNameList = dao.insert(inserttoNameList);
					request.setAttribute("execute_message", msgNameListtable + " " + messageInsertNameList);
				}

				//schedule_manuscriptの初期化
				dao.truncateTable(TABLE_NAME_SCHEDULE_MANUSCRIPT);

				//一元管理テーブルの初期化
				dao.truncateTable(TABLE_NAME_DEPLOYED_SCHEDULES);

				for (CodeHeader header : CodeHeader.values()) {
					List<String> nameList = new ArrayList<String>(
							new LinkedHashSet<>(ScenarioUtil.mappingArrayList(dao.select(new ExecuteQuery(NAMELIST + ＿ + header.toString(), dao)), record -> (String) record.get("name"))));
					Direction aORb = Direction.lA;
					if (!NameJudging.checkDirection(nameList)) {
						aORb = Direction.lB;
					}

					String outputTableName = HEADER + ＿ + header.toString() + ＿ + DIRECTION;

					//テーブルの削除
					dao.dropTable(outputTableName + aORb.getDirKey(aORb));
					//テーブルの作成
					CreateTable table = new CreateTable(outputTableName + aORb.getDirKey(aORb), "code");
					String msg = dao.createTable(inputArriveAndDeparture(nameList, table, header, aORb));

					//交換
					aORb = aORb.exchangeAB(aORb);
					Collections.reverse(nameList);

					//テーブルの削除
					dao.dropTable(outputTableName + aORb.getDirKey(aORb));
					//テーブルの作成
					CreateTable tableReverse = new CreateTable(outputTableName + aORb.getDirKey(aORb), "code");
					String msgBack = dao.createTable(inputArriveAndDeparture(nameList, tableReverse, header, aORb));

					request.setAttribute("execute_message", msg + " " + msgBack);

					//既存データのデプロイ
					ExecuteQuery select = new ExecuteQuery(TABLE_NAME_SCHEDULE_MANUSCRIPT_INSERTED, dao);
					select.setFilter("code", Operator.STARTSWITH, header.toString());
					System.out.println(dao.select(select));
					List<String> deployCodes = ScenarioUtil.mappingArrayList(dao.select(select), map -> (String) map.get("code")).stream().distinct().collect(Collectors.toList());
					String message = deploy(deployCodes, TABLE_NAME_SCHEDULE_MANUSCRIPT_INSERTED, dao);
					request.setAttribute("execute_message", message);
				}

				//schedule_manuscriptの初期化
				dao.truncateTable(TABLE_NAME_SCHEDULE_MANUSCRIPT);

			}

			if (actionEquals(action, "deploy")) {

				//デプロイ実行
				ExecuteQuery select = new ExecuteQuery(tableName, dao);
				List<String> deployCodes = ScenarioUtil.mappingArrayList(dao.select(select), map -> (String) map.get("code")).stream().distinct().collect(Collectors.toList());
				String message = deploy(deployCodes, tableName, dao);

				//schedule_manuscript_inserted への代入
				List<Map<String, Object>> schedules = dao.select(select);
				Insert insert = new Insert("schedule_manuscript_inserted", dao);
				for (Map<String, Object> schedule : schedules) {
					List<String> insertValue = new ArrayList<>();
					insertValue.add((String) schedule.get("code"));
					insertValue.add((String) schedule.get("name"));
					insertValue.add((String) schedule.get("arr"));
					insertValue.add((String) schedule.get("dep"));
					insertValue.add(CurrentDateTimeStamp.CURRENT_TIMESTAMP.toString());
					insert.setInsertValue(insertValue);
				}
				String insertMsg = dao.insert(insert);
				try {
					Integer.parseInt(insertMsg);
				} catch (Exception e) {
					message = insertMsg;
				}

				request.setAttribute("execute_message", message);

				//schedule_manuscriptの初期化
				dao.truncateTable(TABLE_NAME_SCHEDULE_MANUSCRIPT);

			}
			if (actionEquals(action, "selectCode")) {
				String deployCode = getRequestParameter(request, "deployed_code");
				System.out.println(deployCode);
				ExecuteQuery selectCode = new ExecuteQuery(TABLE_NAME_SCHEDULE_MANUSCRIPT_INSERTED, dao);
				selectCode.setFilter("code", Operator.EQUALS, deployCode);
				selectCode.setOrderByColumn("dep");
				List<Map<String, Object>> selectedData = dao.select(selectCode);
				request.setAttribute("selectedData", selectedData);

				ExecuteQuery ikisakiCode = new ExecuteQuery(TABLE_NAME_DEPLOYED_SCHEDULES, dao);
				ikisakiCode.setFilter("deployed_code", Operator.EQUALS, deployCode);
				Map<String, Object> selectedIkisaki = dao.select(ikisakiCode).get(0);
				request.setAttribute("deployed_code", deployCode);
				request.setAttribute("shubetsu", selectedIkisaki.get("shubetsu"));
				request.setAttribute("ikisaki", selectedIkisaki.get("ikisaki"));

			}

			if (actionEquals(action, "deleteCode")) {
				String deployedCode = getRequestParameter(request, "deployed_code");

				for (CodeHeader header : CodeHeader.values()) {
					//レコードの削除
					Delete delete = new Delete(HEADER + ＿ + header.toString() + ＿ + DIRECTION + deployedCode.substring(deployedCode.length() - 1), dao);
					delete.setFilters("code", Operator.EQUALS, deployedCode);
					dao.delete(delete);
				}

				Delete deleteDeployedSchedules = new Delete(TABLE_NAME_DEPLOYED_SCHEDULES, dao);
				deleteDeployedSchedules.setFilters("deployed_code", Operator.EQUALS, deployedCode);
				dao.delete(deleteDeployedSchedules);

				Delete deleteManuscriptInserted = new Delete(TABLE_NAME_SCHEDULE_MANUSCRIPT_INSERTED, dao);
				deleteManuscriptInserted.setFilters("code", Operator.EQUALS, deployedCode);
				dao.delete(deleteManuscriptInserted);

				request.setAttribute("execute_message", "コード番号：" + deployedCode + "を削除しました。");
			}

		} catch (DAOException e) {
			e.printStackTrace();
		}
		//ページへの転送
		gotoPage(request, response, page);

	}

	private CreateTable inputArriveAndDeparture(List<String> nameList, CreateTable table, CodeHeader header, Direction aORb) {

		table.setColumnAttr(FPAIPAColumnname.code.toString(), ColumnTypes.VARCHAR, 10, true, "");
		table.setColumnAttr(FPAIPAColumnname.shubetsu.toString(), ColumnTypes.VARCHAR, 20, false, "");
		table.setColumnAttr(FPAIPAColumnname.ikisaki.toString(), ColumnTypes.VARCHAR, 30, false, "");

		for (String name : nameList) {
			if (NameJudging.judgeEnding(name, header, aORb).equals(Terminus.isTerminusA)) {
				if (aORb.toString().contains("A")) {
					//Aの場合
					table.setColumnAttr(name + ＿ + ARR, ColumnTypes.TIME, 30, false, "");
				} else {
					//Bの場合
					table.setColumnAttr(name + ＿ + DEP, ColumnTypes.TIME, 30, false, "");
				}
			} else if (NameJudging.judgeEnding(name, header, aORb).equals(Terminus.isTerminusB)) {
				if (aORb.toString().contains("A")) {
					//Aの場合
					table.setColumnAttr(name + ＿ + DEP, ColumnTypes.TIME, 30, false, "");
				} else {
					//Bの場合
					table.setColumnAttr(name + ＿ + ARR, ColumnTypes.TIME, 30, false, "");
				}
			} else {
				table.setColumnAttr(name + ＿ + ARR, ColumnTypes.TIME, 30, false, "");
				table.setColumnAttr(name + ＿ + DEP, ColumnTypes.TIME, 30, false, "");
			}
		}

		return table;
	}

	private String deploy(List<String> deployCodes, String tableName, MainDAO dao) throws DAOException {

		String place = "";
		List<String> executeMessage = new ArrayList<>();

		for (String deployCode : deployCodes) {

			List<CodeHeader> codeHeaders = NameJudging.judgeAttribute(deployCode);
			ExecuteQuery selectInsertedData = new ExecuteQuery(TABLE_NAME_SCHEDULE_MANUSCRIPT_INSERTED, dao);
			ScenarioUtil.mappingArrayList(dao.select(selectInsertedData), map -> (String) map.get("code"));
			if (!ScenarioUtil.mappingArrayList(dao.select(selectInsertedData), map -> (String) map.get("code")).contains(deployCode) || !tableName.equals(TABLE_NAME_SCHEDULE_MANUSCRIPT)) {

				ExecuteQuery deployCodeSelect = new ExecuteQuery(tableName, dao, Arrays.asList("code", "name", "arr", "dep"));
				deployCodeSelect.setFilter("code", Operator.EQUALS, deployCode);
				List<Map<String, Object>> schedules = dao.select(deployCodeSelect);
				String shubetsu = "";
				String orthodoxShubetsu = "";
				String ikisaki = (String) schedules.get(schedules.size() - 1).get("name");

				// ヘッダーごとの処理
				for (CodeHeader codeHeader : codeHeaders) {
					String namelistTableName = (NAMELIST + ＿ + codeHeader.toString()).toLowerCase();

					List<String> nameList = ScenarioUtil.mappingArrayList(dao.select(new ExecuteQuery(namelistTableName, dao, Arrays.asList("name"))), map -> (String) map.get("name"));
					String insertTableName = (HEADER + ＿ + codeHeader.toString() + ＿ + "direction").toLowerCase();
					if (deployCode.endsWith("A")) {
						insertTableName = insertTableName + "a";
					} else if (deployCode.endsWith("B")) {
						insertTableName = insertTableName + "b";
					}
					List<Map<String, Object>> filteredSchedules = ScenarioUtil.filteringArrayList(schedules,
							map -> NameJudging.judgeSchedule(codeHeader, deployCode, nameList, (String) map.get("name")));

					List<String> insertFilter = DirectionBaseColumns.getColumnNames();
					for (Map<String, Object> schedule : filteredSchedules) {

						if (ScenarioUtil.checkObjectValue(schedule.get("arr"))) {
							insertFilter.add((String) schedule.get("name") + ＿ + ARR);
						}
						if (ScenarioUtil.checkObjectValue(schedule.get("dep"))) {
							insertFilter.add((String) schedule.get("name") + ＿ + DEP);
						}
					}
					Insert insert = new Insert(insertTableName, dao, insertFilter);

					List<String> insertValues = new ArrayList<>();
					insertValues.add(deployCode);//codeの値挿入
					shubetsu = NameJudging.judgeShubetsu(codeHeader, deployCode, ScenarioUtil.mappingArrayList(filteredSchedules, map -> (String) map.get("name"))).toString();
					if (codeHeader.toString().equals(deployCode.substring(0, 2))) {
						orthodoxShubetsu = shubetsu;
					}
					insertValues.add(shubetsu);//shubetsuの値挿入
					insertValues.add(ikisaki);//ikisakiの値挿入

					filteredSchedules.stream().forEachOrdered((schedule) -> {
						String arrTime = (String) schedule.get("arr");
						if (ScenarioUtil.checkStringValue(arrTime)) {
							insertValues.add(arrTime);
						}
						String depTime = (String) schedule.get("dep");
						if (ScenarioUtil.checkStringValue(depTime)) {
							insertValues.add(depTime);
						}
					});

					insert.setInsertValue(insertValues);
					dao.insert(insert);
				}

				//deployed_schedules への代入
				Insert insert = new Insert(TABLE_NAME_DEPLOYED_SCHEDULES, dao);
				List<String> insertValue = new ArrayList<>();
				insertValue.add(deployCode);
				insertValue.add(orthodoxShubetsu);
				insertValue.add(ikisaki);
				insertValue.add(CurrentDateTimeStamp.CURRENT_TIMESTAMP.toString());
				insert.setInsertValue(insertValue);
				executeMessage.add(dao.insert(insert));
				place = deployCode;
			} else {
				System.out.println(ScenarioUtil.mappingArrayList(dao.select(selectInsertedData), map -> (String) map.get("code")));
				System.out.println("deployCode :" + deployCode);
				System.out.println("tableName :" + tableName);
				System.out.println(!ScenarioUtil.mappingArrayList(dao.select(selectInsertedData), map -> (String) map.get("code")).contains(deployCode));
				System.out.println(!tableName.equals(TABLE_NAME_SCHEDULE_MANUSCRIPT));

			}

		}

		try {
			if (executeMessage.isEmpty()) {
				return "deploy対象はありませんでした。";
			}
			executeMessage.stream().forEachOrdered(msg -> Integer.parseInt(msg));
			return "deployが" + executeMessage.size() + "件完了しました。";
		} catch (Exception e) {
			System.out.println(e.getClass());
			return "deployで" + place + "に不備が見つかりました。";
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
