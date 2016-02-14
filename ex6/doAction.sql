CREATE or REPLACE FUNCTION
doAction(CID INTEGER, AName VARCHAR, ADate DATE, AAmount REAL) RETURNS INTEGER as $$
DECLARE
  resultRecord RECORD;
  ANum INTEGER;
BEGIN
  SELECT * INTO resultRecord
  FROM customers c
  WHERE c.customerid = CID;
  IF NOT FOUND OR resultRecord.accountstatus = 'close' THEN
    RETURN -1;
  ELSIF (AName = 'receive' AND AAmount < 0) OR ((AName = 'payment' OR AName = 'saving' OR AName = 'close') AND AAmount > 0)THEN
    RETURN -1;
  ELSE
    UPDATE accountbalance
    SET balance = balance + AAmount
    WHERE accountnum = resultRecord.accountnum;
    INSERT INTO actions(AccountNum, ActionName, ActionDate, Amount ) VALUES (resultRecord.accountnum, AName, ADate, AAmount) RETURNING ActionNum into ANum;
    RETURN ANum;
  END IF;
END;
$$ LANGUAGE plpgsql;
