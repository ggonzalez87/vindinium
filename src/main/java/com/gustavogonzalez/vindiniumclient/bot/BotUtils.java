package com.gustavogonzalez.vindiniumclient.bot;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.brianstempin.vindiniumclient.bot.BotMove;
import com.brianstempin.vindiniumclient.bot.advanced.AdvancedGameState;
import com.gustavogonzalez.vindiniumclient.bot.advanced.mybot.MyBot;
import com.brianstempin.vindiniumclient.dto.GameState;

public class BotUtils {
	
    public static BotMove directionTowards(GameState.Position currentLocation, GameState.Position target) {
        if (target.getX() < currentLocation.getX()) {
            return BotMove.NORTH;
        } else if (target.getX() > currentLocation.getX()) {
            return BotMove.SOUTH;
        } else if (target.getY() < currentLocation.getY()) {
            return BotMove.WEST;
        } else if (target.getY() > currentLocation.getY()) {
            return BotMove.EAST;
        } else {
            return BotMove.STAY;
        }
    }

    /**
     * Returns a list of the enemies with radius squares of your hero
     * @param gameState
     * @param searchResults
     * @param radius
     * @return
     */
    public static List<GameState.Hero> getHeroesAround(AdvancedGameState gameState,
                                                           Map<GameState.Position, MyBot.DijkstraResult> searchResults,
                                                       int radius) {
        List<GameState.Hero> heroes = new LinkedList<>();

        for(GameState.Hero currentHero : gameState.getHeroesByPosition().values()) {
            GameState.Position currentHeroPosition = currentHero.getPos();
            if(searchResults.get(currentHeroPosition).getDistance() <= radius
                    && currentHero.getId() != gameState.getMe().getId())
                heroes.add(currentHero);
        }

        return heroes;
    }  

}
