package com.gustavogonzalez.vindiniumclient.bot.advanced.mybot;

import com.brianstempin.vindiniumclient.bot.BotMove;
import com.brianstempin.vindiniumclient.bot.BotUtils;
import com.brianstempin.vindiniumclient.bot.advanced.Pub;
import com.brianstempin.vindiniumclient.dto.GameState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class SquatDecisioner implements Decision<MyBot.GameContext, BotMove> {
	
	private static final Logger logger = LogManager.getLogger(SquatDecisioner.class);

    @Override
    public BotMove makeDecision(MyBot.GameContext context) {
        GameState.Hero me = context.getGameState().getMe();
        Map<GameState.Position, MyBot.DijkstraResult> dijkstraResultMap = context.getDijkstraResultMap();

        // The way to squat is to get next to a tavern.  Don't walk into it unless we need health.
        Pub nearestPub = null;
        MyBot.DijkstraResult nearestPubDijkstraResult = null;
        for(Pub pub : context.getGameState().getPubs().values()) {
        	MyBot.DijkstraResult dijkstraResult = dijkstraResultMap.get(pub.getPosition());
            if(nearestPub == null && dijkstraResult != null) {
                nearestPub = pub;
                nearestPubDijkstraResult = dijkstraResultMap.get(pub.getPosition());
                continue;
            }
            if(dijkstraResult != null && dijkstraResultMap.get(nearestPub.getPosition()).getDistance()
                    > dijkstraResult.getDistance()) {
                nearestPub = pub;
                nearestPubDijkstraResult = dijkstraResultMap.get(pub.getPosition());
            }
        }

        // Do we need to move to get there?
        if(null == nearestPubDijkstraResult) {
            return BotMove.STAY;
        } else if(nearestPubDijkstraResult.getDistance() > 1) {
        	MyBot.DijkstraResult currentResult = nearestPubDijkstraResult;
            GameState.Position currentPosition = nearestPub.getPosition();

            while(currentResult.getDistance() > 1) {
                currentPosition = currentResult.getPrevious();
                currentResult = dijkstraResultMap.get(currentPosition);
            }

            logger.info("Moving towards a pub to squat.");
            return BotUtils.directionTowards(me.getPos(), currentPosition);
        }

        // Ok, we must be there.  Do we need health?
        if(me.getLife() < 50) {
            logger.info("Getting health while squatting.");
            return BotUtils.directionTowards(me.getPos(), nearestPub.getPosition());
        }

        // Nothing to do...squat!
        logger.info("Squatting at pub.");
        return BotMove.STAY;
    }

}
