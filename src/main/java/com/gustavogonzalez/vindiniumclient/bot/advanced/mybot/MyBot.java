package com.gustavogonzalez.vindiniumclient.bot.advanced.mybot;

import com.gustavogonzalez.vindiniumclient.bot.BotMove;
import com.gustavogonzalez.vindiniumclient.bot.BotUtils;
import com.brianstempin.vindiniumclient.bot.advanced.AdvancedBot;
import com.brianstempin.vindiniumclient.bot.advanced.AdvancedGameState;
import com.brianstempin.vindiniumclient.bot.advanced.Vertex;
import com.brianstempin.vindiniumclient.dto.GameState;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class MyBot implements AdvancedBot {
	
    public static class GameContext {
        private final AdvancedGameState gameState;
        private final Map<GameState.Position, DijkstraResult> dijkstraResultMap;

        public GameContext(AdvancedGameState gameState, Map<GameState.Position, DijkstraResult> dijkstraResultMap) {
            this.gameState = gameState;
            this.dijkstraResultMap = dijkstraResultMap;
        }

        public AdvancedGameState getGameState() {
            return gameState;
        }

        public Map<GameState.Position, DijkstraResult> getDijkstraResultMap() {
            return dijkstraResultMap;
        }
    }
    
    public static class DijkstraResult {
        private int distance;
        private GameState.Position previous;

        public DijkstraResult(int distance, GameState.Position previous) {
            this.distance = distance;
            this.previous = previous;
        }

        public int getDistance() {
            return distance;
        }

        public GameState.Position getPrevious() {
            return previous;
        }
    }
    
    public static synchronized Map<GameState.Position, DijkstraResult> dijkstraSearch(AdvancedGameState gameState) {
        Map<GameState.Position, DijkstraResult> result = new HashMap<>();

        DijkstraResult startingResult = new DijkstraResult(0, null);
        Queue<GameState.Position> queue = new ArrayBlockingQueue<>(gameState.getBoardGraph().size());
        queue.add(gameState.getMe().getPos());
        result.put(gameState.getMe().getPos(), startingResult);

        while(!queue.isEmpty()) {
            GameState.Position currentPosition = queue.poll();
            DijkstraResult currentResult = result.get(currentPosition);
            Vertex currentVertext = gameState.getBoardGraph().get(currentPosition);

            // If there's a bot here, then this vertex goes nowhere
            if(gameState.getHeroesByPosition().containsKey(currentPosition)
                    && !currentPosition.equals(gameState.getMe().getPos()))
                continue;

            int distance = currentResult.getDistance() + 1;

            for(Vertex neighbor : currentVertext.getAdjacentVertices()) {
                DijkstraResult neighborResult = result.get(neighbor.getPosition());
                if(neighborResult == null) {
                    neighborResult = new DijkstraResult(distance, currentPosition);
                    result.put(neighbor.getPosition(), neighborResult);
                    queue.remove(neighbor.getPosition());
                    queue.add(neighbor.getPosition());
                } else if(neighborResult.distance > distance) {
                    DijkstraResult newNeighborResult = new DijkstraResult(distance, currentPosition);
                    result.put(neighbor.getPosition(), newNeighborResult);
                    queue.remove(neighbor.getPosition());
                    queue.add(neighbor.getPosition());
                }
            }
        }

        return result;
    }
    
    private final Decision<GameContext, BotMove> decisioner;
    
    //Implemented own methods to search in game
    public MyBot(){
    	SquatDecisioner sd = new SquatDecisioner();
    	MineTargetingDecisioner mtd = new MineTargetingDecisioner(sd);
    	EnemyMineTargetingDecisioner emtd = new EnemyMineTargetingDecisioner(mtd);
    	HealDecisioner hd = new HealDecisioner();
    	BotWellnessDecisioner bwd = new BotWellnessDecisioner(emtd, hd);
    	this.decisioner = bwd;
    }
    
    @Override
    public BotMove move(AdvancedGameState gameState) {

        Map<GameState.Position, DijkstraResult> dijkstraResultMap = dijkstraSearch(gameState);

        GameContext context = new GameContext(gameState, dijkstraResultMap);
        return this.decisioner.makeDecision(context);

    }

    @Override
    public void setup() {
        // No-op
    }

    @Override
    public void shutdown() {
        // No-op
    }

}
