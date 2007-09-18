-- Studenten
CREATE TABLE PREFIX_student
(
  MatrikelNo      INT NOT NULL,
  FirstName       VARCHAR(100),
  LastName        VARCHAR(100),
  Email           VARCHAR(100),
  DateEdit        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(MatrikelNo)
);

-- Tutor
CREATE TABLE PREFIX_tutor
(
  TutorID         INT,
  FirstName       VARCHAR(100),
  LastName        VARCHAR(100),
  DateEdit        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(FirstName, LastName),  
  PRIMARY KEY(TutorID)
);

-- Veranstaltungen
CREATE TABLE PREFIX_event
(
  EventID       INT NOT NULL,
  TutorID       INT DEFAULT 0,
  EventName     VARCHAR(100),
  Description   VARCHAR(100),
  DateEdit      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  
  PRIMARY KEY(EventID),
  foreign key(TutorID) references PREFIX_tutor(TutorID) on delete CASCADE on update CASCADE
);

-- Definition von Aufgaben (projects)
CREATE TABLE PREFIX_project
(
 EventID      INT NOT NULL, -- ID des zugehoerigen Events
 ProjectID    INT NOT NULL,
 Description  VARCHAR(100), -- Beschreibung des Projekts
 DateEdit     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 Deadline     TIMESTAMP, 
 PRIMARY KEY(ProjectID),
 foreign key(EventID) references PREFIX_event(EventID)  on delete CASCADE on update CASCADE
);

-- Gruppen
CREATE TABLE PREFIX_group
(
  GroupID       INT NOT NULL,
  EventID       INT NOT NULL,
  Description   VARCHAR(100),
  DateEdit      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(GroupID),
  foreign key(EventID) references PREFIX_event(EventID) on delete CASCADE on update CASCADE
);

-- Zuordnung von Studenten zu Gruppen
CREATE TABLE PREFIX_groupToStudent
(
  GroupID         INT NOT NULL,
  MatrikelNo      INT NOT NULL,
  DateEdit        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(GroupID, MatrikelNo),
  foreign key(MatrikelNo) references PREFIX_student(MatrikelNo) on delete CASCADE on update CASCADE,
  foreign key(GroupID) references PREFIX_group(GroupID) on delete CASCADE on update CASCADE
);

-- Zuordnung von Tutoren zu Gruppen
CREATE TABLE PREFIX_groupToTutor
(
  GroupID     INT NOT NULL,
  TutorID     INT NOT NULL,
  DateEdit    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY(GroupID,TutorID),
  foreign key(GroupID) references PREFIX_group(GroupID) on delete CASCADE on update CASCADE,
  foreign key(TutorID) references PREFIX_tutor(TutorID) on delete CASCADE on update CASCADE
);

-- Zuordnung von Gruppen zu Veranstaltungen
-- CREATE TABLE PREFIX_eventToGroup
-- (
--  EventID  INT NOT NULL,
--  GroupID  INT NOT NULL,
--  DateEdit        TIMESTAMP DEFAULT now(),
  
--  PRIMARY KEY(EventID, GroupID)
-- );

-- statt obiger Tabelle werden Studenten direkt nochmal Events zugeordnet
CREATE TABLE PREFIX_eventToStudent
(
  EventID       INT NOT NULL,
  MatrikelNo    INT NOT NULL,
  DateEdit      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  
  PRIMARY KEY(EventID,MatrikelNo),
  foreign key(EventID) references PREFIX_event(EventID) on delete CASCADE on update CASCADE,
  foreign key(MatrikelNo) references PREFIX_student(MatrikelNo) on delete CASCADE on update CASCADE
);



-- Zuordnung von Aufgaben zu Studenten
CREATE TABLE PREFIX_projectToStudent
(
 ProjectID INT NOT NULL,
 MatrikelNo INT NOT NULL,
 DateEdit  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 
 PRIMARY KEY(ProjectID, MatrikelNo),
 foreign key(ProjectID) references PREFIX_project(ProjectID) on delete CASCADE on update CASCADE,
 foreign key(MatrikelNo) references PREFIX_student(MatrikelNo) on delete CASCADE on update CASCADE
);

