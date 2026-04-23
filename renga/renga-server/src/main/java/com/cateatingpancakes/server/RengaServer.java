package com.cateatingpancakes.server;

import com.cateatingpancakes.tile.CallData;
import com.cateatingpancakes.tile.Tile;
import com.cateatingpancakes.tile.TileMeld;
import com.cateatingpancakes.tile.TileSet;

public class RengaServer 
{
    public static void main(String[] args)
    {
        TileSet ts = new TileSet();
        ts.add(new Tile(12));
        ts.add(new Tile(13));
        TileMeld tm = new TileMeld(TileMeld.MeldType.CHII, ts);

        tm.setData(new CallData(new Tile(14), 2));
        System.out.println(tm.getData());
    }
}
