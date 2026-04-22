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
        
        long seed = 0, sum = 0;
        for(int i = 0; i < 100000; i++)
        {
            BasicWall bw = new BasicWall(strategy, seed);
            BasicHand bh = new BasicHand(bw, 13);
            sum += bh.getTilesAway();
            seed++;

            if(!bh.interpret().isEmpty())
            {
                System.out.println(bh);
            }
        }

        System.out.println(sum);
    }
}
