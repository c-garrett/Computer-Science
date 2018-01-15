function [coords] = plotMosquito()
%PLOTMOSQUITO Summary of this function goes here
%   Detailed explanation goes here
coords = zeros(1100, 2);
for i=1:1000
    if i >= 1 && i <= 9
        filename = strcat('Train/0000', int2str(i), '.wav');
    elseif i >= 10 && i <= 99
        filename = strcat('Train/000', int2str(i), '.wav');
    elseif i >= 100 && i <= 999
        filename = strcat('Train/00', int2str(i), '.wav');
    else
        filename = strcat('Train/0', int2str(i), '.wav');
    end
    [signal, Fs] = audioread(filename);
    X = fft(signal);
    abs_X = abs(X);
    % abs_X = smoothts(abs(X), 'b', 100);
    [max1, max2] = findLocalMaximum(abs_X);
    coords(i, :) = [max1, max2];
end

syntheticCords = syntheticMosquitos();
setA = [syntheticCords(:,1), syntheticCords(:,2)];
setB = [syntheticCords(:,3), syntheticCords(:,4)];

csvwrite('mosquitosMax.dat',coords)

subplot(3,1,1)
plot(coords(:,1),coords(:,2),'.');
axis([0 3000 0 3000])
title('Mosquitos')

subplot(3,1,2)
plot(setA(:,1),setA(:,2),'.');
axis([0 3000 0 3000])
title('SyntheticA')

subplot(3,1,3)
plot(setB(:,1),setB(:,2),'.');
axis([0 3000 0 3000])
title('Synthetic1B')

