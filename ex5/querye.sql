select c.cname
from contributor c
where not exists (
	select d.aid
	from donated d
	where d.cid = c.cid and d.donation < 50000)
order by cname asc
