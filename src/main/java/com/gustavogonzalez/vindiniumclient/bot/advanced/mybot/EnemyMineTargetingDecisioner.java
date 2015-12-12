package com.gustavogonzalez.vindiniumclient.bot.advanced.mybot;

import com.gustavogonzalez.vindiniumclient.bot.BotMove;
import com.gustavogonzalez.vindiniumclient.bot.BotUtils;
import com.brianstempin.vindiniumclient.bot.advanced.Mine;
import com.brianstempin.vindiniumclient.bot.advanced.Vertex;
import com.brianstempin.vindiniumclient.dto.GameState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class EnemyMineTargetingDecisioner implements Decision<MyBot.GameContext, BotMove> {

	private static final Logger logger = LogManager.getLogger(EnemyMineTargetingDecisioner.class);

	private final Decision<MyBot.GameContext, BotMove> noTargetFoundDecisioner;

	public EnemyMineTargetingDecisioner(Decision<MyBot.GameContext, BotMove> noTargetFoundDecisioner) {
		this.noTargetFoundDecisioner = noTargetFoundDecisioner;
	}

	@Override
	public BotMove makeDecision(MyBot.GameContext context) {
		logger.info("Deciding which mine to target");
		GameState.Hero me = context.getGameState().getMe();

		// Checks to see if they are any untaken mines first
		for (Mine currentMine : context.getGameState().getMines().values()) {
			if (currentMine.getOwner() == null) {
				return noTargetFoundDecisioner.makeDecision(context);
			}
		}
		
		MyBot.DijkstraResult closestMineResult = null;
		Mine closestMine = null;
		for (Mine currentMine : context.getGameState().getMines().values()) {
			if (currentMine.getOwner().getId() != context.getGameState().getMe().getId()) {

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
			
			logger.info("Going for an enemy mine");
			return BotUtils.directionTowards(closestMineResult.getPrevious(), nextMove);
		}

		
		//Couldn't find an enemy mine to attack
		logger.info("No enemy mines to attack");
		return noTargetFoundDecisioner.makeDecision(context);
	}
}
