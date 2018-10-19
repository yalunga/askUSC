DROP DATABASE IF EXISTS askUSC;
CREATE DATABASE askUSC;
USE askUSC;

CREATE TABLE User (
	userID VARCHAR(100) PRIMARY KEY,
    idToken VARCHAR(100) UNIQUE,
    fullName VARCHAR(100),
    lastName VARCHAR(100),
    firstName VARCHAR(100),
    email VARCHAR(100),
    userType VARCHAR(100),
    studentID VARCHAR(100) NULL UNIQUE,
    studentType VARCHAR(100) NULL,
    instructorID VARCHAR(100) NULL UNIQUE,
    instructorType VARCHAR(100) NULL
);

CREATE TABLE Class (
	classID VARCHAR(100) PRIMARY KEY,
    department VARCHAR(100),
    classNumber VARCHAR(100),
    instructorID VARCHAR(100),
    classDescription VARCHAR(100),
    lectureIDs VARCHAR(100),
    FOREIGN KEY (instructorID) REFERENCES User(instructorID)
);

CREATE TABLE Lecture (
	lectureUUID VARCHAR(100) UNIQUE,
    sectionID VARCHAR(100) PRIMARY KEY,
    classID VARCHAR(100),
    startTime TIME,
    endTime TIME,
    meetingDaysOfWeek VARCHAR(100),
    lectureDescription VARCHAR(500),
    FOREIGN KEY (classID) REFERENCES Class(classID)
);

CREATE TABLE LectureRegistration (
	studentID VARCHAR(100),
    lectureUUID VARCHAR(100),
    FOREIGN KEY (studentID) REFERENCES User(studentId),
    FOREIGN KEY (lectureUUID) REFERENCES Lecture(lectureUUID)
);

CREATE TABLE QnA_Table (
	idx INT(11) PRIMARY KEY,
    lectureUUID VARCHAR(100),
    userID VARCHAR(100),
    postTitle VARCHAR(100) NULL,
    postContent VARCHAR(500),
    time_stamp DATETIME,
    upvotes SMALLINT(11),
    FOREIGN KEY (lectureUUID) REFERENCES Lecture(lectureUUID),
    FOREIGN KEY (userID) REFERENCES User(userID)
);

CREATE TABLE AttendanceRecord (
	idx INT(11) PRIMARY KEY,
    studentID VARCHAR(100),
    lectureUUID VARCHAR(100),
    lectureDate DATE,
	FOREIGN KEY (studentID) REFERENCES User(StudentID),
    FOREIGN KEY (lectureUUID) REFERENCES Lecture(lectureUUID)
);