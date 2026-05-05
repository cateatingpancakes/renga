package com.cateatingpancakes.engine;

import com.cateatingpancakes.tile.Tile;

/**
 * Represents a possible action a player may take during the course of the game.
 * While ActionType should be self-explanatory, the other two fields of the record
 * behave as follows:
 * - target: An integer indicating the target of an action. For tile calls from
 * another player, this is to be their number according to the engine's mapping of
 * players to integers. For tile calls that don't involve another player, this is
 * the index of the called tile within the caller's hand.
 * - tileTarget: The tile that is being involved in the call. In some cases, this
 * field, along with the target, do not form a complete description of the action, 
 * and so may require some context or choice from the player's hand, this choice
 * being the game engine's responsibility to implement. For instance, GameAction
 * {ActionType.CHII, 2, 0m} completely represents a chii call made on the red 5
 * of manzu in a Riichi game, but it does not carry the context of whether to
 * call the tile with a 34m, 46m or 67m protorun in a player's hand.
 */
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
        KAN_OPEN, KAN_CLOSED, KAN_ADDED, KITA, CHII, PON
    }
}
