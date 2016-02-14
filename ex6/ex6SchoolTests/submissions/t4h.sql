DELETE FROM Actions cascade;
DELETE FROM AccountBalance cascade;
DELETE FROM Customers cascade;
TRUNCATE TABLE Customers RESTART IDENTITY cascade;
TRUNCATE TABLE Actions RESTART IDENTITY cascade;

insert into Customers(CustomerID, CustomerName, CustomerPassword, AccountStatus, Overdraft) values (1,'shani', 'pass', 'open', -5);
insert into Customers(CustomerID, CustomerName, CustomerPassword, AccountStatus, Overdraft) values (7,'miki', 'mouse', 'open', -50);
insert into Customers(CustomerID, CustomerName, CustomerPassword, AccountStatus, Overdraft) values (100,'dog', 'cool', 'open', -10);
insert into Customers(CustomerID, CustomerName, CustomerPassword, AccountStatus, Overdraft) values (1000,'moshe', 'cohen', 'close', -30);

insert into AccountBalance (AccountNum,Balance) values (1,0);
insert into AccountBalance (AccountNum,Balance) values (2,-25);
insert into AccountBalance (AccountNum,Balance) values (3,100);

insert into Actions (AccountNum, ActionName, ActionDate, Amount) values (3, 'receive', '2015-7-4',100);
insert into Actions (AccountNum, ActionName, ActionDate, Amount) values (2, 'payment', '2015-7-4',-25);