-- Noten pro Event, Project und Student
CREATE TABLE PREFIX_results
(
 MatrikelNo INT NOT NULL,
 Result  VARCHAR(255), -- damit auch etwas wie "bestanden" moeglich ist
 ProjectID INT NOT NULL,
 DateEdit  TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
 PRIMARY KEY(MatrikelNo,ProjectID),
 foreign key(MatrikelNo) references PREFIX_student(MatrikelNo) on delete CASCADE on update CASCADE,
 foreign key(ProjectID) references PREFIX_project(ProjectID) on delete CASCADE on update CASCADE
);
-- Terminplaner --------------------------------------------
-- Termine fuer Events
CREATE TABLE PREFIX_term
(
 TermID  INT NOT NULL,
 EventID  INT NOT NULL,
 DateEdit  TIMESTAMP NOT NULL,
 
 PRIMARY KEY(TermID),
 foreign key(EventID) references PREFIX_event(EventID) on delete CASCADE on update CASCADE
);
-- Wer hat an den Terminen teilgenommen?
CREATE TABLE PREFIX_termToStudent
(
 TermID  INT NOT NULL,
 MatrikelNo INT NOT NULL,
 
 PRIMARY KEY(TermID,MatrikelNo),
 foreign key(MatrikelNo) references PREFIX_student(MatrikelNo) on delete CASCADE on update CASCADE,
 foreign key(TermID) references PREFIX_term(TermID) on delete CASCADE on update CASCADE
);

-- Standardwert
Insert into PREFIX_tutor (TutorID, FirstName, LastName) Values (0,'N.N.', 'N.N.');



-- ###################################################################################################
-- Beispieldaten
-- ###################################################################################################

-- Tutor anlegen
Insert into PREFIX_tutor (TutorID, FirstName, LastName) Values (4,'Frau', 'Frey');

Insert into PREFIX_tutor (TutorID, FirstName, LastName) Values (1,'Manfred', 'Siekmann');
Insert into PREFIX_tutor (TutorID, FirstName, LastName) Values (2,'Frank', 'Thiesing');
Insert into PREFIX_tutor (TutorID, FirstName, LastName) Values (3,'Jürgen', 'Biermann');

