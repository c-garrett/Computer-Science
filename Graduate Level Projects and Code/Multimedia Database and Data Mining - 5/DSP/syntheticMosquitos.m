function [result] = syntheticMosquitos()
%syntheticMosquitos Summary of this function goes here
%   Detailed explanation goes here
setA = [];
setB = [];
for i=1:5
    filenameA = strcat('Test/', int2str(i), 'a.wav');
    filenameB = strcat('Test/',int2str(i), 'b.wav');
    
    [signal, Fs] = audioread(filenameA);
    X = fft(signal);
    abs_X = abs(X);
    [max1, max2] = findLocalMaximum(abs_X);
    setA = [setA;max1,max2];
    
    [signal, Fs] = audioread(filenameB);
    X = fft(signal);
    abs_X = abs(X);
    [max1, max2] = findLocalMaximum(abs_X);
    setB = [setB;max1,max2];
end

disp(setA);
disp(setB);
data = [setA setB];
csvwrite('syntheticMosquitosLocalMax.dat',data)

result = [setA setB];

end