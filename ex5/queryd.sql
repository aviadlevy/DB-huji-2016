select distinct c.cname
from contributor c
where not exists
	(select d1.aid
	from donated d1, contributor c1
	where c1.cid = d1.cid and c1.cname = 'Rothschild'
	and d1.aid not in
		(select d2.aid
		from donated d2
		where d2.cid = c.cid))
order by cname asc
