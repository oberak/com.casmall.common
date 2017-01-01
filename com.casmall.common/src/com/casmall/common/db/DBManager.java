package com.casmall.common.db;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class DBManager {
	private static DBManager instance;
	private static final String RESOURCE = "sql-maps.xml";
	private SqlSessionFactory sessionFactory;

	public static DBManager getInstance() {
		if (instance == null)
			instance = new DBManager();
		return instance;
	} // getInstance()

	private DBManager() {
		try {
			Reader reader = Resources.getResourceAsReader(RESOURCE);
			sessionFactory = new SqlSessionFactoryBuilder().build(reader);
		} catch (IOException e) {
			e.printStackTrace();
		} // try
	} // Constructor()

	public SqlSession openSession() {
		return this.openSession(false);
	} // openSession()

	public SqlSession openSession(boolean autoCommit) {
		SqlSession session = null;
		session = sessionFactory.openSession(autoCommit);
		return session;
	} // openSession()

} // class DBManager
