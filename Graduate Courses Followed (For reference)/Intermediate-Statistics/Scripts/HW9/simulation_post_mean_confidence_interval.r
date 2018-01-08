problem4e <- function(){
	set.seed(1)
	p1boot <- rbeta(10000, 31, 21)
	p2boot <- rbeta(10000, 41, 11)
	psiboot <- log((p1boot/(1-p1boot)) / (p2boot / (1-p2boot)))
	posteriormean <- mean(psiboot)
	print(posteriormean)
	lower <- quantile(psiboot, .05)
	upper <- quantile(psiboot, .95)
	print(paste(lower,upper))
}

problem4e()