package com.cateatingpancakes.engine;

import com.cateatingpancakes.engine.GameAction.ActionType;
import com.cateatingpancakes.hand.CommonSplitter;
import com.cateatingpancakes.hand.Splitter;
import com.cateatingpancakes.state.GameState;
import com.cateatingpancakes.tile.Tile;

public abstract class RiichiEngine<StateT extends GameState> extends GameEngine<StateT>
{
    /**
     * Possible states of Riichi engines.
     * This, and the moment field, is kept separate since other variants may want to add
     * different moments, and, just generally, confusion can arise.
     */
    public static enum EngineMoment
    {
        DRAW, ACTION, DISCARD, INTERRUPT, REACT, FINALIZE
    }

    /**
     * Call selection bit for chiis "from the left", where
     * the discarded tile is rightmost in the meld.
     */
    public static final int   SELECT_CHII_LEFT = 0b000001;

    /**
     * Call selection bit for chiis "in the middle", where
     * the discarded tile is in the middle of the meld.
     */
    public static final int SELECT_CHII_MIDDLE = 0b000010;

    /**
     * Call selection bit for chiis "from the right", where
     * the discarded tile is leftmost in the meld.
     */
    public static final int  SELECT_CHII_RIGHT = 0b000100;

    /**
     * Call selection bit for calls that want to use the first 
     * red tile.
     */
    public static final int   SELECT_CALL_RED_1 = 0b001000;

    /**
     * Call selection bit for calls that want to use the first
     * red tile as well as the second.
     */
    public static final int   SELECT_CALL_RED_2 = 0b010000;

    /**
     * Call selection bit for calls that want to use the first
     * red tile, the second, and the third.
     */
    public static final int   SELECT_CALL_RED_3 = 0b100000;


    protected EngineMoment moment;
    protected boolean[]    riichi;
    protected boolean[]    winCallRight;
    protected Tile         lastDiscard;
    protected ActionType   lastResolved;
    protected Tile         lastInterrupt;
    protected Integer[]    callSelect;
    protected Splitter     splitter = new CommonSplitter();

    /**
     * Gets the engine's moment.
     * @return The engine moment.
     */
    public EngineMoment getMoment()
    {
        return moment;
    }

    /**
     * Sets the call selection integer for resolving certain tile calls.
     * The set and unset bits in this integer act as flags to disambiguate
     * between multiple calls on the same tile when the context of a hand 
     * that can call that tile in multiple ways is added.
     * @param select The call selection integer.
     * @param who The player who is selecting.
     */
    public void selectCall(int select, int who)
    {
        callSelect[who] = select;
    }
}
