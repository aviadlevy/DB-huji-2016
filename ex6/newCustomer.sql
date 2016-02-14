CREATE or REPLACE FUNCTION newCustomer(CID INTEGER, CName VARCHAR, CPassword VARCHAR, Odraft REAL) RETURNS INTEGER as $$
DECLARE
  cnt INTEGER;
  account INTEGER;
  status VARCHAR;
BEGIN
  SELECT count(*) into cnt
  FROM customers c
  WHERE c.customerid = CID;
  IF cnt = 0 THEN
    INSERT INTO customers(CustomerId, CustomerName, CustomerPassword, AccountStatus, Overdraft)
    VALUES( CID, CName, CPassword, 'open', Odraft);
    SELECT accountnum into account
    FROM customers c1
    WHERE c1.customerid = CID;
    INSERT INTO accountbalance VALUES(account, 0);
    RETURN account;
  ELSE
    SELECT accountstatus into status
    FROM customers c
    WHERE c.customerid = CID;
    IF status = 'open' THEN
      return -1;
    ELSE
      UPDATE customers
      SET customername = CName, customerpassword = CPassword, overdraft = Odraft, accountstatus = 'open'
      WHERE customers.customerid = CID;
      SELECT accountnum into account FROM customers c WHERE c.customerid = CID;
      INSERT INTO accountbalance VALUES(account, 0);
      RETURN account;
    END IF;
  END IF;
END;
$$ LANGUAGE plpgsql;

