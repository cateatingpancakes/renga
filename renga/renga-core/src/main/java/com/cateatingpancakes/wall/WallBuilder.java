package com.cateatingpancakes.wall;

import java.util.ArrayList;

import com.cateatingpancakes.Tile;

public interface WallBuilder 
{
    /**
     * This interface's contract is fulfilled if the implementor returns a non-empty, non-null ArrayList of Tile objects
     * that must be present in the wall in order to begin a "proper" game of the implemented variant, in no particular order.
     * @return An ArrayList of tiles to begin in the wall.
     */
    ArrayList<Tile> buildWall();
}
