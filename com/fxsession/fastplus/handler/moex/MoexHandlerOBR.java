/**
 * 
 */
package com.fxsession.fastplus.handler.moex;

import java.util.concurrent.atomic.AtomicInteger;


import com.fxsession.fastplus.fpf.FPFMessage;
import com.fxsession.fastplus.fpf.FPFOrderBookL2;
import com.fxsession.fastplus.fpf.IFPFHandler;
import com.fxsession.fastplus.fpf.IFPFOrderBook;
import com.fxsession.fastplus.fpf.IFPField;
import com.fxsession.fastplus.fpf.OnCommand;

/**
 * @author Dmitry Vulf
 * 
 * OBR
 *
 */

public class MoexHandlerOBR extends FPFOrderBookL2 implements IFPFHandler, IFPField {
	
	AtomicInteger  rptSeq = new AtomicInteger(-1);
	@Override
	public String getInstrumentID() {

		return "EURUSD000TOM";
	}
	
	public boolean checkRepeatMessage(String sRpt) {
		/*
		 * THis method cuts off duplicate messages coming from the 2 stream. However it cuts only 95% of duplicates 
		 */
		Integer iRep =   Integer.valueOf(sRpt);
		if (iRep ==rptSeq.intValue())
			return true;
		else{
			rptSeq.set(iRep);
			return false;
		}
	} 

	
	public OnCommand push(FPFMessage message) {
		OnCommand retval = OnCommand.ON_PROCESS;
	    String rptseq = message.getFieldValue(RPTSEQ);
	    if (checkRepeatMessage(rptseq))
	    	return retval;
		String key =  message.getFieldValue(MDENTRYID);
	    String type = message.getFieldValue(MDENTRYTYPE);
	    String size = message.getFieldValue(MDENTRYSIZE);
	    String px = message.getFieldValue(MDENTRYPX);
	    String timemcs = message.getFieldValue(ORIGINTIME);
	    String timestamp = message.getFieldValue(MDENTRYTIME);
	    Long  ltimestamp = Long.parseLong(timestamp);
		Long  ltimemcs = Long.parseLong(timemcs);
	    String updAction =message.getFieldValue(MDUPDATEACTION); 
		switch (updAction){
			case IFPFOrderBook.ADD 		: 
				if (type.equals(IFPFOrderBook.BID))  
					addBid(key,size, px,ltimestamp,ltimemcs); 
				else
					addAsk(key,size, px,ltimestamp,ltimemcs);  
				break;
			case IFPFOrderBook.CHANGE 	:   
				if (type.equals(IFPFOrderBook.BID))
					changeBid(key,size, px, ltimestamp,ltimemcs);
				else
					changeAsk(key,size, px, ltimestamp,ltimemcs);
				break;
			case IFPFOrderBook.DELETE 	: 
				if (type.equals(IFPFOrderBook.BID)) 
					deleteBid(key,null); 
				else
					deleteAsk(key,null);
				break;
			default :break; 
		}
		return retval;
	}
	

}
