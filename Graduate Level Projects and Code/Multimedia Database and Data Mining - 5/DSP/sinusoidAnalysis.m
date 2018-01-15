function sinusoidAnalysis()
y = load('noiseWithSinusoid.mat');

wavelet_scaleogram(y, 15, 'sinusoid_scaleogram');

y = load('noiseWithMosquito.mat');
wavelet_scaleogram(y, 15, 'mosquito_scaleogram');
end