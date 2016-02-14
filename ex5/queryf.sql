select c.cid
from contributor c
where (
	select sum(d1.donation)
	from donated d1
	where c.cid = d1.cid) 
	>= all(
	select sum(d2.donation)
	from donated d2
	group by d2.cid)
order by cid asc
