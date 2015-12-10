/**
 * This code fetches the video text from the MYSQL DB
 */
package com.vars.videoadanalysis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author VARS
 *
 */
public class FetchFromDB {

	static Connection conn = null;

	/**
	 * @param args
	 */

	protected FetchFromDB(String userName, String password, String url) {

		try {
			/**
			 * Load JDBC driver for MySQL.
			 */
			Class.forName("com.mysql.jdbc.Driver");

			/**
			 * Connect to the database.
			 */

			conn = DriverManager.getConnection(url, userName, password);

		} catch (Exception e) {
			System.err.println("Cannot connect to database server:\n ");
			conn = null;
		}

	}

	protected ArrayList<String> retrieve(String query, String type) {

		if (conn == null)
			throw new NullPointerException();

		ArrayList<String> valueList = new ArrayList<String>();

		try {

			// Prepare statement
			PreparedStatement querySailors = conn.prepareStatement(query);

			// Execute the query and store it in result set
			ResultSet rs = querySailors.executeQuery();

			while (rs.next()) {
				valueList.add(rs.getString(type));
			}

			// Close result set and close query
			rs.close();
			querySailors.close();
		} catch (SQLException e) {
			System.out.println("Exception while Retrieval");
		}

		return valueList;
	}

	protected HashMap<String, String> retrieve(String query) {

		if (conn == null)
			throw new NullPointerException();

		HashMap<String, String> wordsAndweight = new HashMap<String, String>();

		try {

			// Prepare statement
			PreparedStatement querySailors = conn.prepareStatement(query);

			// Execute the query and store it in result set
			ResultSet rs = querySailors.executeQuery();

			while (rs.next()) {
				wordsAndweight
						.put(rs.getString("word"), rs.getString("weight"));
			}

			// Close result set and close query
			rs.close();
			querySailors.close();
		} catch (SQLException e) {
			System.out.println("Exception while Retrieval");
		}

		return wordsAndweight;
	}

	protected HashMap<String, String> retrieve() {

		if (conn == null)
			throw new NullPointerException();

		// LinkedList<String> valueList = new LinkedList<String>();
		// LinkedList<String> keyList = new LinkedList<String>();
		HashMap<String, String> videoData = new HashMap<String, String>();

		try {

			// Prepare statement
			PreparedStatement querySailors = conn
					.prepareStatement("SELECT video,video_name FROM ads_processed;");

			// Execute the query and store it in result set
			ResultSet rs = querySailors.executeQuery();

			while (rs.next()) {

				videoData
						.put(rs.getString("video_name"), rs.getString("video"));
				// valueList.add(rs.getString("video"));
				// keyList.add(rs.getString("video_name"));
			}

			// Close result set and close query
			rs.close();
			querySailors.close();
		} catch (SQLException e) {
			System.out.println("Exception while Retrieval");
		}

		// return valueList;
		return videoData;
	}
}
