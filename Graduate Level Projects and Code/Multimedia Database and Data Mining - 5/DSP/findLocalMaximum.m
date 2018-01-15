function [ res1, res2 ] = findLocalMaximum( signal )
%FINDLOCALMAXIMUM Summary of this function goes here
%   Detailed explanation goes here
len = length(signal) / 2;
local_maximum = zeros(0, 2);
for i=101:len
    if signal(i - 1) <= signal(i) && signal(i) >= signal(i + 1)
        local_maximum = [local_maximum; i, signal(i)];
    end
end
max_amp_1 = 0;
max_freq_1 = 0;
for i=1:length(local_maximum)
    if local_maximum(i, 2) > max_amp_1
        max_amp_1 = local_maximum(i, 2);
        max_freq_1 = local_maximum(i, 1);
    end
end
max_amp_2 = 0;
max_freq_2 = 0;
for i=1:length(local_maximum)
    if abs(local_maximum(i, 1) - max_freq_1) <= 100
    % if local_maximum(i, 1) == max_freq_1
        continue;
    end
    if local_maximum(i, 2) > max_amp_2
        max_amp_2 = local_maximum(i, 2);
        max_freq_2 = local_maximum(i, 1);
    end
end
max_amp_3 = 0;
max_freq_3 = 0;
for i=1:length(local_maximum)
    if abs(local_maximum(i, 1) - max_freq_1) <= 100 || abs(local_maximum(i, 1) - max_freq_2) <= 100
    % if local_maximum(i, 1) == max_freq_1 || local_maximum(i, 1) == max_freq_2
        continue;
    end
    if local_maximum(i, 2) > max_amp_3
        max_amp_3 = local_maximum(i, 2);
        max_freq_3 = local_maximum(i, 1);
    end
end

res = sort([max_freq_1, max_freq_2, max_freq_3]);
res1 = res(1);
res2 = res(2);

end