-- Studenten anlegen
Insert into PREFIX_student(MatrikelNo, FirstName, LastName, Email ) Values (320001, 'Kai', 'Ritterbusch', 'kai.ritterbusch@fh-osnabrueck.de');
Insert into PREFIX_student(MatrikelNo, FirstName, LastName, Email ) Values (320002, 'Andreas', 'Depping', 'andreas.depping@fh-osnabrueck.de');
Insert into PREFIX_student(MatrikelNo, FirstName, LastName, Email ) Values (320003, 'Philipp', 'Rollwage', 'philipp.rollwage@fh-osnabrueck.de');
Insert into PREFIX_student(MatrikelNo, FirstName, LastName, Email ) Values (320004, 'Jan', 'Uhlenbrok', 'jan.uhlenbrock@fh-osnabrueck.de');
Insert into PREFIX_student(MatrikelNo, FirstName, LastName, Email ) Values (320005, 'Alex', 'Kailbach', 'alex.kailbach@fh-osnabrueck.de');
Insert into PREFIX_student(MatrikelNo, FirstName, LastName, Email ) Values (320006, 'Daniela', 'Schwerdt', 'daniela.schwerdt@fh-osnabrueck.de');
Insert into PREFIX_student(MatrikelNo, FirstName, LastName, Email ) Values (320007, 'Tanja', 'Baumgart', 'tanja.baumgart@fh-osnabrueck.de');
Insert into PREFIX_student(MatrikelNo, FirstName, LastName, Email ) Values (320008, 'Karen', 'Baumgart', 'karen.baumgart@fh-osnabrueck.de');
Insert into PREFIX_student(MatrikelNo, FirstName, LastName, Email ) Values (320009, 'Christian', 'Lins', 'christian.lins@fh-osnabrueck.de');
Insert into PREFIX_student(MatrikelNo, FirstName, LastName, Email ) Values (320010, 'Annemarie', 'Meier', 'annemarie.meier@fh-osnabrueck.de');
Insert into PREFIX_student(MatrikelNo, FirstName, LastName, Email ) 
Values (320011, 'Karl Heinz', 'Müller', 'karl-heinz.mueller@fh-osnabrueck.de');
Insert into PREFIX_student(MatrikelNo, FirstName, LastName, Email ) Values (320012, 'Karl ', 'Schulz', 'karl.schulz@fh-osnabrueck.de');
Insert into PREFIX_student(MatrikelNo, FirstName, LastName, Email ) Values (320013, 'Franz', 'Fischer', 'franz.fischer@fh-osnabrueck.de');
Insert into PREFIX_student(MatrikelNo, FirstName, LastName, Email ) Values (320014, 'Paula', 'Schneider', 'paula.schneider@fh-osnabrueck.de');
Insert into PREFIX_student(MatrikelNo, FirstName, LastName, Email ) Values (320015, 'Johanna', 'Doe', 'johanna.doe@fh-osnabrueck.de');
Insert into PREFIX_student(MatrikelNo, FirstName, LastName, Email ) Values (320016, 'Jutta', 'Kleinschmidt', 'jutta.kleinschmidt@fh-osnabrueck.de');
Insert into PREFIX_student(MatrikelNo, FirstName, LastName, Email ) Values (320017, 'Tobias', 'Hoffmann', 'tobias.hoffmann@fh-osnabrueck.de');
Insert into PREFIX_student(MatrikelNo, FirstName, LastName, Email ) Values (320018, 'Erich Maria Hans Jr', 'Remarque - Schachschneider', 'erich.schachschneider@fh-osnabrueck.de');
Insert into PREFIX_student(MatrikelNo, FirstName, LastName, Email ) Values (320019, 'Hellmut', 'Walter', 'hellmut.walter@fh-osnabrueck.de');
Insert into PREFIX_student(MatrikelNo, FirstName, LastName, Email ) Values (320020, 'Won', 'ToChun', 'won.tochun@fh-osnabrueck.de');
Insert into PREFIX_student(MatrikelNo, FirstName, LastName, Email ) Values (320021, 'Hans Dieter', 'Lee', 'hans-dieter.lee@fh-osnabrueck.de');
Insert into PREFIX_student(MatrikelNo, FirstName, LastName, Email ) Values (320022, 'Chan', 'Wing', 'chan.wing@fh-osnabrueck.de');
Insert into PREFIX_student(MatrikelNo, FirstName, LastName, Email ) Values (320023, 'Schack', 'Schirack', 'schack.schirack@fh-osnabrueck.de');

-- Events einfuegen
INSERT INTO PREFIX_event(EventID,TutorID,EventName,Description) VALUES (0,3,'Datenbanken','Praktikum von Prof. Biermann');
INSERT INTO PREFIX_event(EventID,TutorID,EventName,Description) VALUES (1,1,'Software-Engineering','Praktikum von Prof. Siekmann');
INSERT INTO PREFIX_event(EventID,TutorID,EventName,Description) VALUES (2,4,'Datenbanken','Praktikum von Fr. Frey');
INSERT INTO PREFIX_event(EventID,EventName,Description) VALUES (3,'Bildverarbeitung','');
INSERT INTO PREFIX_event(EventID,EventName,Description) VALUES (4,'Objektorientierte Programmierung','');
INSERT INTO PREFIX_event(EventID,EventName,Description) VALUES (5,'Einführung in die Informatik','');
INSERT INTO PREFIX_event(EventID,EventName,Description) VALUES (6,'Kryptologie','');
INSERT INTO PREFIX_event(EventID,EventName,Description) VALUES (7,'Akustik und Optik','');
INSERT INTO PREFIX_event(EventID,EventName,Description) VALUES (8,'Elektrotechnik','');

