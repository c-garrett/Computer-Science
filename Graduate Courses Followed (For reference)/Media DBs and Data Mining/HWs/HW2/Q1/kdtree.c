/* $Revision: 2.1 $ */
/* $Author: christos $ */
/* $Id: kdtree.c,v 2.1 2001/02/11 16:22:16 christos Exp Locker: christos $ */

#include <stdlib.h>
#include "kdtree.h"
#include "vector.h"

/* inserts a vector into the tree, and returns the new root */

TREENODE *insert(TREENODE *subroot, VECTOR *vp){
    #ifdef DEBUG
    // printf("tree insert was called with \n");
    // vecprint(vp);
    #endif

    return( rinsert(subroot, vp, 0) );
}

TREENODE *rinsert(TREENODE *subroot, VECTOR *vp, int level){

    if(subroot == NULL){ /* we hit the bottom of the tree */
    subroot = talloc();
    subroot->pvec  = veccopy(vp);
    return(subroot);
    }

    if( (vp->vec)[level] <= ((subroot->pvec)->vec)[level] ){
    subroot->left = rinsert(subroot->left, vp, (level+1) % (vp->len) );
    }else{
    subroot->right = rinsert(subroot->right, vp, (level+1) % (vp->len) );
    }
    
    return(subroot); /* although it didn't change */
}

void tprint(TREENODE *subroot){
    rtprint(subroot, 0);
}
void rtprint(TREENODE *subroot, int level){
    int i;

    if( subroot != NULL ){
    rtprint(subroot->left, level+1);
    for(i=0; i<level;i++){
        printf("\t");
    }
    vecprint(subroot->pvec);
    rtprint(subroot->right, level+1);
    }
}

TREENODE *talloc(){
    TREENODE *tp;

    tp = (TREENODE *) malloc( sizeof(TREENODE) );
    assert( tp != NULL);
    tp->left = NULL; /* added 2/7/97, to avoid runtime errors */
    tp->right = NULL; /* added 2/7/97, to avoid runtime errors */
    return(tp);
}

void tfree(TREENODE *tp){
    if(tp != NULL ){
    tfree(tp->left);
    tfree(tp->right);
    vecfree(tp->pvec);
    free( (char *) tp);
    }
}

void nnsearch(TREENODE *subroot, VECTOR *vp, int count){
    VECTOR *best;

    #ifdef DEBUG
    printf("nn tree search was called with \n");
    vecprint(vp);
    #endif

    best = rnnsearch(subroot, vp, (VECTOR *)NULL, 0);

    if( best != NULL){
    printf("nearest neighbor: ");
    vecprint(best);
    }else{
    printf("empty tree\n");
    }


}

float checkDist(VECTOR *vp1, VECTOR *vp2){
    float max, cur; /* squared result */
    NUMBER v1, v2;
    int pos, length;

    assert( (vp1->len) == (vp2->len) );
    length = vp1->len;
    max=0.0;
    for(pos=0; pos< length; pos++){
        v1 = (vp1->vec)[pos];
        v2 = (vp2->vec)[pos];
        cur = fabs(v1-v2);
        if(cur > max){
            max = fabs(v1-v2);
        }
    }
    return(max);
}

/* returns a pointer to the nn vector  */
/* 'best'       is the currently best match  */
/* 'level'      is the current level of the tree */
VECTOR *rnnsearch(TREENODE *subroot, VECTOR *vp, VECTOR *best, int level){

    NUMBER mindist;  /* the current best distance */
    NUMBER rootdist;  /* the distance from the root */
    int numdims;

    numdims = vp->len;
 
    /* if subroot is empty tree */
    if(subroot == NULL) {
        return(best);
    }

    rootdist = myvecdist2( vp, subroot->pvec);
    mindist = myvecdist2( vp, best);
    #ifdef DEBUG
    printf("rootdist %g mindist %g\n", rootdist, mindist);
    #endif

    if( rootdist < mindist){
        if((subroot->pvec)->flag==0){
            best = subroot->pvec;
            mindist = rootdist;
        }
    }

    /* check the subtrees - start from the most promising first */
    if( (vp->vec)[level] > ((subroot->pvec)->vec)[level]){
        /* the right subtree is more promising */
        best = rnnsearch( subroot->right, vp, best, (level+1)% numdims);
        mindist = myvecdist2(vp, best);

        /* now check the left subtree */
        if( (vp->vec)[level] <= ( ((subroot->pvec)->vec)[level] + mindist ) ){
            /* only then is the left subtree promising */
            /* notice that we use the UPDATED mindist */
            best = rnnsearch( subroot->left, vp, best, (level+1)% numdims);
        }
    }else{
        /* the left subtree is more promising */
        best = rnnsearch( subroot->left, vp, best, (level+1)% numdims);
        mindist = myvecdist2(vp, best);

        /* now check the right subtree */
        if( ( (vp->vec)[level] + mindist) > ((subroot->pvec)->vec)[level] ){
            best = rnnsearch( subroot->right, vp, best, (level+1)%numdims);
        }
    }

    return(best);
}

