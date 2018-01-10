-- Find the duplicate movie titles list the year of the first and second movie
.header on
.mode column

drop view movies;

create view movies
	as 
	select N.title, N.year
	from nominations as N;

select distinct N1.title, N1.year as firstYear, N2.year as secondYear
from movies as N1, movies as N2
where N1.title = N2.title
and N1.year < N2.year
order by N1.year, N1.title;

