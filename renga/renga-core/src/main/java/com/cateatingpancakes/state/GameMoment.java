package com.cateatingpancakes.state;

import java.io.Serializable;

public record GameMoment(int game, GameWind round) implements Serializable
{
    public static enum GameWind
    {
        W_EAST, W_SOUTH, W_WEST, W_NORTH
    }    

    /**
     * Returns the game wind immediately following a given game wind.
     * @param wind The game wind.
     * @return The next wind.
     */
    public static GameWind nextWind(GameWind wind)
    {
        switch(wind)
        {
            case W_EAST -> {
                return GameWind.W_SOUTH;
            }
            case W_SOUTH -> {
                return GameWind.W_WEST;
            }
            case W_WEST -> {
                return GameWind.W_NORTH;
            }
            case W_NORTH -> {
                return GameWind.W_EAST;
            }
            default -> throw new AssertionError("Could not resolve game wind " + wind);
        }
    }
}
