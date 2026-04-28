package com.cateatingpancakes.state;

import java.io.Serializable;

public record GameMoment(int game, int repeat, GameWind round) implements Serializable
{
    /**
     * Copy-constructs a game moment.
     * @param moment The moment to copy.
     */
    public GameMoment(GameMoment other)
    {
        this(other.game, other.repeat, other.round);
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
            case  EAST -> {
                return GameWind.SOUTH;
            }
            case SOUTH -> {
                return GameWind.WEST;
            }
            case  WEST -> {
                return GameWind.NORTH;
            }
            case NORTH -> {
                return GameWind.EAST;
            }
            default -> throw new AssertionError("Could not resolve game wind " + wind);
        }
    }
}
