#include "horder.h"
#include "util.h"

int main(int argc, char ** argv)
{
    int order, i, j, dim, num;

    char fname[20] = "hpoint.dat";
    FILE * data;
    data = fopen(fname, "w");
    
    order = 7;
    dim = 2;
    num = (int)pow(dim, order);

    int* coord = malloc(2 * sizeof(int));

    for(i = 0; i < num * num; i++)
    {
        coord = ihorder(order, i);
        // To be implemented
        writeToFile(data, coord[0], coord[1]);
        free(coord);
    }
    
    printf("    FYI: %s is done - result is in file %s\n", argv[0], fname);
    return 0;
}
