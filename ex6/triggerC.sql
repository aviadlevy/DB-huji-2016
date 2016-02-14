CREATE OR REPLACE FUNCTION actionsDate() RETURNS TRIGGER AS $$
BEGIN
  IF new.ActionDate != current_date THEN
    new.ActionDate = current_date;
  END IF;
  RETURN new;
END;
$$ LANGUAGE plpgsql VOLATILE;

CREATE OR REPLACE FUNCTION savingsDate() RETURNS TRIGGER AS $$
BEGIN
  IF new.DepositDate!= current_date THEN
    new.DepositDate = current_date;
  END IF;
  RETURN new;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER triggerCAction
BEFORE INSERT ON actions
FOR EACH ROW EXECUTE procedure actionsDate();

CREATE TRIGGER triggerCSaving
BEFORE INSERT ON savings
FOR EACH ROW EXECUTE procedure savingsDate();
