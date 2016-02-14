CREATE or REPLACE FUNCTION
newSaving(CID INTEGER, SDeposit REAL, SDate DATE, SNumOfYears INTEGER, SInterst REAL) RETURNS INTEGER as $$
DECLARE
  resultRecord RECORD;
  returnSavingNum INTEGER;
BEGIN
  SELECT *
  INTO resultRecord
  FROM customers c
  WHERE CID = c.customerid;
  IF NOT FOUND OR resultRecord.accountstatus = 'close'
  THEN
    RETURN -1;
  ELSE
    PERFORM doaction(CID, 'saving', SDate, -SDeposit);
    INSERT INTO savings (accountnum, deposit, depositdate, numofyears, interest)
    VALUES (resultRecord.accountnum, SDeposit, SDate, SNumOfYears, SInterst);
    SELECT savingnum INTO returnSavingNum
    FROM savings
    WHERE accountnum = resultRecord.accountnum;
    RETURN returnSavingNum;
  END IF;
END;
$$ LANGUAGE plpgsql;

