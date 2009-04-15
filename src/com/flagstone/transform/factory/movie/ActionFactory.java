package com.flagstone.transform.factory.movie;

import com.flagstone.transform.coder.CoderException;
import com.flagstone.transform.coder.SWFDecoder;
import com.flagstone.transform.movie.Types;
import com.flagstone.transform.movie.action.Action;
import com.flagstone.transform.movie.action.ActionObject;
import com.flagstone.transform.movie.action.BasicAction;
import com.flagstone.transform.movie.action.Call;
import com.flagstone.transform.movie.action.ExceptionHandler;
import com.flagstone.transform.movie.action.GetUrl;
import com.flagstone.transform.movie.action.GetUrl2;
import com.flagstone.transform.movie.action.GotoFrame;
import com.flagstone.transform.movie.action.GotoFrame2;
import com.flagstone.transform.movie.action.GotoLabel;
import com.flagstone.transform.movie.action.If;
import com.flagstone.transform.movie.action.Jump;
import com.flagstone.transform.movie.action.NewFunction;
import com.flagstone.transform.movie.action.NewFunction2;
import com.flagstone.transform.movie.action.Push;
import com.flagstone.transform.movie.action.RegisterCopy;
import com.flagstone.transform.movie.action.SetTarget;
import com.flagstone.transform.movie.action.Table;
import com.flagstone.transform.movie.action.WaitForFrame;
import com.flagstone.transform.movie.action.WaitForFrame2;
import com.flagstone.transform.movie.action.With;

/**
 * Factory is the default implementation of an SWFFactory which used to create 
 * instances of Transform classes.
 */
@SuppressWarnings("PMD")
public final class ActionFactory implements SWFFactory<Action> {

	public Action getObject(final SWFDecoder coder) throws CoderException {

		Action action;
		
		int type = coder.scanByte();
		
		if (type < 128) {
			action = BasicAction.fromInt(coder.readByte());
		} else {
			switch (type) {
			case Types.GET_URL:
				action = new GetUrl(coder);
				break;
			case Types.GOTO_FRAME:
				action = new GotoFrame(coder);
				break;
			case Types.GOTO_LABEL:
				action = new GotoLabel(coder);
				break;
			case Types.SET_TARGET:
				action = new SetTarget(coder);
				break;
			case Types.WAIT_FOR_FRAME:
				action = new WaitForFrame(coder);
				break;
			case Types.CALL:
				action = Call.getInstance();
				coder.adjustPointer(24);
				break;
			case Types.PUSH:
				action = new Push(coder);
				break;
			case Types.WAIT_FOR_FRAME_2:
				action = new WaitForFrame2(coder);
				break;
			case Types.JUMP:
				action = new Jump(coder);
				break;
			case Types.IF:
				action = new If(coder);
				break;
			case Types.GET_URL_2:
				action = new GetUrl2(coder);
				break;
			case Types.GOTO_FRAME_2:
				action = new GotoFrame2(coder);
				break;
			case Types.TABLE:
				action = new Table(coder);
				break;
			case Types.REGISTER_COPY:
				action = new RegisterCopy(coder);
				break;
			case Types.NEW_FUNCTION:
				action = new NewFunction(coder);
				break;
			case Types.WITH:
				action = new With(coder);
				break;
			case Types.EXCEPTION_HANDLER:
				action = new ExceptionHandler(coder);
				break;
			case Types.NEW_FUNCTION_2:
				action = new NewFunction2(coder);
				break;
			default:
				action = new ActionObject(coder);
				break;
			}
		}
		return action;
	}

}