-- Termine anlegen
INSERT INTO PREFIX_term(TermID,EventID,DateEdit) VALUES(0,0,'2007-10-2 12:15:00');
INSERT INTO PREFIX_term(TermID,EventID,DateEdit) VALUES(1,0,'2007-10-10 14:20:00');
INSERT INTO PREFIX_term(TermID,EventID,DateEdit) VALUES(2,0,'2007-10-13 13:45:00');
INSERT INTO PREFIX_term(TermID,EventID,DateEdit) VALUES(3,0,'2007-10-14 12:00:00');
INSERT INTO PREFIX_term(TermID,EventID,DateEdit) VALUES(4,0,'2007-10-15 12:15:00');
INSERT INTO PREFIX_term(TermID,EventID,DateEdit) VALUES(5,0,'2007-10-16 12:15:00');
INSERT INTO PREFIX_term(TermID,EventID,DateEdit) VALUES(6,0,'2007-10-17 12:15:00');
INSERT INTO PREFIX_term(TermID,EventID,DateEdit) VALUES(7,0,'2007-10-18 12:15:00');
INSERT INTO PREFIX_term(TermID,EventID,DateEdit) VALUES(8,0,'2007-10-19 12:15:00');
INSERT INTO PREFIX_term(TermID,EventID,DateEdit) VALUES(9,0,'2007-10-20 12:15:00');
INSERT INTO PREFIX_term(TermID,EventID,DateEdit) VALUES(10,0,'2007-10-21 12:15:00');
INSERT INTO PREFIX_term(TermID,EventID,DateEdit) VALUES(11,0,'2007-10-22 12:15:00');
INSERT INTO PREFIX_term(TermID,EventID,DateEdit) VALUES(12,0,'2007-10-23 12:15:00');
INSERT INTO PREFIX_term(TermID,EventID,DateEdit) VALUES(13,0,'2007-10-24 12:15:00');
INSERT INTO PREFIX_term(TermID,EventID,DateEdit) VALUES(14,0,'2007-10-25 12:15:00');
INSERT INTO PREFIX_term(TermID,EventID,DateEdit) VALUES(15,0,'2007-10-26 12:15:00');
INSERT INTO PREFIX_term(TermID,EventID,DateEdit) VALUES(16,0,'2007-10-27 12:15:00');
INSERT INTO PREFIX_term(TermID,EventID,DateEdit) VALUES(17,0,'2007-10-28 12:15:00');
INSERT INTO PREFIX_term(TermID,EventID,DateEdit) VALUES(18,0,'2007-10-29 12:15:00');
INSERT INTO PREFIX_term(TermID,EventID,DateEdit) VALUES(19,0,'2007-10-30 12:15:00');
INSERT INTO PREFIX_term(TermID,EventID,DateEdit) VALUES(20,0,'2007-10-3 12:15:00');
INSERT INTO PREFIX_term(TermID,EventID,DateEdit) VALUES(21,0,'2007-10-4 12:15:00');
INSERT INTO PREFIX_term(TermID,EventID,DateEdit) VALUES(22,0,'2007-10-5 12:15:00');
INSERT INTO PREFIX_term(TermID,EventID,DateEdit) VALUES(23,0,'2007-10-6 12:15:00');
INSERT INTO PREFIX_term(TermID,EventID,DateEdit) VALUES(24,0,'2007-10-7 12:15:00');
INSERT INTO PREFIX_term(TermID,EventID,DateEdit) VALUES(25,0,'2007-10-8 12:15:00');
INSERT INTO PREFIX_term(TermID,EventID,DateEdit) VALUES(26,1,'2007-10-11 14:25:00');

-- Termine den Studenten hinzufuegen
INSERT INTO PREFIX_termToStudent(TermID,MatrikelNo) VALUES(0,320001);
INSERT INTO PREFIX_termToStudent(TermID,MatrikelNo) VALUES(1,320001);
INSERT INTO PREFIX_termToStudent(TermID,MatrikelNo) VALUES(2,320001);
INSERT INTO PREFIX_termToStudent(TermID,MatrikelNo) VALUES(3,320001);
INSERT INTO PREFIX_termToStudent(TermID,MatrikelNo) VALUES(4,320001);
INSERT INTO PREFIX_termToStudent(TermID,MatrikelNo) VALUES(5,320002);
INSERT INTO PREFIX_termToStudent(TermID,MatrikelNo) VALUES(0,320003);
INSERT INTO PREFIX_termToStudent(TermID,MatrikelNo) VALUES(0,320004);

