package de.onyxbits.tradetrax.services;

import java.io.File;

import org.apache.tapestry5.hibernate.HibernateConfigurer;
import org.hibernate.cfg.Configuration;

/**
 * Configure the database, hibernate is going to use. By default, HSQLDB is
 * used. This can be overriden by putting a hibernate.xml file in the database
 * directory.
 * 
 * @author patrick
 * 
 */
public class LedgerConfigurer implements HibernateConfigurer {

	private File dbpath;

	/**
	 * 
	 * @param basedir
	 *          basedirectory for database files.
	 * @param path
	 *          the contextpath from the servletcontext
	 */
	public LedgerConfigurer(File dbpath) {
		this.dbpath = dbpath;
	}

	public void configure(Configuration configuration) {
		File file = new File(dbpath, "hibernate.xml");
		if (file.exists()) {
			configuration.configure(file);
		}
		else {
			file = new File(dbpath, "hsqldb");
			configuration
					.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver")
					.setProperty("hibernate.connection.url",
							"jdbc:hsqldb:" + file.getAbsolutePath() + ";shutdown=true")
					.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect")
					.setProperty("hibernate.connection.username", "sa")
					.setProperty("hibernate.connection.password", "")
					.setProperty("hibernate.hbm2ddl.auto", "update");
		}
	}
}
