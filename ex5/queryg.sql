--first case: Bill is contributor and donated
select d1.donation as MaxDonation
from donated d1, contributor c1
where d1.donation = (
	select max(d2.donation)
	from donated d2
	where d2.cid = c1.cid)
and c1.cname = 'Bill Gates'

union
--second case: Bill is contributor but he's cheap (not donated.. :) )
select 0 as MaxDonation
from contributor c2
where c2.cname = 'Bill Gates'AND 'Bill Gates' NOT IN (
	SELECT c1.cname
	FROM Donated d, Contributor c1
	WHERE d.cid = c1.cid)
	
union
--third case: Bill is not contributor at all...
select 0 as MaxDonation
from contributor c3
where 'Bill Gates' != all(
	select c4.cname
	from contributor c4
)
order by MaxDonation asc
