package com.cateatingpancakes.server;

import java.util.ArrayList;

import com.cateatingpancakes.hand.BasicHand;
import com.cateatingpancakes.hand.CommonSplitter;
import com.cateatingpancakes.hand.HandInterpretation;
import com.cateatingpancakes.hand.Splitter;
import com.cateatingpancakes.state.RiichiState4P;
import com.cateatingpancakes.tile.TileMeld;
import com.cateatingpancakes.wall.BasicWall;
import com.cateatingpancakes.wall.RiichiBuilder4P;
import com.cateatingpancakes.wall.WallBuilder;

public class RengaServer 
{
    public static void main(String[] args)
    {
        Splitter hs = new CommonSplitter();
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

        RiichiState4P rs3p = new RiichiState4P();
        for(BasicHand bhx : rs3p.directHands())
            System.out.println(bhx);
        System.out.println(rs3p.directWall());
        System.out.println(rs3p.directDeadWallBottom());
        System.out.println(rs3p.directDeadWallTop());
        rs3p.directHands().get(0).sort();
        rs3p.directPonds().get(0).add(rs3p.directHands().get(0).get(2));
        rs3p.directHands().get(0).remove(2);
        rs3p.throwTile(0, 2);
        rs3p.throwTile(0, 2);
        System.out.println(rs3p.directPonds().get(0));
        System.out.println(rs3p.directHands().get(0));
    }
}
