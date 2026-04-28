package com.cateatingpancakes.engine;

import com.cateatingpancakes.state.GameState;

public abstract class GameEngine<T extends GameState>
{
    protected T state;

    /**
     * Get a reference to the game state.
     * @return The game state.
     */
    public T directState()
    {
        return state;
    }
}
