INSERT INTO Contributor values(1, 'contributor1');
INSERT INTO Contributor values(2, 'contributor2');
INSERT INTO Contributor values(3, 'contributor3');

INSERT INTO Organization values(10, 'Latet', 1996);
INSERT INTO Organization values(20, 'Elem', 1983);
INSERT INTO Organization values(30, 'organization3', 1990);
INSERT INTO Organization values(40, 'organization4', 1984);
INSERT INTO Organization values(50, 'organization5', 1989);

INSERT INTO Donated values(1, 10, 1000);
INSERT INTO Donated values(1, 20, 2000);

INSERT INTO Donated values(2, 20, 2000);
INSERT INTO Donated values(2, 50, 2000);
INSERT INTO Donated values(2, 10, 2000);

INSERT INTO Donated values(3, 40, 1000);
INSERT INTO Donated values(3, 20, 4000);
INSERT INTO Donated values(3, 30, 5000);
INSERT INTO Donated values(3, 10, 8000);
INSERT INTO Donated values(3, 50, 9000);