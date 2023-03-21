package usr.УкраїнаRevengers.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dbmng.bean.CreateTable;
import dbmng.bean.CreateTable.ColumnTypes;
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
				tableName = "schedule_manuscript";
				for (CodeHeader header : CodeHeader.values()) {

					String nameListTableName = HEADER + ＿ + header.toString() + ＿ + NAMELIST;

					//テーブルの削除
					dao.dropTable(nameListTableName);

					//テーブルの作成
					CreateTable nameListtable = new CreateTable(nameListTableName, "name");
					nameListtable.setColumnAttr("num", ColumnTypes.SERIAL, 0, true, "");
					nameListtable.setColumnAttr("name", ColumnTypes.VARCHAR, 40, true, "");
					String msgNameListtable = dao.createTable(nameListtable);

					//値の挿入
					List<String> nameList = new ArrayList<String>(
							new LinkedHashSet<>(ScenarioUtil.mappingArrayList(dao.select(new ExecuteQuery(tableName, dao)), record -> (String) record.get("name"))));
					Insert inserttoNameList = new Insert(nameListTableName, dao);
					inserttoNameList.setInsertValue(nameList);
					String messageInsertNameList = dao.insert(inserttoNameList);
					request.setAttribute("execute_message", msgNameListtable + " " + messageInsertNameList);
				}

			}
			if (actionEquals(action, "createTableByManuscriptInsert")) {
				for (CodeHeader header : CodeHeader.values()) {
					tableName = HEADER + ＿ + header.toString() + ＿ + NAMELIST;
					List<String> nameList = new ArrayList<String>(
							new LinkedHashSet<>(ScenarioUtil.mappingArrayList(dao.select(new ExecuteQuery(tableName, dao)), record -> (String) record.get("name"))));
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

					dao.truncateTable(tableName);

					request.setAttribute("execute_message", msg + " " + msgBack);
				}

			}

			if (actionEquals(action, "deploy")) {
				ExecuteQuery select = new ExecuteQuery(tableName, dao, Arrays.asList("code"));
				select.setGroupByColumn("code");
				List<String> deployCodes = ScenarioUtil.mappingArrayList(dao.select(select), map -> (String) map.get("code"));

				boolean msgFlg = true;
				String place = "";
				for (String deployCode : deployCodes) {
					List<String> deployMessage = deploy(deployCode, tableName, dao);
					try {
						deployMessage.stream().forEachOrdered(msg -> Integer.parseInt(msg));

					} catch (Exception e) {
						msgFlg = false;
						place = deployCode;
						e.printStackTrace();
					}
				}

				if (msgFlg) {
					request.setAttribute("execute_message", "deployが完了しました。");

				} else {
					request.setAttribute("execute_message", "deployで" + place + "に不備が見つかりました。");
				}

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

	private List<String> deploy(String deployCode, String tableName, MainDAO dao) throws DAOException {

		List<CodeHeader> codeHeaders = NameJudging.judgeAttribute(deployCode);

		ExecuteQuery deployCodeSelect = new ExecuteQuery(tableName, dao, Arrays.asList("code", "name", "arr", "dep"));
		deployCodeSelect.setFilter("code", Operator.EQUALS, deployCode);
		List<Map<String, Object>> schedules = dao.select(deployCodeSelect);
		String shubetsu = "";
		String orthodoxShubetsu = "";
		String ikisaki = (String) schedules.get(schedules.size() - 1).get("name");

		List<String> executeMessage = new ArrayList<>();

		// ヘッダーごとの処理
		for (CodeHeader codeHeader : codeHeaders) {
			String namelistTableName = (HEADER + ＿ + codeHeader.toString() + ＿ + "nameList").toLowerCase();

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
			List<String> scheduleNames = ScenarioUtil.mappingArrayList(filteredSchedules, map -> (String) map.get("name"));
			shubetsu = NameJudging.judgeShubetsu(codeHeader, deployCode, scheduleNames).toString();
			if (codeHeader.toString().equals(deployCode.substring(0, 2))) {
				orthodoxShubetsu = shubetsu;
			}
			insertValues.add(shubetsu);//shubetsuの値挿入
			insertValues.add(ikisaki);//ikisakiの値挿入

			for (Map<String, Object> schedule : filteredSchedules) {

				String arrTime = (String) schedule.get("arr");
				if (ScenarioUtil.checkStringValue(arrTime)) {
					insertValues.add(arrTime);
				}
				String depTime = (String) schedule.get("dep");
				if (ScenarioUtil.checkStringValue(depTime)) {
					insertValues.add(depTime);
				}

			}
			System.out.println("insertFilter   " + insertFilter);
			System.out.println("insertValues   " + insertValues);
			insert.setInsertValue(insertValues);
			executeMessage.add(dao.insert(insert));
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

		return executeMessage;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
