CREATE or REPLACE FUNCTION createTop10() RETURNS trigger as $$
DECLARE
	minBalance int;
BEGIN
	DELETE FROM Top10Customers;
	INSERT INTO Top10Customers(
		SELECT * FROM AccountBalance
		WHERE Balance > 0
		ORDER BY Balance DESC
		LIMIT 10);
	SELECT MIN(Balance) into minBalance
	FROM Top10Customers;

	INSERT INTO Top10Customers(SELECT * FROM AccountBalance a
		                       WHERE Balance = minBalance and a.accountNum NOT IN
		                      (SELECT accountNum FROM Top10Customers));
	return NULL;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER triggerTop10
AFTER INSERT OR UPDATE OR DELETE ON accountbalance
FOR EACH STATEMENT EXECUTE procedure createTop10();
