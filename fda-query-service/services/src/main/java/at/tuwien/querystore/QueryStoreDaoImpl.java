package at.tuwien.querystore;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

@Repository
public class QueryStoreDaoImpl implements QueryStoreDao {


	private SessionFactory sessionFactory;

	private Session session;

	private static final Logger LOGGER = LoggerFactory.getLogger(QueryStoreDaoImpl.class);

	@Autowired
	public QueryStoreDaoImpl(SessionFactory sessionFactory){
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void persistQuery(Query query) {
		configureDatasource();
		this.session = sessionFactory.openSession();
		this.session.beginTransaction();
		this.session.persist(query);
		this.session.getTransaction().commit();
		this.session.close();
	}

	private void configureDatasource() {


	}

	@Override
	public Optional<Query> getQueryForResultSetHash(String resultSetHash) {
		this.session = sessionFactory.openSession();
		this.session.beginTransaction();
		org.hibernate.query.Query query = this.session.createQuery("from Query where resultsetHash = :resultSetHash");
		query.setParameter("resultSetHash", resultSetHash);

		Query resultQuery = null;
		try {
			resultQuery = (Query) query.getSingleResult();
		} catch (NoResultException e) {
			LOGGER.info("resultsetHash is not in query_store yet");
		}
		this.session.getTransaction().commit();
		this.session.close();
		return Optional.ofNullable(resultQuery);
	}

	public Optional<Query> getQueryForPID(int pid) {
		this.session = sessionFactory.openSession();
		this.session.beginTransaction();
		org.hibernate.query.Query query = this.session.createQuery("from Query where pid = :pid");
		query.setParameter("pid", pid);

		Query resultQuery = null;
		try {
			resultQuery = (Query) query.getSingleResult();
		} catch (NoResultException e) {
			LOGGER.info("there is no query with pid=" + pid);
		}
		this.session.getTransaction().commit();
		this.session.close();
		return Optional.ofNullable(resultQuery);
	}

	public ResultSet executeQuery(String query) {
		Statement stmt = null;
		Connection connection = null;
		ResultSet rs = null;
		try {
			connection = getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	private Connection getConnection() {
		Connection connection = null;
		Session session = sessionFactory.openSession();
		SessionImpl sessionImpl = (SessionImpl) session;
		connection = sessionImpl.connection();
		return connection;

	}

}
