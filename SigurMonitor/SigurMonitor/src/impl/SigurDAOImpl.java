package impl;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.ImageIcon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.DatabaseMetaData;

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
		if(!(new File("photo").exists())) {
			new File("photo").mkdir();
		}
		if(!(new File("audio").exists())) {
			new File("audio").mkdir();
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
        ResultSet rs=null, rsLocal = null;
                    
        Connection h2connection = null;
        Statement createStmt = null;
        PreparedStatement writeStmt = null, updateStmt = null;        
        
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

            String name;
            int object_id, ts;
            boolean updatePhoto;
            
            //h2connection = DriverManager.getConnection("jdbc:h2:mem:db_mem;DB_CLOSE_DELAY=-1");
            h2connection = DriverManager.getConnection("jdbc:h2:./sigurdata");                        
            h2connection.setAutoCommit(false);
            
            DatabaseMetaData metadata = h2connection.getMetaData();
            rs = metadata.getTables(null, null, "VISITORS", null);
            if(!rs.next()) {
            	createStmt = h2connection.createStatement();
            	createStmt.execute("CREATE TABLE VISITORS (ID INTEGER PRIMARY KEY AUTO_INCREMENT, SIGUR_ID INTEGER, NAME VARCHAR, SURNAME VARCHAR, FATHERNAME VARCHAR, TS INTEGER, TABID VARCHAR)");
            }
            rs.close();
            	
            writeStmt = h2connection.prepareStatement("INSERT INTO VISITORS(SIGUR_ID, NAME, SURNAME, FATHERNAME, TS) VALUES(?,?,?,?,?)");
            updateStmt = h2connection.prepareStatement("UPDATE VISITORS SET TS = ? WHERE ID = ?");
            
            rs = stmt.executeQuery("SELECT personal.ID, personal.NAME, photo.TS FROM personal INNER JOIN photo ON personal.id = photo.id WHERE STATUS='AVAILABLE'");
            
            rsLocal = h2connection.createStatement().executeQuery("SELECT ID, TS FROM VISITORS");
            HashMap<Integer, Integer> savedInfo = new HashMap<>();            
            while(rsLocal.next()) {
            	savedInfo.put(rsLocal.getInt(1), rsLocal.getInt(2));;
            }
            rsLocal.close();
            
            ArrayList<String> objectsToLoadPhoto = new ArrayList<String>();
            while(rs.next()){            	
            	
                object_id = rs.getInt(1);
                name = rs.getString(2);
                ts = rs.getInt(3);
                
                updatePhoto = true;
                if(savedInfo.containsKey(object_id)) {
                	updatePhoto = savedInfo.get(object_id) != ts;
                	if(updatePhoto) {
                		updateStmt.setInt(1, ts);
                		updateStmt.setInt(2, object_id);
                		updateStmt.execute();
                	}
                } else {
                	String[] nameSplit = name.split(" ");
                    if(nameSplit.length == 3) {
    					writeStmt.setInt(1, object_id);
    					writeStmt.setString(2, nameSplit[1]);
    					writeStmt.setString(3, nameSplit[0]);
    					writeStmt.setString(4, nameSplit[2]);
    					writeStmt.setInt(5, ts);
    					writeStmt.execute();						
    				}
                };                
                if(updatePhoto) {
                	objectsToLoadPhoto.add(String.valueOf(object_id));
                }
                				
			}
            
            //loading photo
            rs.close();
            
			if (!objectsToLoadPhoto.isEmpty()) {				
				Blob photoBlob;
				FileOutputStream fos;								
				rs = stmt.executeQuery("SELECT ID, PREVIEW_RASTER FROM PHOTO WHERE ID IN ("
						+ String.join(",", objectsToLoadPhoto) + ")");
				while (rs.next()) {
					object_id = rs.getInt(1);
					photoBlob = rs.getBlob(2);										
					fos = new FileOutputStream("photo/" + String.valueOf(object_id) + ".img");
					fos.write(photoBlob.getBytes(1, (int) photoBlob.length()));
					fos.close();					
				}				
			}
			
			h2connection.commit();
            
        } catch (Exception e) {
        	try {
				h2connection.rollback();
			} catch (SQLException e1) {				
				logger.error(e.getMessage());
			}
            e.printStackTrace();
            logger.error(e.getMessage());
            return false;
        } finally {
        	if(rsLocal!=null) {
        		try {
					rsLocal.close();
				} catch (SQLException e) {						
				}
        	}
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
        	if(updateStmt!=null) {
        		try {
        			updateStmt.close();
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
		//try (Connection h2connection = DriverManager.getConnection("jdbc:h2:mem:db_mem;DB_CLOSE_DELAY=-1");
		try (Connection h2connection = DriverManager.getConnection("jdbc:h2:./sigurdata");
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

	@Override
	public ImageIcon getVisitorPhoto(int id)  {
		ImageIcon photoImageIcon = new ImageIcon("photo/" + String.valueOf(id) + ".img");
		return photoImageIcon;
	}

}
