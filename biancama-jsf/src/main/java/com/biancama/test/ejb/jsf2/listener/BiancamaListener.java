package com.biancama.test.ejb.jsf2.listener;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BiancamaListener implements PhaseListener {

	private final static Logger logger = LoggerFactory.getLogger(BiancamaListener.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 5518684426190726397L;

	@Override
	public void beforePhase(PhaseEvent event) {
		logger.info("START PHASE " + event.getPhaseId());
	}

	@Override
	public void afterPhase(PhaseEvent event) {
		logger.info("END PHASE " + event.getPhaseId());
	}


	@Override
	public PhaseId getPhaseId() {		
		 return PhaseId.ANY_PHASE;
	}
}
