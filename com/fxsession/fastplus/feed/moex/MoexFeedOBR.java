package com.fxsession.fastplus.feed.moex;

import org.apache.log4j.Logger;
import org.openfast.Message;
import org.openfast.SequenceValue;
import org.openfast.session.FastConnectionException;

import com.fxsession.fastplus.fpf.FPFMessage;
import com.fxsession.fastplus.fpf.FPFeedDispatcher;

/**
 * @author Dmitry Vulf
 * 
 *         Aggregated OrderBook Feeds(OBR), 20 best prices
 */
public class MoexFeedOBR extends MoexFeed {
	private static Logger mylogger = Logger.getLogger(MoexFeedOBR.class);

	/**
         * 
         */
	public MoexFeedOBR(FPFeedDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public String getTemplateID() {
		return "3312";
	}

	@Override
	public String getSiteID() {
		return "OBR-A";
	}

	@Override
	public void processMessage(Message message) throws FastConnectionException {

		try {
			if (message.getTemplate().getId().equals(getTemplateID())) {
				FPFMessage fmessage = new FPFMessage(SYMBOL);
				String value = message.getString(FPFMessage
						.getFieldName(MSGSEQNUM));
				fmessage.putFieldValue(MSGSEQNUM, value);
				SequenceValue secval = message.getSequence(FPFMessage
						.getFieldName(GROUPMDENTRIES));

				for (int i = 0; i < secval.getValues().length; i++) {
					value = secval.getValues()[i].getString(FPFMessage
							.getFieldName(SYMBOL));
					fmessage.putFieldValue(SYMBOL, value);
					value = secval.getValues()[i].getString(FPFMessage
							.getFieldName(MDENTRYID));
					fmessage.putFieldValue(MDENTRYID, value);
					value = secval.getValues()[i].getString(FPFMessage
							.getFieldName(MDENTRYTYPE));
					fmessage.putFieldValue(MDENTRYTYPE, value);
					value = secval.getValues()[i].getString(FPFMessage
							.getFieldName(MDENTRYSIZE));
					fmessage.putFieldValue(MDENTRYSIZE, value);
					value = secval.getValues()[i].getString(FPFMessage
							.getFieldName(MDENTRYPX));
					fmessage.putFieldValue(MDENTRYPX, value);
					value = secval.getValues()[i].getString(FPFMessage
							.getFieldName(RPTSEQ));
					fmessage.putFieldValue(RPTSEQ, value);
					value = secval.getValues()[i].getString(FPFMessage
							.getFieldName(MDUPDATEACTION));
					fmessage.putFieldValue(MDUPDATEACTION, value);
					dispatcher.dispatch(this, fmessage);
				}

			} else
				processHeartbeat(message);
		} catch (Exception e) {
			mylogger.error(e);
			throw new FastConnectionException(e);
		}
	}

}