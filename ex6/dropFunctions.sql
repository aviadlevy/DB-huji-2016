DROP FUNCTION newcustomer(cID INT , CName VARCHAR, CPassword VARCHAR , Overd REAL );
DROP FUNCTION doAction(CID INT, AcName VARCHAR, AcDate DATE, AmountM REAL);
DROP FUNCTION closecustomer(CID INT);
DROP FUNCTION newSaving(CID INT , Depos REAL, DeposDate DATE, NumYears INT, theInterest REAL) ;
-- added cascade because the triggers depends on the tables, 
-- and maybe case the table not dropped yet by user..
DROP FUNCTION actionsdate() cascade;
DROP FUNCTION closeaccountoverdraft() cascade;
DROP FUNCTION savingsdate() cascade;
DROP FUNCTION withdrawoverdraft() cascade;
DROP FUNCTION createTop10() cascade;
