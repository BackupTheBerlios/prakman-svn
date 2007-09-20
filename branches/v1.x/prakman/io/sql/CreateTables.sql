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

-- Gruppen anlegen
INSERT INTO PREFIX_group(GroupID,EventID,Description) VALUES (1,0,'Gruppe 2 v. Datenbanken Biermann');
INSERT INTO PREFIX_group(GroupID,EventID,Description) VALUES (2,1,'Gruppe 1 v. SE Siekmann');
INSERT INTO PREFIX_group(GroupID,EventID,Description) VALUES (3,0,'Gruppe 3 v. Datenbanken Biermann');
INSERT INTO PREFIX_group(GroupID,EventID,Description) VALUES (4,0,'Gruppe 1 v. Datenbanken Biermann');

-- Gruppen den Profs. zuordnen
INSERT INTO PREFIX_groupToTutor(GroupID,TutorID) VALUES(4,3);
INSERT INTO PREFIX_groupToTutor(GroupID,TutorID) VALUES(2,1);

--Projekte anlegen
INSERT INTO PREFIX_project(EventID,ProjectID,Description,Deadline) VALUES(0,0,'Erstellen eines Skripts in LaTeX','2007-09-01');
INSERT INTO PREFIX_project(EventID,ProjectID,Description,Deadline) VALUES(1,1,'Programmierung eines Praktikum-Managers, kurz PrakMan.','2007-09-18');
INSERT INTO PREFIX_project(EventID,ProjectID,Description,Deadline) VALUES(0,2,'Erarbeitung von Testdaten','2007-09-17');
