package usr.УкраїнаRevengers.servlet;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
import usr.УкраїнаRevengers.bean.SceduleComparing;
import usr.УкраїнаRevengers.utility.NameJudging;
import usr.УкраїнаRevengers.utility.NameJudging.CodeHeader;
import usr.УкраїнаRevengers.utility.NameJudging.Direction;
import usr.УкраїнаRevengers.utility.NameJudging.FPAIPAColumnname;
import usr.УкраїнаRevengers.utility.NameJudging.Shubetsu;
import usr.УкраїнаRevengers.utility.NameJudging.Terminus;
import usr.УкраїнаRevengers.utility.NumberingNameList;

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
				tableName = TABLE_NAME_SCHEDULE_MANUSCRIPT;
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

				FileList modelFileList = new FileList(MODEL_DIR);
				//登録した編集方法でファイルを編集
				modelFileList.editFile(dao);

				//schedule_manuscriptの初期化
				dao.truncateTable(TABLE_NAME_SCHEDULE_MANUSCRIPT);

				//一元管理テーブルの初期化
				dao.truncateTable(TABLE_NAME_DEPLOYED_SCHEDULES);

				//テーブルの初期化
				for (CodeHeader header : CodeHeader.values()) {

					String nameListTableName = NAMELIST + ＿ + header.toString();

					List<ManuscriptScedule> manuscriptSchedulesbyCode = ScenarioUtil.filteringArrayList(ScenarioUtil.mappingArrayList(modelFileList.getBookList(), book -> book.arrangeTextBook()),
							manuscriptSchedule -> manuscriptSchedule.getCode().startsWith(header.toString()));

					List<String> nameListofSchedule = new ArrayList<>();
					for (List<String> nameArray : ScenarioUtil.mappingArrayList(manuscriptSchedulesbyCode,
							schedule -> ScenarioUtil.mappingArrayList(schedule.getScheduleTags(), scheduleTag -> scheduleTag.getName()))) {
						nameArray.stream().map(name -> name.trim()).forEachOrdered(nameListofSchedule::add);
					}
					nameListofSchedule = ScenarioUtil.distinctArrayList(nameListofSchedule);
					System.out.println(nameListofSchedule);

					/*namelistシリーズ*/
					//テーブルの削除
					dao.dropTable(nameListTableName);

					//テーブルの作成
					CreateTable nameListtable = new CreateTable(nameListTableName, "name");
					nameListtable.setColumnAttr("num", ColumnTypes.SERIAL, 0, true, "");
					nameListtable.setColumnAttr("name", ColumnTypes.VARCHAR, 40, true, "");
					String msgNameListtable = dao.createTable(nameListtable);

					//値の挿入
					Insert inserttoNameList = new Insert(nameListTableName, dao);
					nameListofSchedule.stream().forEachOrdered(name -> inserttoNameList.setInsertValue(Arrays.asList(name)));
					String messageInsertNameList = dao.insert(inserttoNameList);

					/*direction関係*/

					List<String> nameList = new ArrayList<String>(
							new LinkedHashSet<>(ScenarioUtil.mappingArrayList(dao.select(new ExecuteQuery(NAMELIST + ＿ + header.toString(), dao)), record -> (String) record.get("name"))));
					Direction aORb = Direction.A;
					if (!NameJudging.checkDirection(nameList)) {
						aORb = Direction.B;
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

					request.setAttribute("execute_message", msgNameListtable + " " + messageInsertNameList + " " + msg + " " + msgBack);
				}

				//schedule_manuscriptの初期化
				dao.truncateTable(TABLE_NAME_SCHEDULE_MANUSCRIPT);

				for (CodeHeader header : CodeHeader.values()) {
					//既存データのデプロイ
					ExecuteQuery select = new ExecuteQuery(TABLE_NAME_SCHEDULE_MANUSCRIPT_INSERTED, dao);
					select.setFilter("code", Operator.STARTSWITH, header.toString());
					List<String> deployCodes = ScenarioUtil.mappingArrayList(dao.select(select), map -> (String) map.get("code")).stream().distinct().collect(Collectors.toList());
					String message = deploy(deployCodes, TABLE_NAME_SCHEDULE_MANUSCRIPT_INSERTED, dao);
					request.setAttribute("execute_message", message);
				}

			}

			if (actionEquals(action, "deploy")) {

				//デプロイ実行
				ExecuteQuery select = new ExecuteQuery(tableName, dao);
				List<String> deployCodes = ScenarioUtil.mappingArrayList(dao.select(select), map -> (String) map.get("code")).stream().distinct().collect(Collectors.toList());
				String message = deploy(deployCodes, tableName, dao);

				//schedule_manuscript_inserted への代入
				List<Map<String, Object>> schedules = dao.select(select);
				Insert insert = new Insert(TABLE_NAME_SCHEDULE_MANUSCRIPT_INSERTED, dao);
				int num = 1;
				String compareCode = "";
				for (Map<String, Object> schedule : schedules) {
					String code = (String) schedule.get("code");
					List<String> insertValue = new ArrayList<>();
					if (!compareCode.equals(code)) {
						num = 1;
					}
					insertValue.add(Integer.toString(num++));
					insertValue.add(code);
					insertValue.add((String) schedule.get("name"));
					insertValue.add((String) schedule.get("arr"));
					insertValue.add((String) schedule.get("dep"));
					insertValue.add(CurrentDateTimeStamp.CURRENT_TIMESTAMP.toString());
					insert.setInsertValue(insertValue);
					compareCode = code;
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
				ExecuteQuery ikisakiCode = new ExecuteQuery(TABLE_NAME_DEPLOYED_SCHEDULES, dao);
				ikisakiCode.setFilter("deployed_code", Operator.EQUALS, deployCode);
				Map<String, Object> selectedIkisaki = dao.select(ikisakiCode).get(0);
				ExecuteQuery selectCode = new ExecuteQuery(TABLE_NAME_SCHEDULE_MANUSCRIPT_INSERTED, dao);
				selectCode.setFilter("code", Operator.EQUALS, deployCode);
				selectCode.setOrderByColumn("dep");
				List<Map<String, Object>> selectedData = dao.select(selectCode);

				ManuscriptScedule deployedSchedule = new ManuscriptScedule(selectedIkisaki, selectedData);
				request.setAttribute("deployedSchedule", deployedSchedule);
				request.setAttribute("selectedData", selectedData);
				request.setAttribute("deployed_code", deployCode);
				request.setAttribute("shubetsu", selectedIkisaki.get("shubetsu"));
				request.setAttribute("ikisaki", selectedIkisaki.get("ikisaki"));

			}

			if (actionEquals(action, "deleteCode")) {
				String deployedCode = getRequestParameter(request, "deployed_code");

				if (getRequestParameter(request, "filterColumn").equals("code")) {
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
				} else if (getRequestParameter(request, "filterColumn").equals("deployed_timestamp")) {

					String deployedTimeYYMMDDHH24MI = getRequestParameter(request, "deployed_time");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					Date from = sdf.parse(deployedTimeYYMMDDHH24MI);
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(sdf.parse(deployedTimeYYMMDDHH24MI));
					calendar.add(Calendar.SECOND, 75);
					Date to = calendar.getTime();
					deployedTimeYYMMDDHH24MI = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(from);
					String deployedTimeYYMMDDHH24MIextent = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(to);
					ExecuteQuery selectSchedules = new ExecuteQuery(TABLE_NAME_DEPLOYED_SCHEDULES, dao, Arrays.asList("deployed_code"));
					selectSchedules.setFilter("deployed_timestamp", Operator.gE, deployedTimeYYMMDDHH24MI);
					selectSchedules.setFilter("deployed_timestamp", Operator.lT, deployedTimeYYMMDDHH24MIextent);

					List<String> deployCodes = ScenarioUtil.mappingArrayList(dao.select(selectSchedules), map -> (String) map.get("deployed_code"));

					int deleteCount = 0;
					for (String code : deployCodes) {
						for (CodeHeader header : CodeHeader.values()) {
							//レコードの削除
							Delete delete = new Delete(HEADER + ＿ + header.toString() + ＿ + DIRECTION + code.substring(code.length() - 1), dao);
							delete.setFilters("code", Operator.EQUALS, code);
							dao.delete(delete);
						}

						Delete deleteDeployedSchedules = new Delete(TABLE_NAME_DEPLOYED_SCHEDULES, dao);
						deleteDeployedSchedules.setFilters("deployed_code", Operator.EQUALS, code);
						dao.delete(deleteDeployedSchedules);

						Delete deleteManuscriptInserted = new Delete(TABLE_NAME_SCHEDULE_MANUSCRIPT_INSERTED, dao);
						deleteManuscriptInserted.setFilters("code", Operator.EQUALS, code);
						deleteCount += Integer.parseInt(dao.delete(deleteManuscriptInserted));
					}
					request.setAttribute("execute_message", "以下のコードが無事にundeployされました。" + deployCodes + " undeploy件数：" + deleteCount + "件");
				}
			}

			if (actionEquals(action, "compare")) {
				String origindeployedCode = getRequestParameter(request, "origin_code");
				String editdeployedCode = getRequestParameter(request, "edit_code");
				System.out.println(origindeployedCode + " " + editdeployedCode);
				ExecuteQuery selectoriginmanuscript = new ExecuteQuery(TABLE_NAME_SCHEDULE_MANUSCRIPT_INSERTED, dao);
				selectoriginmanuscript.setFilter("code", Operator.EQUALS, origindeployedCode);
				ExecuteQuery selectoriginschedules = new ExecuteQuery(TABLE_NAME_DEPLOYED_SCHEDULES, dao);
				selectoriginschedules.setFilter("deployed_code", Operator.EQUALS, origindeployedCode);
				ManuscriptScedule originSchedule = new ManuscriptScedule(dao.select(selectoriginschedules).get(0), dao.select(selectoriginmanuscript));

				ExecuteQuery selecteditmanuscript = new ExecuteQuery(TABLE_NAME_SCHEDULE_MANUSCRIPT_INSERTED, dao);
				selecteditmanuscript.setFilter("code", Operator.EQUALS, editdeployedCode);
				ExecuteQuery selecteditschedules = new ExecuteQuery(TABLE_NAME_DEPLOYED_SCHEDULES, dao);
				selecteditschedules.setFilter("deployed_code", Operator.EQUALS, editdeployedCode);
				ManuscriptScedule editSchedule = new ManuscriptScedule(dao.select(selecteditschedules).get(0), dao.select(selecteditmanuscript));

				SceduleComparing comparing = new SceduleComparing(originSchedule, editSchedule);
				session.setAttribute("genpon_schedule", originSchedule);
				session.setAttribute("old_schedule", editSchedule);
				session.setAttribute("new_schedule", comparing.getNewSchedule());

			}
		} catch (DAOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO 自動生成された catch ブロック
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

			ExecuteQuery selectInsertedData = new ExecuteQuery(TABLE_NAME_SCHEDULE_MANUSCRIPT_INSERTED, dao);
			ScenarioUtil.mappingArrayList(dao.select(selectInsertedData), map -> (String) map.get("code"));
			if (!ScenarioUtil.mappingArrayList(dao.select(selectInsertedData), map -> (String) map.get("code")).contains(deployCode) || !tableName.equals(TABLE_NAME_SCHEDULE_MANUSCRIPT)) {

				ExecuteQuery deployCodeSelect = new ExecuteQuery(tableName, dao, Arrays.asList("code", "name", "arr", "dep"));
				deployCodeSelect.setFilter("code", Operator.EQUALS, deployCode);
				List<Map<String, Object>> schedules = dao.select(deployCodeSelect);
				List<String> scheduleNames = ScenarioUtil.mappingArrayList(schedules, schedule -> (String) schedule.get("name"));
				String shubetsu = "";
				String orthodoxShubetsu = "";
				String ikisaki = (String) schedules.get(schedules.size() - 1).get("name");
				int ischangeLimitedExpress_in_SH01 = 1;

				List<CodeHeader> codeHeaders = NameJudging.judgeAttribute(deployCode, scheduleNames);

				// ヘッダーごとの処理
				for (CodeHeader codeHeader : codeHeaders) {
					String namelistTableName = (NAMELIST + ＿ + codeHeader.toString()).toLowerCase();

					List<String> nameList = ScenarioUtil.mappingArrayList(dao.select(new ExecuteQuery(namelistTableName, dao, Arrays.asList("name"))), map -> (String) map.get("name"));
					String insertTableName = (HEADER + ＿ + codeHeader.toString() + ＿ + DIRECTION).toLowerCase();
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
					if ((codeHeader.equals(CodeHeader.TY) || codeHeader.equals(CodeHeader.MG)) && scheduleNames.contains(NumberingNameList.SH01) && shubetsu.equals(Shubetsu.急行.toString())) {
						ischangeLimitedExpress_in_SH01++;
					}
					if (codeHeader.equals(CodeHeader.SO) && scheduleNames.contains(NumberingNameList.SH01) && shubetsu.equals(Shubetsu.特急.toString())) {
						ischangeLimitedExpress_in_SH01++;
					}
					if (ischangeLimitedExpress_in_SH01 >= 3) {
						orthodoxShubetsu = Shubetsu.相特急行.toString();
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
			}

		}

		try {
			if (executeMessage.isEmpty()) {
				return "deploy対象はありませんでした。";
			}
			executeMessage.stream().forEachOrdered(msg -> Integer.parseInt(msg));
			return "deployが" + executeMessage.size() + "件完了しました。";
		} catch (Exception e) {
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
