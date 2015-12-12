package com.brianstempin.vindiniumclient.bot.advanced.mybot;

import com.brianstempin.vindiniumclient.bot.advanced.mybot.MyBot.GameContext;
import com.brianstempin.vindiniumclient.bot.BotMove;
import com.brianstempin.vindiniumclient.bot.BotUtils;
import com.brianstempin.vindiniumclient.bot.advanced.Mine;
import com.brianstempin.vindiniumclient.bot.advanced.Vertex;
import com.brianstempin.vindiniumclient.dto.GameState;

import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MineTargetingDecisioner implements Decision<MyBot.GameContext, BotMove> {

	private static final Logger logger = LogManager.getLogger(MineTargetingDecisioner.class);
	private final Decision<MyBot.GameContext, BotMove> noMineFoundDecisioner;

	public MineTargetingDecisioner(Decision<MyBot.GameContext, BotMove> noMineFoundDecisioner) {
		this.noMineFoundDecisioner = noMineFoundDecisioner;
	}

	@Override
	public BotMove makeDecision(MyBot.GameContext context) {
		// TODO Auto-generated method stub

		MyBot.DijkstraResult closestMineResult = null;
		Mine closestMine = null;
		for (Mine currentMine : context.getGameState().getMines().values()) {
			if (currentMine.getOwner() == null) {

				MyBot.DijkstraResult currentResult = context.getDijkstraResultMap().get(currentMine.getPosition());

				if (currentResult == null) {
					continue;
				}
				
				if(closestMine == null){
					closestMine = currentMine;
					closestMineResult = context.getDijkstraResultMap().get(closestMine.getPosition());
					continue;
				}
				else if(closestMineResult.getDistance() > currentResult.getDistance()){
					closestMine = currentMine;
					closestMineResult = context.getDijkstraResultMap().get(closestMine.getPosition());
				}
			}
		}
		
		if(closestMine != null){
			GameState.Position nextMove = closestMine.getPosition();
			while(closestMine != null && closestMineResult.getDistance() > 1){
				nextMove = closestMineResult.getPrevious();
				closestMineResult = context.getDijkstraResultMap().get(nextMove);
			}
			
			logger.info("Going for an empty mine");
			return BotUtils.directionTowards(closestMineResult.getPrevious(), nextMove);
		}
		/*
		 * GameState.Position myPosition =
		 * context.getGameState().getMe().getPos(); Map<GameState.Position,
		 * Vertex> boardGraph = context.getGameState().getBoardGraph();
		 * 
		 * // Are we next to a mine that isn't ours? for (Vertex currentVertex :
		 * boardGraph.get(myPosition).getAdjacentVertices()) { Mine mine =
		 * context.getGameState().getMines().get(currentVertex.getPosition());
		 * if (mine != null && (mine.getOwner() == null ||
		 * mine.getOwner().getId() != context.getGameState().getMe().getId())) {
		 * 
		 * // Is it safe to take? if
		 * (BotUtils.getHeroesAround(context.getGameState(),
		 * context.getDijkstraResultMap(), 1).size() > 0) { logger.info(
		 * "Mine found, but another hero is too close."); return
		 * noMineFoundDecisioner.makeDecision(context); } logger.info(
		 * "Taking a mine that we happen to already be walking by.");
		 * 
		 * return BotUtils.directionTowards(myPosition, mine.getPosition()); } }
		 * 
		 * // Nope. logger.info("No opportunistic mines exist."); return
		 * noMineFoundDecisioner.makeDecision(context);
		 */
		
		logger.info("No empty mines to target");
		return noMineFoundDecisioner.makeDecision(context);
	}
}