-- Gruppen anlegen
INSERT INTO PREFIX_group(GroupID,EventID,Description) VALUES (1,0,'Gruppe 2 v. Datenbanken Biermann');
INSERT INTO PREFIX_group(GroupID,EventID,Description) VALUES (2,1,'Gruppe 1 v. SE Siekmann');
INSERT INTO PREFIX_group(GroupID,EventID,Description) VALUES (3,0,'Gruppe 3 v. Datenbanken Biermann');
INSERT INTO PREFIX_group(GroupID,EventID,Description) VALUES (4,0,'Gruppe 1 v. Datenbanken Biermann');

-- Veranstaltung den Studenten hinzufuegen
INSERT INTO PREFIX_eventToStudent(EventID,MatrikelNo) VALUES(0,320001);
INSERT INTO PREFIX_eventToStudent(EventID,MatrikelNo) VALUES(0,320002);
INSERT INTO PREFIX_eventToStudent(EventID,MatrikelNo) VALUES(0,320003);
INSERT INTO PREFIX_eventToStudent(EventID,MatrikelNo) VALUES(0,320004);
INSERT INTO PREFIX_eventToStudent(EventID,MatrikelNo) VALUES(0,320005);
INSERT INTO PREFIX_eventToStudent(EventID,MatrikelNo) VALUES(0,320006);
INSERT INTO PREFIX_eventToStudent(EventID,MatrikelNo) VALUES(0,320007);
INSERT INTO PREFIX_eventToStudent(EventID,MatrikelNo) VALUES(0,320008);
INSERT INTO PREFIX_eventToStudent(EventID,MatrikelNo) VALUES(0,320009);
INSERT INTO PREFIX_eventToStudent(EventID,MatrikelNo) VALUES(0,320010);
INSERT INTO PREFIX_eventToStudent(EventID,MatrikelNo) VALUES(0,320011);
INSERT INTO PREFIX_eventToStudent(EventID,MatrikelNo) VALUES(0,320012);
INSERT INTO PREFIX_eventToStudent(EventID,MatrikelNo) VALUES(0,320013);
INSERT INTO PREFIX_eventToStudent(EventID,MatrikelNo) VALUES(0,320014);
INSERT INTO PREFIX_eventToStudent(EventID,MatrikelNo) VALUES(0,320015);
INSERT INTO PREFIX_eventToStudent(EventID,MatrikelNo) VALUES(0,320016);
INSERT INTO PREFIX_eventToStudent(EventID,MatrikelNo) VALUES(0,320017);
INSERT INTO PREFIX_eventToStudent(EventID,MatrikelNo) VALUES(0,320018);
INSERT INTO PREFIX_eventToStudent(EventID,MatrikelNo) VALUES(0,320019);
INSERT INTO PREFIX_eventToStudent(EventID,MatrikelNo) VALUES(0,320020);
INSERT INTO PREFIX_eventToStudent(EventID,MatrikelNo) VALUES(0,320021);
INSERT INTO PREFIX_eventToStudent(EventID,MatrikelNo) VALUES(0,320022);
INSERT INTO PREFIX_eventToStudent(EventID,MatrikelNo) VALUES(0,320023);
INSERT INTO PREFIX_eventToStudent(EventID,MatrikelNo) VALUES(1,320004);
INSERT INTO PREFIX_eventToStudent(EventID,MatrikelNo) VALUES(1,320005);
INSERT INTO PREFIX_eventToStudent(EventID,MatrikelNo) VALUES(1,320006);
INSERT INTO PREFIX_eventToStudent(EventID,MatrikelNo) VALUES(1,320007);
INSERT INTO PREFIX_groupToStudent(GroupID,MatrikelNo) VALUES(4,320001);
INSERT INTO PREFIX_groupToStudent(GroupID,MatrikelNo) VALUES(4,320008);
INSERT INTO PREFIX_groupToStudent(GroupID,MatrikelNo) VALUES(4,320009);
INSERT INTO PREFIX_groupToStudent(GroupID,MatrikelNo) VALUES(4,320010);
INSERT INTO PREFIX_groupToStudent(GroupID,MatrikelNo) VALUES(4,320011);
INSERT INTO PREFIX_groupToStudent(GroupID,MatrikelNo) VALUES(1,320003);
INSERT INTO PREFIX_groupToStudent(GroupID,MatrikelNo) VALUES(1,320004);
INSERT INTO PREFIX_groupToStudent(GroupID,MatrikelNo) VALUES(1,320005);
INSERT INTO PREFIX_groupToStudent(GroupID,MatrikelNo) VALUES(1,320006);
INSERT INTO PREFIX_groupToStudent(GroupID,MatrikelNo) VALUES(1,320007);
INSERT INTO PREFIX_groupToStudent(GroupID,MatrikelNo) VALUES(2,320002);
INSERT INTO PREFIX_groupToStudent(GroupID,MatrikelNo) VALUES(2,320001);
INSERT INTO PREFIX_groupToStudent(GroupID,MatrikelNo) VALUES(3,320012);
INSERT INTO PREFIX_groupToStudent(GroupID,MatrikelNo) VALUES(3,320013);
INSERT INTO PREFIX_groupToStudent(GroupID,MatrikelNo) VALUES(3,320014);
INSERT INTO PREFIX_groupToStudent(GroupID,MatrikelNo) VALUES(3,320015);
INSERT INTO PREFIX_groupToStudent(GroupID,MatrikelNo) VALUES(3,320016);
INSERT INTO PREFIX_groupToStudent(GroupID,MatrikelNo) VALUES(3,320017);
INSERT INTO PREFIX_groupToStudent(GroupID,MatrikelNo) VALUES(3,320018);
INSERT INTO PREFIX_groupToStudent(GroupID,MatrikelNo) VALUES(3,320019);
INSERT INTO PREFIX_groupToStudent(GroupID,MatrikelNo) VALUES(3,320020);
INSERT INTO PREFIX_groupToStudent(GroupID,MatrikelNo) VALUES(3,320021);
INSERT INTO PREFIX_groupToStudent(GroupID,MatrikelNo) VALUES(3,320022);
INSERT INTO PREFIX_groupToStudent(GroupID,MatrikelNo) VALUES(3,320023);

