package com.cateatingpancakes.engine;

import com.cateatingpancakes.tile.Tile;

public record GameAction(ActionType type, Integer target, Tile tileTarget) 
{
    /**
     * Copy-constructs a game action.
     * @param other The game action to copy.
     */
    public GameAction(GameAction other)
    {
        this(other.type, other.target, other.tileTarget);
    }

    /**
     * Enumeration of all possible Mahjong action types.
     * If you're implementing a new variant, add your action here.
     * This is the "universal dictionary" for GameEngine action types,
     * and if an invalid action type is somehow sent to your GameEngine,
     * then it should ignore that input.
     */
    public static enum ActionType
    {
        NOTHING, DISCARD, FLOWER, RIICHI, RON, TSUMO, 
        KAN_OPEN, KAN_CLOSED, KAN_ADEDD, KITA, CHII, PON
    }
}
