-- find pairs of people who compete too often against each other
.header on
.mode column

-- determine clashes ->
-- when people are nominated same year
-- for the same category
-- 3 or more times

drop view competitors;

create view competitors
	as
	select N1.personId as p1, N2.personId as p2,
	count(*) as num_clashes
	from nominations as N1, nominations as N2
	where N1.catId = N2.catId
	and N1.year = N2.year
	and N1.personId <> N2.personId
	group by N1.personId, N2.personId;

drop view clashes;

create view clashes
	as
	select N1.p1, N1.p2, N1.num_clashes 
	from competitors as N1
	where num_clashes > 2;

select P1.lastName, P1.firstName, P2.lastName, P2.firstName, C1.num_clashes 
from people as P1, people as P2, clashes as C1
	where P1.personId = C1.p1
	and P2.personId = C1.p2
	and P1.lastName < P2.lastName
order by num_clashes desc, P1.lastName, P1.firstName, P2.lastName, P2.firstName;

create view competitors1 as 
	select N1.personId as pid1, N2.personId as pid2,
		count(*) as num_clashes
	from nominations as N1, nominations as N2
	where N1.personId <> N2.personId
		and N1.catId = N2.catId
		and N1.year = N2.year
	group by N1.personId, N2.personId;

select P1.lastName, P1.firstName,
	P2.lastName, P2.firstName,
	C.num_clashes
from people as P1, people as P2, competitors1 as C
where P1.personId = C.pid1
	and P2.personId = C.pid2
	and P1.lastName < P2.lastName
	and C.num_clashes > 2
	order by C.num_clashes desc, P1.lastName, P1.firstName,
		P2.lastName, P2.firstName;