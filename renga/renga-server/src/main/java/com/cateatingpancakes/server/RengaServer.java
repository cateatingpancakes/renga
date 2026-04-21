package com.cateatingpancakes.server;

import com.cateatingpancakes.BasicHand;
import com.cateatingpancakes.BasicWall;
import com.cateatingpancakes.wall.WallBuilder;
import com.cateatingpancakes.wall.WbRiichi4P;

public class RengaServer 
{
    public static void main(String[] args)
    {
        WallBuilder strategy = new WbRiichi4P();

        long seed = 0, sumAway = 0;
        for(int i = 0; i < 100000; i++)
        {
            BasicWall wall = new BasicWall(strategy, seed);
            BasicHand hand = new BasicHand(wall, 13);
            int away = hand.getTilesAway();
            sumAway += away;
            seed++;
        }

        System.out.println(sumAway);
    }
}
