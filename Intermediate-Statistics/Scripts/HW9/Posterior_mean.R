problem4 <- function(){
	p1boot <- rbeta(10000, 31, 21)
	p2boot <- rbeta(10000, 41, 11)
	tauboot <- p2boot - p1boot
	post_mean(tauboot)
	confidence_interval(tauboot)
}

post_mean <- function(tauboot){
	print("The posterior mean")
	posterior_mean <- mean(tauboot)
	print(posterior_mean)
}

confidence_interval <- function(tauboot){
	print("The posterior 90% confidence interval")
	lower <- quantile(tauboot, 0.05)
	upper <- quantile(tauboot, 0.95)
	print(paste(lower,upper))
}

problem4()