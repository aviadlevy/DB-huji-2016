select distinct c.cname
from contributor c, organization o1, organization o2, donated d1, donated d2
where ((c.cid = d1.cid and o1.aid = d1.aid and o1.oname = 'Latet') and
(c.cid = d2.cid and o2.aid = d2.aid and o2.oname = 'Elem'))
order by cname asc
