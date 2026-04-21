package com.cateatingpancakes.wall;

import java.util.ArrayList;

import com.cateatingpancakes.tile.Tile;

/**
 * This interface's contract is fulfilled if the implementor returns via the method buildWall() a non-empty, non-null 
 * ArrayList of Tile objects that must be present in the wall in order to begin a proper game of the implemented 
 * mahjong variant, in no particular order.
 */
public interface WallBuilder 
{
    /**
     * @return An ArrayList of tiles to begin in the wall.
     */
    ArrayList<Tile> buildWall();
}
