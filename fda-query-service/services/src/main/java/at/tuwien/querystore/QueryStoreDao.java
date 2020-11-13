package at.tuwien.querystore;

import java.util.Optional;

public interface QueryStoreDao {
	public void persistQuery(Query query);

	public Optional<Query> getQueryForResultSetHash(String resultSetHash);
}
