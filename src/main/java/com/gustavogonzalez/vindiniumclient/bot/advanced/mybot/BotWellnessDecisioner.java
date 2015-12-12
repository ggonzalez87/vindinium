package com.gustavogonzalez.vindiniumclient.bot.advanced.mybot;

import com.gustavogonzalez.vindiniumclient.bot.BotMove;
import com.gustavogonzalez.vindiniumclient.bot.BotUtils;
import com.brianstempin.vindiniumclient.bot.advanced.Vertex;
import com.brianstempin.vindiniumclient.dto.GameState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BotWellnessDecisioner implements Decision<MyBot.GameContext, BotMove>{
	private static final Logger logger = LogManager.getLogger(BotWellnessDecisioner.class);

    private final Decision<MyBot.GameContext, BotMove> yesDecisioner;
    private final Decision<MyBot.GameContext, BotMove> noDecisioner;

    public BotWellnessDecisioner(Decision<MyBot.GameContext, BotMove> yesDecisioner,
                                 Decision<MyBot.GameContext, BotMove> noDecisioner) {
        this.yesDecisioner = yesDecisioner;
        this.noDecisioner = noDecisioner;
    }

    @Override
    public BotMove makeDecision(MyBot.GameContext context) {

        GameState.Hero me = context.getGameState().getMe();
        Vertex myVertex = context.getGameState().getBoardGraph().get(me.getPos());

        // Do we have money for a pub?
        if(me.getGold() < 2) {
            // We're broke...pretend like we're healthy.
            logger.info("Bot is broke.  Find empty mines even if its not healthy.");
            return yesDecisioner.makeDecision(context);
        }

        // Is the bot already next to a pub?  Perhaps its worth a drink
        for(Vertex currentVertex : myVertex.getAdjacentVertices()) {
            if(context.getGameState().getPubs().containsKey(
                    currentVertex.getPosition())) {
                if(me.getLife() < 80) {
                    logger.info("Bot is next to a pub already and could use health.");
                    return BotUtils.directionTowards(me.getPos(), currentVertex.getPosition());
                }

                // Once we find a pub, we don't care about evaluating the rest
                break;
            }
        }

        // Is the bot well?
        if(context.getGameState().getMe().getLife() >= 50) {
            logger.info("Bot is healthy.");
            return yesDecisioner.makeDecision(context);
        }
        else {
            logger.info("Bot is damaged.");
            return noDecisioner.makeDecision(context);
        }
    }

}
