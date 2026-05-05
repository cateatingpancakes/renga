package com.cateatingpancakes.state;

import java.util.ArrayList;
import java.util.List;

import com.cateatingpancakes.tile.Tile;
import com.cateatingpancakes.wall.BasicWall;
import com.cateatingpancakes.wall.RiichiBuilder4P;
import com.cateatingpancakes.wall.WallBuilder;

public final class RiichiState4P extends RiichiState
{
    /**
     * The number of players in the game.
     */
    public static final int PLAYER_COUNT = 4;

    /**
     * Constructs a 4-player Riichi game state with a random wall.
     */
    public RiichiState4P()
    {
        WallBuilder builder = new RiichiBuilder4P();
        this(new BasicWall(builder));
    }

    /**
     * Constructs a 4-player Riichi game state from a given wall.
     * @param wall The wall to use.
     */
    public RiichiState4P(BasicWall wall)
    {
        super(wall);
        addPlayers(PLAYER_COUNT);

        this.seats.addAll(new ArrayList<>(List.of(
            GameWind.EAST, GameWind.SOUTH, GameWind.WEST, GameWind.NORTH
        )));
    }

    @Override
    public int nextTile(Tile tile) 
    {
        int index = tile.toIndex();

        switch(index)
        {
            case 8 -> {
                return 0;  // 9m to 1m
            }
            case 17 -> {
                return 9;  // 9p to 1p
            }
            case 26 -> {
                return 18; // 9s to 1s
            }
            case 30 -> {
                return 27; // 4z to 1z (wind loop-around)
            }
            case 33 -> {
                return 31; // 7z to 5z (dragon loop-around)
            }
            default -> { 
                return index + 1;
            }
        }
    }
}
