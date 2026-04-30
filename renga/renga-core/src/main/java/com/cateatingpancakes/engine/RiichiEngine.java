package com.cateatingpancakes.engine;

import com.cateatingpancakes.state.GameState;
import com.cateatingpancakes.tile.Tile;

public abstract class RiichiEngine<StateT extends GameState> extends GameEngine<StateT>
{
    /**
     * Possible states of the engine.
     */
    public static enum EngineMoment
    {
        DRAW, ACTION, DISCARD, INTERRUPT, REACT
    }

    /**
     * The maximum index number of Riichi tiles.
     */
    public static final int INDEX_NUMBER_RIICHI_MAX = 34;

    protected EngineMoment moment;
    protected Tile         lastDiscard;
}
