package at.tuwien.querystore;

import org.apache.commons.lang.StringUtils;

public class HistoryTableWrapper {

	public String determineSqlStmtForHistoryData(Query requestQuery) {
		String query = requestQuery.getQuery();
		if (StringUtils.containsIgnoreCase(query, "WHERE")) {
			query += " AND ";
		} else {
			query += " WHERE ";
		}

		return getBaseQueryWithTimeStampCheck(requestQuery, query) + " UNION "
				+ getHistoryQueryWithTimeStampCheck(requestQuery, query);
	}

	private String getHistoryQueryWithTimeStampCheck(Query requestQuery, String tempQuery) {
		tempQuery = tempQuery.replace(requestQuery.getResourceName(), requestQuery.getResourceName() + "_history");
		return tempQuery + " " + getHistoryTimeStampCheckPart(requestQuery);
	}

	private String getBaseQueryWithTimeStampCheck(Query requestQuery, String tempQuery) {
		return tempQuery + " " + getTimeStampCheckPart(requestQuery);
	}

	private String getHistoryTimeStampCheckPart(Query requestQuery) {
		return String.format("lower(sys_period) <= '%s' AND '%s' < upper(sys_period)",
				requestQuery.getExecTimestamp(), requestQuery.getExecTimestamp());
	}
	private String getTimeStampCheckPart(Query requestQuery) {
		return String.format("lower(sys_period) <= '%s'",
				requestQuery.getExecTimestamp());
	}
}
