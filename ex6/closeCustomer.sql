CREATE or REPLACE FUNCTION
closeCustomer(CID INTEGER) RETURNS INTEGER as $$
DECLARE
  resultRecord RECORD;
BEGIN
  SELECT *
  INTO resultRecord
  FROM customers c
  WHERE CID = c.customerid;
  IF NOT FOUND OR resultRecord.accountstatus = 'close' THEN
    RETURN -1;
  ELSE

    DELETE FROM accountbalance
    WHERE accountnum = resultRecord.accountnum;
    DELETE FROM savings
    WHERE accountnum = resultRecord.accountnum;
    DELETE FROM top10customers
    WHERE accountnum = resultRecord.accountnum;

    UPDATE customers
    SET accountstatus = 'close'
    WHERE customerid = CID;

    RETURN resultRecord.accountnum;
  END IF;
END;
$$ LANGUAGE plpgsql;