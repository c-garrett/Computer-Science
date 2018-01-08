library(MASS)

simulate = function(n,p,mu,N){
	bayes = rep(0,N)
	freq = rep(0,N)
	Sigma = diag(rep(1,p))
	for(i in 1:N){
		x = mvrnorm(n,mu,Sigma)
		xbar = apply(x,2,mean)
		freq[i] = sum(xbar^2) - (p/n)
		bayes[i] = sum(xbar^2) + (p/n)
	}
	return(list(freq=freq,bayes=bayes))
} 

n  = 10
p  = 100
mu = rep(0,p)
N  = 1000

out = simulate(n,p,mu,N)

print(out)

pdf("plot.pdf")

hist(out$freq,nclass=25)
hist(out$bayes,nclass=25)