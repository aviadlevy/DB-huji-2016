CREATE or REPLACE FUNCTION closeAccountOverdraft() RETURNS trigger as $$
DECLARE
  CID INTEGER;
BEGIN
  IF old.balance < 0 THEN
    RAISE EXCEPTION 'You Cant close this account due to overdraft';
  ELSIF old.balance > 0 THEN
    INSERT INTO Actions(AccountNum, ActionName, ActionDate, Amount)
    VALUES(old.AccountNum, 'close', current_date, -old.Balance);
  END IF;
  RETURN old;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER triggerA
AFTER DELETE ON accountbalance
FOR EACH ROW EXECUTE PROCEDURE closeAccountOverdraft();