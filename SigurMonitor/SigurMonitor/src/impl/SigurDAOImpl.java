package impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sigur.SigurDAO;

public class SigurDAOImpl implements SigurDAO{

	private static final Logger logger = LogManager.getLogger(SigurDAOImpl.class.getName());
	
	private static SigurDAOImpl instance;
	
	private SigurDAOImpl() {
		try {
            Class.forName("org.h2.Driver");
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
	}
	
	public static synchronized SigurDAOImpl getInstance() {
		if(instance==null) {
			instance = new SigurDAOImpl();
		}
		return instance;
	}
	
	@Override
	public boolean getVisitorsFromServer(Properties syncSettings) {
		
		Connection mysqlconnection = null;
        Statement stmt = null;
        ResultSet rs=null;
                    
        Connection h2connection = null;
        Statement createStmt = null;
        PreparedStatement writeStmt = null;
        
        try {	        		        	
        	StringBuilder stringBuilder = new StringBuilder();
        	stringBuilder.append("jdbc:mysql://");
            stringBuilder.append(syncSettings.getProperty(SigurSettingsManagerImpl.SERVER_ADDRESS_PROPERTY_KEY, "")).append(":3305").append("/");
            stringBuilder.append("tc-db-main");

            DriverManager.setLoginTimeout(60);
            Properties connectionProperties = new Properties();
            connectionProperties.put("connectTimeout", "10000");
            connectionProperties.put("user", "root");
            connectionProperties.put("password", "");
            mysqlconnection = DriverManager.getConnection(stringBuilder.toString(), connectionProperties);
            stmt = mysqlconnection.createStatement();
            	            
            //int syncProgress = 0;

            rs = stmt.executeQuery("SELECT COUNT(id) FROM personal");
            rs.next();
            //int syncSize = rs.getInt(1);
            rs.close();

            String name;
            int object_id;
            
            h2connection = DriverManager.getConnection("jdbc:h2:mem:db_mem;DB_CLOSE_DELAY=-1");
            createStmt = h2connection.createStatement();
            createStmt.execute("CREATE TABLE VISITORS (ID INTEGER PRIMARY KEY AUTO_INCREMENT, SIGUR_ID INTEGER, NAME VARCHAR, SURNAME VARCHAR, FATHERNAME VARCHAR)");
                        
            writeStmt = h2connection.prepareStatement("INSERT INTO VISITORS(SIGUR_ID, NAME, SURNAME, FATHERNAME) VALUES(?,?,?,?)"); 
            
            rs = stmt.executeQuery("SELECT id, name FROM personal");
            while(rs.next()){	                
                object_id = rs.getInt(1);
                name = rs.getString(2);
                String[] nameSplit = name.split(" ");
                if(nameSplit.length == 3) {
					writeStmt.setInt(1, object_id);
					writeStmt.setString(2, nameSplit[1]);
					writeStmt.setString(3, nameSplit[0]);
					writeStmt.setString(4, nameSplit[2]);
					writeStmt.execute();						
				}
				//syncProgress++;
			}

			rs.close();
            stmt.close();
            mysqlconnection.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return false;
        } finally {
        	if(rs!=null) {
        		try {
					rs.close();
				} catch (SQLException e) {						
				}
        	}
        	if(stmt!=null) {
        		try {
					stmt.close();
				} catch (SQLException e) {
				}
        	}
        	if(mysqlconnection!=null) {
        		try {
					mysqlconnection.close();
				} catch (SQLException e) {
				}
        	}
        	if(createStmt!=null) {
        		try {
					createStmt.close();
				} catch (SQLException e) {
				}
        	}
        	if(writeStmt!=null) {
        		try {
					writeStmt.close();
				} catch (SQLException e) {
				}
        	}
        	if(h2connection!=null) {
        		try {
					h2connection.close();
				} catch (SQLException e) {
				}
        	}
        }	
		
		return true;
	}

	@Override
	public String getVisitorName(int id) {
		
		ResultSet rs = null;

		String visitorName = "";
		try (Connection h2connection = DriverManager.getConnection("jdbc:h2:mem:db_mem;DB_CLOSE_DELAY=-1");
				PreparedStatement stmt = h2connection
						.prepareStatement("SELECT NAME, FATHERNAME FROM VISITORS WHERE SIGUR_ID = ?")) {
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			if (rs.next()) {
				StringBuilder sb = new StringBuilder();
				sb.append(rs.getString(1));
				sb.append(" ").append(rs.getString(2));
				visitorName = sb.toString();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
		}
		return visitorName;
	}

}
