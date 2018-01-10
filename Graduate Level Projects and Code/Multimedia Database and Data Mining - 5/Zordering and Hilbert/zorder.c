#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#include <string.h>
#include "zorder.h"

/*
 * zorder - Calculate the index number (Z-value) of the point (represented by
 * coordinate) in the Z-order curve with given dimension and order.
 */
int zorder(int order, int* coor, int dim)
{
    // Calculate the z-order by bit shuffling
    int i, value = 0, mask, j;
    for(i = 0; i < dim; ++i)
    {
        for(j = 0; j < order; ++j)
        {
            mask = 1 << j;
            // Check whether the value in the position is 1
            if (coor[i] & mask)
                // Do bit shuffling
                value |= 1 << (j * dim + dim - i - 1);
        }
    }
    return value;
}

/*
 * izorder - Calculate the coordinate of the point with given Z-value,
 * dimension and order.
 */
int* izorder(int order, int value, int dim)
{
    int i, mask, j;
    int* coor = malloc(dim * sizeof(int));

    // Initialize the coordinates to zeros
    for(i = 0; i < dim; i++)
        coor[i] = 0;

    for(i = 0; i < order; i++)
    {
        mask = 1 << i * dim;

        for(j = 0; j < dim; j++)
        {
            // Check whether the value in the position is 1
            if((value & (mask << j)))
                // Do bit shuffling
                coor[dim - j - 1] |= 1 << i;
        }
    }

    return coor;
}