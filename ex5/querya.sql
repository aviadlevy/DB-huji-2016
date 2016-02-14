select distinct cname
from contributor, organization, donated
where organization.aid = donated.aid and contributor.cid = donated.cid and organization.estYear < 1980
order by cname asc
