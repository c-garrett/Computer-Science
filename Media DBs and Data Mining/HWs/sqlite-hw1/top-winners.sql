.header on
.mode column

/* List all the people who have won three or more times */
select P.personId, P.firstName, P.lastName, count(*) as numWins 
from nominations as N, people as P
where N.personId = P.personId 
and won = 't'
group by P.personId
having numWins > 2
order by numWins desc, P.lastName, P.firstName;

/* alternative solution creating a view */

drop view winners;
create view winners
	as 
	select P.personId, P.firstName, P.lastName, N.won
	from nominations as N, people as P
	where P.personId = N.personId;

.schema winners

-- select * from winners;

select personId, firstName, lastName, count(*) as numWins from winners
where won = 't'
group by personId
having numWins > 2
order by numWins desc, lastName, firstName;