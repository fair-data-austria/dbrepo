package at.tuwien.querystore.dao.impl;

import org.springframework.stereotype.Repository;

@Repository
public class QueryStoreDaoImpl  {

//	@Autowired
//	private SessionFactory sessionFactory;
//
//	private Session session;
//
//	private static final Logger LOGGER = LoggerFactory.getLogger(QueryStoreDaoImpl.class);
//
//	@Override
//	public void persistQuery(Query query) {
//		this.session = sessionFactory.openSession();
//		this.session.beginTransaction();
//		this.session.persist(query);
//		this.session.getTransaction().commit();
//		this.session.close();
//	}
//
//	@Override
//	public Optional<Query> getQueryForResultSetHash(String resultSetHash) {
//		this.session = sessionFactory.openSession();
//		this.session.beginTransaction();
//		org.hibernate.query.Query query = this.session.createQuery("from Query where resultset_hash = :resultSetHash");
//		query.setParameter("resultSetHash", resultSetHash);
//
//		Query resultQuery = null;
//		try {
//			resultQuery = (Query) query.getSingleResult();
//		} catch (NoResultException e) {
//			LOGGER.info("resultsetHash is not in query_store yet");
//		}
//		this.session.getTransaction().commit();
//		this.session.close();
//		return Optional.ofNullable(resultQuery);
//	}
//
//	public Optional<Query> getQueryForPID(int pid) {
//		this.session = sessionFactory.openSession();
//		this.session.beginTransaction();
//		org.hibernate.query.Query query = this.session.createQuery("from Query where pid = :pid");
//		query.setParameter("pid", pid);
//
//		Query resultQuery = null;
//		try {
//			resultQuery = (Query) query.getSingleResult();
//		} catch (NoResultException e) {
//			LOGGER.info("there is no query with pid=" + pid);
//		}
//		this.session.getTransaction().commit();
//		this.session.close();
//		return Optional.ofNullable(resultQuery);
//	}
//
//	public ResultSet executeQuery(String query) {
//		Statement stmt = null;
//		Connection connection = null;
//		ResultSet rs = null;
//		try {
//			connection = getConnection();
//			stmt = connection.createStatement();
//			rs = stmt.executeQuery(query);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return rs;
//	}
//
//	private Connection getConnection() {
//		Connection connection = null;
//		Session session = sessionFactory.openSession();
//		SessionImpl sessionImpl = (SessionImpl) session;
//		connection = sessionImpl.connection();
//		return connection;
//
//	}

}
