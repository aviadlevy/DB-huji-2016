DELETE FROM AccountBalance cascade;
DELETE FROM Customers cascade;
TRUNCATE TABLE Customers RESTART IDENTITY cascade;

insert into Customers(CustomerID, CustomerName, CustomerPassword, AccountStatus, Overdraft) values (1,'shani', 'pass', 'open', -5);
insert into Customers(CustomerID, CustomerName, CustomerPassword, AccountStatus, Overdraft) values (7,'miki', 'mouse', 'open', -50);
insert into Customers(CustomerID, CustomerName, CustomerPassword, AccountStatus, Overdraft) values (100,'dog', 'cool', 'open', -10);

insert into AccountBalance (AccountNum,Balance) values (1,0);
insert into AccountBalance (AccountNum,Balance) values (2,0);
insert into AccountBalance (AccountNum,Balance) values (3,0);