/* like vecdist2, but returns HUGE if one of the vectors is NULL */
NUMBER myvecdist2( VECTOR *vp1, VECTOR *vp2){
    if( (vp1 == NULL) || (vp2 == NULL) ) { return (MYHUGE) ;}
    else{ return( sqrt(vecdist2(vp1, vp2)) ) ; }
}


void rangesearch(TREENODE *subroot, VECTOR *vpLow, VECTOR *vpHigh){
    #ifdef DEBUG
    printf("tree range search was called with \n");
    vecprint(vpLow);
    vecprint(vpHigh);
    #endif
    rrangesearch(subroot, vpLow, vpHigh, 0);
}

void rrangesearch(TREENODE *subroot, VECTOR *vpLow, VECTOR *vpHigh, int level){
    #ifdef DEBUG
    printf("recursive tree range search was called with \n");
    vecprint(vpLow);
    vecprint(vpHigh);
    printf("level=%d\n", level);
    #endif

    /*** this part was disabled, for homework1 **/

    int numdims;

    if( subroot != NULL ){
        numdims = (subroot->pvec)->len;
        if( contains( vpLow, vpHigh, subroot->pvec ) ){
            vecprint(subroot->pvec);
        }
        if( (vpLow->vec)[level] <= ((subroot->pvec)->vec)[level] ){
            /* left branch can not be excluded */
            rrangesearch( subroot->left, vpLow, vpHigh, (level+1)% numdims);
        }
        if( (vpHigh->vec)[level] > ((subroot->pvec)->vec)[level] ){
            /* right branch can not be excluded */
            /* notice the '>' as opposed to '>=' */
            rrangesearch( subroot->right, vpLow, vpHigh, (level+1)% numdims);
        }
    }
    /*****/
    return;
}

/* returns TRUE if the interval contains the vp point */
/* The interval is CLOSED, ie it contains its endpoints */

BOOLEAN contains( VECTOR *vpLow, VECTOR *vpHigh, VECTOR *vp){
    BOOLEAN res;
    int i;
    int numdims;

    numdims = vp->len;

    for(i=0, res=TRUE; res && (i< numdims); i++){
       if( ( (vp->vec)[i] < (vpLow->vec)[i] ) ||
       ((vp->vec)[i] > (vpHigh->vec)[i]) ){
       res = FALSE;
       }
    }
    return(res);

}
// Tree creation if <= left > right
//   0 
// -1 2

/* Count method */
void countrange(TREENODE *subroot, VECTOR *vpLow, VECTOR *vpHigh){
	int res = rcountrange(subroot, vpLow, vpHigh, 0);
    printf("result=%d\n", res);
}

int rcountrange(TREENODE *subroot, VECTOR *vpLow, VECTOR *vpHigh, int level){

    int res = 0;
	int numdims;

    if( subroot != NULL ){
        numdims = (subroot->pvec)->len;
        if( contains( vpLow, vpHigh, subroot->pvec ) ){
        	res++;
        }
        if( (vpLow->vec)[level] <= ((subroot->pvec)->vec)[level] ){
            /* left branch can not be excluded */
            res += rcountrange( subroot->left, vpLow, vpHigh, (level+1)% numdims);
        }
        if( (vpHigh->vec)[level] > ((subroot->pvec)->vec)[level] ){
            /* right branch can not be excluded */
            /* notice the '>' as opposed to '>=' */
            res += rcountrange( subroot->right, vpLow, vpHigh, (level+1)% numdims);
        }
    }

    /*****/
    return res;
}