-- Gruppen den Profs. zuordnen
INSERT INTO PREFIX_groupToTutor(GroupID,TutorID) VALUES(4,3);
INSERT INTO PREFIX_groupToTutor(GroupID,TutorID) VALUES(2,1);

--Projekte anlegen
INSERT INTO PREFIX_project(EventID,ProjectID,Description,Deadline) VALUES(0,0,'Erstellen eines Skripts in LaTeX','2007-09-01');
INSERT INTO PREFIX_project(EventID,ProjectID,Description,Deadline) VALUES(1,1,'Programmierung eines Praktikum-Managers, kurz PrakMan.','2007-09-18');
INSERT INTO PREFIX_project(EventID,ProjectID,Description,Deadline) VALUES(0,2,'Erarbeitung von Testdaten','2007-09-17');

-- Projekt: Testdaten
INSERT INTO PREFIX_projectToStudent(ProjectID,MatrikelNo) VALUES(2,320004);
INSERT INTO PREFIX_projectToStudent(ProjectID,MatrikelNo) VALUES(2,320005);
INSERT INTO PREFIX_projectToStudent(ProjectID,MatrikelNo) VALUES(2,320006);
INSERT INTO PREFIX_projectToStudent(ProjectID,MatrikelNo) VALUES(2,320007);
INSERT INTO PREFIX_projectToStudent(ProjectID,MatrikelNo) VALUES(2,320008);
INSERT INTO PREFIX_projectToStudent(ProjectID,MatrikelNo) VALUES(2,320010);
INSERT INTO PREFIX_projectToStudent(ProjectID,MatrikelNo) VALUES(2,320011);
INSERT INTO PREFIX_projectToStudent(ProjectID,MatrikelNo) VALUES(2,320012);
INSERT INTO PREFIX_projectToStudent(ProjectID,MatrikelNo) VALUES(2,320013);
INSERT INTO PREFIX_projectToStudent(ProjectID,MatrikelNo) VALUES(2,320014);
INSERT INTO PREFIX_projectToStudent(ProjectID,MatrikelNo) VALUES(2,320015);
INSERT INTO PREFIX_projectToStudent(ProjectID,MatrikelNo) VALUES(2,320016);
INSERT INTO PREFIX_projectToStudent(ProjectID,MatrikelNo) VALUES(2,320017);
INSERT INTO PREFIX_projectToStudent(ProjectID,MatrikelNo) VALUES(2,320018);
INSERT INTO PREFIX_projectToStudent(ProjectID,MatrikelNo) VALUES(2,320019);
INSERT INTO PREFIX_projectToStudent(ProjectID,MatrikelNo) VALUES(2,320020);
INSERT INTO PREFIX_projectToStudent(ProjectID,MatrikelNo) VALUES(2,320021);
INSERT INTO PREFIX_projectToStudent(ProjectID,MatrikelNo) VALUES(2,320022);
INSERT INTO PREFIX_projectToStudent(ProjectID,MatrikelNo) VALUES(2,320023);

