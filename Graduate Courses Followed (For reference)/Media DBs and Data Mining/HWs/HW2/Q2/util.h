#include <stdio.h>
#include <math.h>

void writeToFile(FILE * f, int x, int y);
int checkCoor(int coor, int order);


void writeToFile(FILE * data, int x, int y) 
{
    fprintf(data, "%d %d\n", x, y);
}

int checkCoor(int coor, int order)
{
    int max = (int)pow(2, order);
    if(coor < 0 || coor > max - 1)
    {
        return -1;
    }
    else
    {
        return 1;
    }
}