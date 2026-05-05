package com.cateatingpancakes.engine;

import com.cateatingpancakes.state.GameState;

public abstract class GameEngine<StateT extends GameState>
{
    protected StateT        state;
    protected EngineRequest activeRequest;
    protected int           activePlayer;

    /**
     * Get a reference to the game state held by the engine.
     * @return The game state.
     */
    public StateT directState()
    {
        return state;
    }

    /**
     * Get a reference to the engine's current input request.
     * @return The request.
     */
    public EngineRequest directRequest()
    {
        return activeRequest;
    }

    /**
     * Build and set the a request based on the active player and the engine moment.
     */
    public abstract void buildRequest();

    /**
     * Advance the game state by one step. This changes the EngineRequest returned by
     * directRequest() when the game can no longer be advanced until player input is
     * filled in, using the new EngineRequest's fields.
     */
    public abstract void advance();

    /**
     * Execute a game action.
     * @param who The player to execute the action.
     * @param action The action to be executed.
     */
    public abstract void execute(int who, GameAction action);
}
