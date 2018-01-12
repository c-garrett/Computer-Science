data = load("mystery.dat");

s = svd(data);
[U,S,V] = svd(data);
dim = 2;

U = U(:,1:2);
V = V(:,1:2);
V = V';

data_points = [];

for i = 1:rows(data)
  data_point = data(i,:)';
  temp = V*data_point;
  temp = temp';
  data_points = [data_points;temp];
endfor

# scatter(data_points(:,1),data_points(:,2))

[pval,G,U] = grubbstest(data_points(:,1));