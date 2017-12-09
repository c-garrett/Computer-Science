#include "zorder.h"
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
        
    int *p = izorder(order,5,dim);
    printf("coordinate izorder = %d\n",p[0]);
    printf("coordinate izorder = %d\n",p[1]);

    printf("decimal representation %d\n", zorder(order,p,dim));

    printf("pair_distance_sum_2 %lf\n", pair_distance_sum_2(order, dim));

    return 0;
}

double pair_distance_sum_2(int order, int dim){

	int i;
	double result = 0;
	int *pi = malloc(dim * sizeof(int));
	int *pj1 = malloc(dim * sizeof(int));
	int *pj2 = malloc(dim * sizeof(int));

	int n = (int)pow(dim, order);

	for(i = 0; i < n * n - 2; i++){
		int *pi = izorder(order, i, dim);
		int *pj1 = izorder(order, i+1, dim);
		int *pj2 = izorder(order, i+2, dim);

		result += distance(pi, pj1) + distance(pi, pj2);

		free(pi);
		free(pj1);
		free(pj2);
	}
	return result;
}

double distance(int *pi, int *pj){
	return abs(pi[0] - pj[0]) + abs(pi[1] - pj[1]);
}