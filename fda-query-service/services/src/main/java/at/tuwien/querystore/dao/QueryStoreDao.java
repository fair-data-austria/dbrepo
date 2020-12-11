package at.tuwien.querystore.dao;

import at.tuwien.querystore.model.Query;

public interface QueryStoreDao {
	public void persistQuery(Query query);

	public Query getQueryForResultSetHash(String resultSetHash);
}
