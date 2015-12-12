package com.brianstempin.vindiniumclient.bot.advanced.mybot;

public interface Decision<S, R> {

	/**
	 * Given a state, return a result
	 * 
	 * @param state
	 * @return
	 */
	public R makeDecision(S state);
}
