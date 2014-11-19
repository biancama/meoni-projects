package com.biancama.gui.easyShipment.persistence;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class DaoConfig {
    
    private static final String resource = "easyShipment/persistence/SqlMapConfig.xml";
    /**
     * SqlMapClient instances are thread safe, so you only need one.
     * In this case, we'll use a static singleton.  So sue me.  ;-)
     */
    
    private static DaoConfig INSTANCE;
    private SqlMapClient sqlMapper;
   
    public static DaoConfig getInstance(){
        if (INSTANCE == null){
            INSTANCE = new DaoConfig();
        }
        return INSTANCE;
    }
    
    private DaoConfig(){
        try {
            Reader reader = Resources.getResourceAsReader(resource);
            sqlMapper = SqlMapClientBuilder.buildSqlMapClient(reader);
            reader.close(); 
          } catch (IOException e) {
            // Fail fast.
            throw new RuntimeException("Something bad happened while building the SqlMapClient instance." + e, e);
          }
    }
    
  
    public SqlMapClient getSqlMapper() {
        return sqlMapper;
    }
    
    public Connection getConnection() throws SQLException{
        return sqlMapper.getCurrentConnection();
    }
    
    
    public DataSource getDataSource(){
        return sqlMapper.getDataSource();
    }
    
 
 
   



}
