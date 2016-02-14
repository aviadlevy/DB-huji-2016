select o.oname
from organization o
where (4 <= (
		select count(*)
		from donated d
		where o.aid = d.aid))
order by oname asc
