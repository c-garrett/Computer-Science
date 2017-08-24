confidence <- function(){
	print("Obtaining 90% confidence interval")
	set.seed(1)
	Xboot <- rbinom(10000,50,0.6)
	Yboot <- rbinom(10000,50,0.8)
	tauboot <- Yboot/50 - Xboot/50
	seboot <- sqrt(sum((tauboot - 0.2)^2)/10000) # 0.08985388
	lower <- 0.2 - 1.645 * seboot # 0.05219037
	upper <- 0.2 + 1.645 * seboot # 0.3478096
	print(paste(lower,upper))
}

confidence()