-- Projekt: LaTeX-Skript
INSERT INTO PREFIX_projectToStudent(ProjectID,MatrikelNo) VALUES(0,320001);
INSERT INTO PREFIX_projectToStudent(ProjectID,MatrikelNo) VALUES(0,320009);

-- Testdaten-Projekt-Note 
INSERT INTO PREFIX_results(MatrikelNo,Result,ProjectID) VALUES(320004,'Gut',2);
INSERT INTO PREFIX_results(MatrikelNo,Result,ProjectID) VALUES(320005,'Gut',2);
INSERT INTO PREFIX_results(MatrikelNo,Result,ProjectID) VALUES(320006,'Gut',2);
INSERT INTO PREFIX_results(MatrikelNo,Result,ProjectID) VALUES(320007,'Gut',2);
INSERT INTO PREFIX_results(MatrikelNo,Result,ProjectID) VALUES(320008,'Gut',2);
INSERT INTO PREFIX_results(MatrikelNo,Result,ProjectID) VALUES(320010,'Gut',2);
INSERT INTO PREFIX_results(MatrikelNo,Result,ProjectID) VALUES(320011,'Gut',2);
INSERT INTO PREFIX_results(MatrikelNo,Result,ProjectID) VALUES(320012,'Gut',2);
INSERT INTO PREFIX_results(MatrikelNo,Result,ProjectID) VALUES(320013,'Gut',2);
INSERT INTO PREFIX_results(MatrikelNo,Result,ProjectID) VALUES(320014,'Gut',2);
INSERT INTO PREFIX_results(MatrikelNo,Result,ProjectID) VALUES(320015,'Sehr Gut',2);
INSERT INTO PREFIX_results(MatrikelNo,Result,ProjectID) VALUES(320016,'Gut',2);
INSERT INTO PREFIX_results(MatrikelNo,Result,ProjectID) VALUES(320017,'2',2);
INSERT INTO PREFIX_results(MatrikelNo,Result,ProjectID) VALUES(320018,'Gut',2);
INSERT INTO PREFIX_results(MatrikelNo,Result,ProjectID) VALUES(320019,'Gut',2);
INSERT INTO PREFIX_results(MatrikelNo,Result,ProjectID) VALUES(320020,'Gut',2);
INSERT INTO PREFIX_results(MatrikelNo,Result,ProjectID) VALUES(320021,'1,2',2);
INSERT INTO PREFIX_results(MatrikelNo,Result,ProjectID) VALUES(320022,'Gut',2);
INSERT INTO PREFIX_results(MatrikelNo,Result,ProjectID) VALUES(320023,'Gut',2); 

-- LaTeX-Projekt-Note
INSERT INTO PREFIX_results(MatrikelNo,Result,ProjectID) VALUES(320001,'Akzeptabel',0);
INSERT INTO PREFIX_results(MatrikelNo,Result,ProjectID) VALUES(320009,'Naja...',0);
