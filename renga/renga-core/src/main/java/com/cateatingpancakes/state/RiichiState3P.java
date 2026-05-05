package com.cateatingpancakes.state;

import java.util.ArrayList;
import java.util.List;

import com.cateatingpancakes.tile.Tile;
import com.cateatingpancakes.wall.BasicWall;
import com.cateatingpancakes.wall.RiichiBuilder3P;
import com.cateatingpancakes.wall.WallBuilder;

public final class RiichiState3P extends RiichiState
{
    /**
     * The number of players in the game.
     */
    public static final int PLAYER_COUNT = 3;

    protected ArrayList<Integer> kita;

    /**
     * Constructs a 3-player Riichi game state with a random wall.
     */
    public RiichiState3P()
    {
        WallBuilder builder = new RiichiBuilder3P();
        this(new BasicWall(builder));
    }

    /**
     * Constructs a 3-player Riichi game state from a given wall.
     * @param wall The wall to use.
     */
    public RiichiState3P(BasicWall wall)
    {
        super(wall);
        addPlayers(PLAYER_COUNT);

        this.seats.addAll(new ArrayList<>(List.of(
            GameWind.EAST, GameWind.SOUTH, GameWind.WEST
        )));
        this.kita = new ArrayList<>(List.of(0, 0, 0));
    }

    /**
     * Get a reference to the array counting north/kita calls for all players.
     * @return The call counts.
     */
    public ArrayList<Integer> directKita()
    {
        return kita;
    }

    @Override
    public int nextTile(Tile tile) 
    {
        int index = tile.toIndex();

        switch(index)
        {
            case 0 -> {
                return 8;  // 1m to 9m
            }
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
                return 27; // 4z to 1z (winds)
            }
            case 33 -> {
                return 31; // 7z to 5z (dragons)
            }
            default -> { 
                return index + 1;
            }
        }
    }
}
