#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#include <string.h>
#include "horder.h"
int ** hcurve;

/*
 * horder - Calculate the index number (H-value) of the point (represented by
 * coordinate) in Hilbert curve with given order.
 */
int horder(int order, int x, int y)
{
    hcurve = (int **)malloc(sizeof(int *) * 2);
    
    int alloc;
    for(alloc = 0; alloc < 2; alloc++)
        hcurve[alloc] = (int *)malloc(sizeof(int) * 2);
    hcurve[0][0] = 1;
    hcurve[0][1] = 2;
    hcurve[1][0] = 0;
    hcurve[1][1] = 3;

    if(order != 1)
    {
        int ** temp;
        int i, j, k, blockLen, add, num;
        for(i = 2; i <= order; i++)
        {
            add = (int)pow(2, 2 * i - 2);
            blockLen = (int)pow(2, i - 1);
            temp = hcurve;
            
            num = (int)pow(2, i);
            hcurve = (int **)malloc(sizeof(int *) * num);
            for(alloc = 0; alloc < num; alloc++)
                hcurve[alloc] = (int *)malloc(sizeof(int) * num);

            for(j = 0; j < blockLen; j++)
            {
                for(k = 0; k < blockLen; k++)
                {
                    hcurve[j][k] = temp[j][k] + add;
                    hcurve[j][k + blockLen] = temp[j][k] + 2 * add;
                    hcurve[j + blockLen][k] = temp[blockLen - k - 1][blockLen - j - 1];
                    hcurve[j + blockLen][k + blockLen] = temp[k][j] + 3 * add;
                }
            }

            free(temp);
        }
    }

    return hcurve[(int)pow(2, order) - 1 - y][x];
}

/*
 * ihorder - Calculate the coordinate of the point with given H-value and
 * order.
 */
int* ihorder(int order, int value)
{
    hcurve = (int **)malloc(sizeof(int *) * 2);
    
    int alloc;
    for(alloc = 0; alloc < 2; alloc++)
        hcurve[alloc] = (int *)malloc(sizeof(int) * 2);
    hcurve[0][0] = 1;
    hcurve[0][1] = 2;
    hcurve[1][0] = 0;
    hcurve[1][1] = 3;

    if(order != 1)
    {
        int ** temp;
        int i, j, k, blockLen, add, num;
        for(i = 2; i <= order; i++)
        {
            add = (int)pow(2, 2 * i - 2);
            blockLen = (int)pow(2, i - 1);
            temp = hcurve;
            
            num = (int)pow(2, i);
            hcurve = (int **)malloc(sizeof(int *) * num);
            for(alloc = 0; alloc < num; alloc++)
                hcurve[alloc] = (int *)malloc(sizeof(int) * num);

            for(j = 0; j < blockLen; j++)
            {
                for(k = 0; k < blockLen; k++)
                {
                    hcurve[j][k] = temp[j][k] + add;
                    hcurve[j][k + blockLen] = temp[j][k] + 2 * add;
                    hcurve[j + blockLen][k] = temp[blockLen - k - 1][blockLen - j - 1];
                    hcurve[j + blockLen][k + blockLen] = temp[k][j] + 3 * add;
                }
            }

            free(temp);
        }
    }

    int x, y, flag = 0;
    int base = (int)pow(2, order);
    for(x = 0; x < base; x++)
    {
        for(y = 0; y < base; y++)
        {
            if(hcurve[x][y] == value)
            {
                flag = 1;
                break;
            }
        }
        if(flag == 1)
            break;
    }

    int* coor = malloc(2 * sizeof(int));
    coor[0] = y;
    coor[1] = base - x - 1;
    return coor;
}

