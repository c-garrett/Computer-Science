#include "horder.h"
#include <math.h>
#include "util.h"

int main(int argc, char ** argv)
{
    if(argc < 2)
    {
        printf("Arguments error\n");
        return 0;
    }

    int order, dim = 2;
    
    order = atoi(argv[1]);

    printf("pair_distance_sum_2 %d\n", pair_distance_sum2(order, dim));
        
    return 0;
}

int pair_distance_sum2(int order, int dim){


	int i, j, num = (int)pow(dim,order), result = 0;
	num *= num;
	num -= 2;

	int *pi = malloc(dim * sizeof(int));
	int *pj1 = malloc(dim * sizeof(int));
	int *pj2 = malloc(dim * sizeof(int));

	for(i = 0; i < num; i++){
		pi = ihorder(order, i);
		pj1 = ihorder(order, i + 1);
		pj2 = ihorder(order, i + 2);
		for(j = 0; j < dim; j++){
			result += abs(pi[j] - pj1[j]);
			result += abs(pi[j] - pj2[j]);
		}
		free(pi);
		free(pj1);
		free(pj2);
	}

	return result;

}