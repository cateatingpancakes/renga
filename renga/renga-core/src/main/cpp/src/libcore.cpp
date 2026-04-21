#include <jni.h>
#include "com_cateatingpancakes_BasicHand.h"

inline int min(const int& a, const int& b)
{
    return (a < b) ? a : b;
}

inline int max(const int& a, const int& b)
{
    return (a > b) ? a : b;
}

void naway_dfs(jint * cnt_array, int idx, int groups, int pairs, int waits, int& min_away)
{
    while(idx < 34 && cnt_array[idx] == 0) 
        idx++;

    if(idx >= 34) 
    {
        int head_ok = (pairs > 0) ? 1 : 0;
        int waits_pairs = waits + max(0, pairs - 1);

        if (groups + waits_pairs > 4)
            waits_pairs = 4 - groups;

        int away = 8 - (2 * groups) - waits_pairs - head_ok;
        min_away = min(min_away, away);
        return;
    }

    if(cnt_array[idx] >= 3) 
    {
        cnt_array[idx] -= 3;
        naway_dfs(cnt_array, idx, groups + 1, pairs, waits, min_away);
        cnt_array[idx] += 3;
    }

    if(cnt_array[idx] >= 2) 
    {
        cnt_array[idx] -= 2;
        naway_dfs(cnt_array, idx, groups, pairs + 1, waits, min_away);
        cnt_array[idx] += 2;
    }

    if(idx < 27 && idx % 9 <= 6) 
    {
        if(cnt_array[idx + 1] > 0 && cnt_array[idx + 2] > 0) 
        {
            cnt_array[idx]--; cnt_array[idx + 1]--; cnt_array[idx + 2]--;
            naway_dfs(cnt_array, idx, groups + 1, pairs, waits, min_away);
            cnt_array[idx]++; cnt_array[idx + 1]++; cnt_array[idx + 2]++;
        }
        
        if(cnt_array[idx + 2] > 0) 
        {
            cnt_array[idx]--; cnt_array[idx + 2]--;
            naway_dfs(cnt_array, idx, groups, pairs, waits + 1, min_away);
            cnt_array[idx]++; cnt_array[idx + 2]++;
        }
    }

    if(idx < 27 && idx % 9 <= 7)
    {
        if(cnt_array[idx + 1] > 0) 
        {
            cnt_array[idx]--; cnt_array[idx + 1]--;
            naway_dfs(cnt_array, idx, groups, pairs, waits + 1, min_away);
            cnt_array[idx]++; cnt_array[idx + 1]++;
        }
    }

    cnt_array[idx]--;
    naway_dfs(cnt_array, idx, groups, pairs, waits, min_away);
    cnt_array[idx]++;
}

void naway_pairs(jint * cnt_array, int& min_away) 
{
    int pairs = 0, types = 0;

    for(int i = 0; i < 34; i++) 
    {
        if (cnt_array[i] >= 2) 
            pairs++;
        if (cnt_array[i] > 0) 
            types++;
    }

    int away = 6 - pairs;

    if(types < 7)
        away += (7 - types);

    min_away = min(min_away, away);
}

void naway_kokushi(jint * cnt_array, int& min_away) 
{
    bool head_ok = false;
    int orphans_cnt = 0, orphans_idx[] = {0, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33};

    for(int i = 0; i < 13; i++) 
    {
        int idx = orphans_idx[i];

        if(cnt_array[idx] > 0) 
        {
            orphans_cnt++;
            if(cnt_array[idx] >= 2) 
                head_ok = true;
        }
    }

    int away = 13 - orphans_cnt - (head_ok ? 1 : 0);
    min_away = min(min_away, away);
}

void naway(jint * cnt_array, int calls, int& min_away)
{
    naway_dfs(cnt_array, 0, calls, 0, 0, min_away);

    if(calls == 0)
    {
        naway_pairs(cnt_array, min_away);
        naway_kokushi(cnt_array, min_away);
    }
}

JNIEXPORT jint JNICALL Java_com_cateatingpancakes_BasicHand_tilesAway
    (JNIEnv * env, jobject jthis, jintArray tileCounts)
{
    jint *cnt_array = env->GetIntArrayElements(tileCounts, NULL);

    int tile_cnt = 0;
    for(int i = 0; i < 42; i++)
        tile_cnt += cnt_array[i];

    int calls = 0;
    if(tile_cnt <= 11)
        calls = 1;
    if(tile_cnt <= 8)
        calls = 2;
    if(tile_cnt <= 5)
        calls = 3;
    if(tile_cnt <= 2)
        calls = 4;

    int min_away = 8;
    naway(cnt_array, calls, min_away);

    env->ReleaseIntArrayElements(tileCounts, cnt_array, JNI_ABORT);
    return min_away;
}