/*
==========================================================================
Data Definition:
Column Name      | Description
-----------------|-----------------------------------------------------
PatientFullICN   | The patient's VA internal control number
-----------------|-----------------------------------------------------
FullName         | The patients full name, first, middle and last
-----------------|-----------------------------------------------------
LastName         | the patient's last name
-----------------|-----------------------------------------------------
FirstName        | the patient's first and middle name
-----------------|-----------------------------------------------------
Birthdate        | the patient's date and time of birth in UTC.
                 | should the time be 0 or omitted then no time was
				         | given
-----------------|-----------------------------------------------------
Gender           | indicates the patient's gender. 'M' for male, 'F'
                 | for female
-----------------|-----------------------------------------------------
LastUpdated      | indicates the last time this record was modified
-----------------|-----------------------------------------------------
Patient          | json payload containing the patient record
-----------------|-----------------------------------------------------


*/

CREATE TABLE [app].[Patient](
  [PatientFullICN] [varchar](50),
  [FullName] [varchar](152) NULL,
  [LastName] [varchar](50) NULL,
  [FirstName] [varchar](50) NULL,
  [Birthdate] [datetime2](0) NULL,
  [Gender] [varchar](50) NULL,
  [LastUpdated] smalldatetime DEFAULT GETUTCDATE(),
  [Patient] [varchar](max) NULL
  constraint PK_Patient primary key clustered (PatientFullICN)
)
GO

CREATE INDEX [IX_Patient_PatientFullICN] on [App].[Patient]([PatientFullICN])
GO

CREATE INDEX [IX_Patient_FullName] on [App].[Patient]([FullName])
GO

CREATE INDEX [IX_Patient_Family] on [App].[Patient]([LastName])
GO

CREATE INDEX [IX_Patient_Given] on [App].[Patient]([FirstName])
GO

CREATE INDEX [IX_Patient_Birthdate] on [App].[Patient]([Birthdate])
GO

CREATE INDEX [IX_Patient_Gender] on [App].[Patient]([Gender])
GO
