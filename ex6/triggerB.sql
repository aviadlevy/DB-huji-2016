CREATE or REPLACE FUNCTION withdrawOverdraft() RETURNS trigger as $$
DECLARE
  ODraft INTEGER;
BEGIN
  SELECT overdraft INTO ODraft
  FROM customers c
  WHERE c.accountnum = new.accountnum;
  IF new.balance < ODraft THEN
    RAISE EXCEPTION 'You cant withdraw more money due to overdraft';
  END IF;
  RETURN new;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER triggerB
BEFORE UPDATE ON accountbalance
FOR EACH ROW EXECUTE PROCEDURE withdrawOverdraft();