package com.biancama.gui.easyShipment.persistence.sqlmapdao;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.biancama.gui.easyShipment.model.Sequence;
import com.biancama.gui.easyShipment.persistence.DaoConfig;
import com.biancama.gui.easyShipment.persistence.iface.SequenceDao;
import com.ibatis.sqlmap.client.SqlMapException;

public class SequenceSqlMapDao implements SequenceDao {

    @Override
    public synchronized int getNextId(String name) throws SQLException {
        Sequence sequence = new Sequence();
        sequence.setTableName(name);

        sequence = (Sequence) DaoConfig.getInstance().getSqlMapper().queryForObject("getSequence", sequence);
        if (sequence == null) {
          throw new SqlMapException("Error: A null sequence was returned from the database (could not get next " + name + " sequence).");
        }
        Sequence updateSequence = new Sequence();
        updateSequence.setId(sequence.getId());
        updateSequence.setIncrementNumber(sequence.getIncrementNumber());
        
        DaoConfig.getInstance().getSqlMapper().update("updateSequence", updateSequence);

        return sequence.getCurrentValue();
    }

    @Override
    public synchronized int getNextDocNumber(String name, Date sequenceDate) throws SQLException {
        Integer IdSequenceForId = getSequenceId(name);
        boolean isStartNewYear = getStartNewYear(IdSequenceForId);
        
        Sequence sequence = produceSequence(IdSequenceForId, sequenceDate);
        String queryId = null;
        String year = null;
        if (isStartNewYear){
            queryId = "getDocNoNextYear";
            year = sequence.getYear();
        }else{
            queryId = "getDocNoNoNextYear";        
        }
        
         sequence = (Sequence) DaoConfig.getInstance().getSqlMapper().queryForObject(queryId, sequence);
        if (sequence == null) {
          throw new SqlMapException("Error: A null sequence was returned from the database (could not get next " + name + " sequence).");
        }
        return updateSequence(isStartNewYear, sequence, year);
    }

    private Sequence produceSequence(Integer IdSequenceForId, Date sequenceDate) {
        Sequence sequence = new Sequence();
        sequence.setId(IdSequenceForId);
        if (sequenceDate != null){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            String calendarYear = sdf.format(sequenceDate);
            sequence.setYear(calendarYear);            
        }
        return sequence;
    }

    private int updateSequence(boolean isStartNewYear, Sequence sequence, String calendarYear) throws SQLException {
        String queryId;
        Sequence updateSequence = new Sequence();
        updateSequence.setId(sequence.getId());
        updateSequence.setIncrementNumber(sequence.getIncrementNumber());
        updateSequence.setYear(calendarYear);
        if (isStartNewYear){
            queryId = "updateDocNoNextYear";
        }else{
            queryId = "updateDocNoNoNextYear";        
        }
        
        DaoConfig.getInstance().getSqlMapper().update(queryId, updateSequence);
        if (sequence.getCurrentValue() != null){
            return sequence.getCurrentValue();
        } else  {
            return 0;
        }
    }

    private boolean getStartNewYear(Integer idSequenceForId) throws SQLException {
        String result = (String) DaoConfig.getInstance().getSqlMapper().queryForObject("getSequenceStartNewYear", idSequenceForId);
        if (result == null){
            return false;
        }
        return "Y".equals(result);
    }

    private Integer getSequenceId(String name) throws SQLException {
        Sequence dummySequence = new Sequence();
        dummySequence.setDocName(name);
        return (Integer) DaoConfig.getInstance().getSqlMapper().queryForObject("getSequenceIdForDoc", dummySequence);
    }

    @Override
    public void updateDocNumber(String docNumber, String sequenceName, Date sequenceDate) throws SQLException {
        Integer IdSequenceForId = getSequenceId(sequenceName);
        boolean isStartNewYear = getStartNewYear(IdSequenceForId);
        
        Sequence sequence = produceSequence(IdSequenceForId, sequenceDate);
        updateSequence(isStartNewYear, sequence, sequence.getYear());
        
    }

 

}
