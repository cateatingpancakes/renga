package com.cateatingpancakes.server;

import java.util.ArrayList;

import com.cateatingpancakes.hand.BasicHand;
import com.cateatingpancakes.hand.CommonSplitter;
import com.cateatingpancakes.hand.HandInterpretation;
import com.cateatingpancakes.hand.HandSplitter;
import com.cateatingpancakes.tile.TileMeld;
import com.cateatingpancakes.wall.BasicWall;
import com.cateatingpancakes.wall.RiichiBuilder4P;
import com.cateatingpancakes.wall.WallBuilder;

public class RengaServer 
{
    public static void main(String[] args)
    {
        HandSplitter hs = new CommonSplitter();
        int sum = 0;
        long seed = 0;

        for(int i = 0; i < 100000; i++)
        {
            WallBuilder wb = new RiichiBuilder4P();
            BasicWall bw = new BasicWall(wb, seed);
            BasicHand bh = new BasicHand(bw, 13);

            int na = hs.tilesAway(bh);
            sum += na;
            if(na < 0)
            {
                System.out.println(bh);
                ArrayList<HandInterpretation> his = hs.interpret(bh);
                for(HandInterpretation hi : his)
                    for(TileMeld tm : hi.getMelds())
                        System.out.println(tm);
            }
            seed++;
        }

        System.out.println(sum);
    }
}
