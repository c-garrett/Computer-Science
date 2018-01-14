edges=importdata("cit-Patents.txt");
size = max(edges(:));
edges_sparse = sparse(edges(:,1), edges(:,2), 1, size, size);
[U,S,V] = svds(edges_sparse,5);
singular_values = svds(edges_sparse,1);

% disp(S(1,1));
% disp(singular_values);

% Matrix N-Terms1 X M-Terms2
% SVD = U,S,V -> 
%   U -> N Terms1 X R Concepts
%   V -> M Terms2 X R Concepts
%   S -> Diagonal Matrix Strength of each Concept

U3 = U(:,3);
UM = max(abs(U3));
sources = find(abs(U3) > .01 * UM);

% disp(sources);

A = [5595021, 5595022, 5595023, 5595024, 5595025];
result_set = [];
value_set = [];

for elm = A     
    if(U(elm,3) ~= 0)
        result_set = [result_set, elm];
        value_set = [value_set, U(elm, 3)];
    end
end

disp(result_set);
disp(value_set);

A = [3697280, 4000146, 5087550, 5134000, 5175233];

result_set = [];
value_set = [];

for elm = A     
    if(V(elm,5) ~= 0)
        result_set = [result_set, elm];
        value_set = [value_set, V(elm, 5)];
    end
end

disp(result_set);
disp(value_set);

% Eigen Spoke plotting
plot(abs(U(:,3)),abs(U(:,5)),".")
axis([-.02 .3 -.02 .3])

