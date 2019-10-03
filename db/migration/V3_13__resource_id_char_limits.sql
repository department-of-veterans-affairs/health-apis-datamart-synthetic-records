/* In order for Identity Service V2 Ids to remain under 64 characters and remain fhir compliant,
   we must restrict the number of characters allowed in cdwIds to 26. */

-- AllergyIntolerance
ALTER TABLE [App].[AllergyIntolerance] DROP CONSTRAINT [PK_AllergyIntolerance]
ALTER TABLE [App].[AllergyIntolerance] ALTER COLUMN [CDWId] varchar(26) NOT NULL
ALTER TABLE [App].[AllergyIntolerance] ADD CONSTRAINT PK_AllergyIntolerance primary key clustered (CDWId)

DROP INDEX [App].[AllergyIntolerance].[IX_AllergyIntolerance_PatientFullICN]
ALTER TABLE [App].[AllergyIntolerance] ALTER COLUMN [PatientFullICN] varchar(26) NOT NULL
CREATE INDEX [IX_AllergyIntolerance_PatientFullICN] ON [App].[AllergyIntolerance] ([PatientFullICN])
GO

-- Condition
ALTER TABLE [App].[Condition] DROP CONSTRAINT [PK_Condition]
ALTER TABLE [App].[Condition] ALTER COLUMN [CDWId] varchar(26) NOT NULL
ALTER TABLE [App].[Condition] ADD CONSTRAINT PK_Condition primary key clustered (CDWId)

DROP INDEX [App].[Condition].[IX_Condition_PatientFullICN]
ALTER TABLE [App].[Condition] ALTER COLUMN [PatientFullICN] varchar(26) NOT NULL
CREATE INDEX [IX_Condition_PatientFullICN] ON [App].[Condition]([PatientFullICN])
GO

-- DiagnosticReport
ALTER TABLE [app].[DiagnosticReport] DROP CONSTRAINT [PK_DiagnosticReport]
DROP INDEX [app].[DiagnosticReport].[IX_DiagnosticReport_PatientFullICN]
ALTER TABLE [app].[DiagnosticReport] ALTER COLUMN [PatientFullICN] varchar(26) NOT NULL
ALTER TABLE [app].[DiagnosticReport] ADD CONSTRAINT PK_DiagnosticReport primary key clustered (PatientFullICN)
CREATE INDEX [IX_DiagnosticReport_PatientFullICN] on [App].[DiagnosticReport] ([PatientFullICN])
GO

-- DiagnosticReport Crosswalk
ALTER TABLE [App].[DiagnosticReport_Crosswalk] DROP CONSTRAINT [PK_DiagnosticReport_Crosswalk]
ALTER TABLE [App].[DiagnosticReport_Crosswalk] ALTER COLUMN [Identifier] varchar(26) NOT NULL
ALTER TABLE [App].[DiagnosticReport_Crosswalk] ADD CONSTRAINT PK_DiagnosticReport_Crosswalk primary key clustered (Identifier)

DROP INDEX [App].[DiagnosticReport_Crosswalk].[IX_DiagnosticReport_Crosswalk_PatientFullICN]
ALTER TABLE [App].[DiagnosticReport_Crosswalk] ALTER COLUMN [PatientFullICN] varchar(26) NOT NULL
CREATE INDEX [IX_DiagnosticReport_Crosswalk_PatientFullICN] ON [app].[DiagnosticReport_Crosswalk](PatientFullICN)
GO

-- Immunization
ALTER TABLE [App].[Immunization] DROP CONSTRAINT [PK_Immunization]
ALTER TABLE [App].[Immunization] ALTER COLUMN [CDWId] varchar(26) NOT NULL
ALTER TABLE [App].[Immunization] ADD CONSTRAINT PK_Immunization primary key clustered (CDWId)

DROP INDEX [App].[Immunization].[IX_Immunization_PatientFullICN]
ALTER TABLE [App].[Immunization] ALTER COLUMN [PatientFullICN] varchar(26) NOT NULL
CREATE INDEX [IX_Immunization_PatientFullICN] ON [App].[Immunization]([PatientFullICN])
GO

-- Medication
ALTER TABLE [App].[Medication] DROP CONSTRAINT [PK_Medication]
DROP INDEX [App].[Medication].[IX_Medication_CDWId]
ALTER TABLE [App].[Medication] ALTER COLUMN [CDWId] varchar(26) NOT NULL
ALTER TABLE [App].[Medication] ADD CONSTRAINT PK_Medication primary key clustered (CDWId)
CREATE INDEX [IX_Medication_CDWId] on [App].[Medication] ([CDWId])
GO

-- MedicationOrder
ALTER TABLE [App].[MedicationOrder] DROP CONSTRAINT [PK_MedicationOrder]
ALTER TABLE [App].[MedicationOrder] ALTER COLUMN [CDWId] varchar(26) NOT NULL
ALTER TABLE [App].[MedicationOrder] ADD CONSTRAINT PK_MedicationOrder primary key clustered (CDWId)

DROP INDEX [App].[MedicationOrder].[IX_MedicationOrder_PatientFullICN]
ALTER TABLE [App].[MedicationOrder] ALTER COLUMN [PatientFullICN] varchar(26) NOT NULL
CREATE INDEX [IX_MedicationOrder_PatientFullICN] ON [App].[MedicationOrder]([PatientFullICN])
GO

-- MedicationStatement
ALTER TABLE [App].[MedicationStatement] DROP CONSTRAINT [PK_MedicationStatement]
ALTER TABLE [App].[MedicationStatement] ALTER COLUMN [CDWId] varchar(26) NOT NULL
ALTER TABLE [App].[MedicationStatement] ADD CONSTRAINT PK_MedicationStatement primary key clustered (CDWId)

DROP INDEX [App].[MedicationStatement].[IX_MedicationStatement_PatientFullICN]
ALTER TABLE [App].[MedicationStatement] ALTER COLUMN [PatientFullICN] varchar(26) NOT NULL
CREATE INDEX [IX_MedicationStatement_PatientFullICN] ON [App].[MedicationStatement]([PatientFullICN])
GO

-- Observation
DROP INDEX [App].[Observation].[IX_Observation_PatientFullICN]
ALTER TABLE [App].[Observation] ALTER COLUMN [PatientFullICN] varchar(26) NOT NULL
CREATE INDEX [IX_Observation_PatientFullICN] ON [App].[Observation]([PatientFullICN])
GO

/* The Observation table was created a little differently, the primary key constraint was created
   on table creation and therefore, we don't know the name of the constraint to drop it.
   The solution is to do a select on the system table to find the constraint, then drop it.
   After altering the CDWId column to be varchar(26) well add the primary key back using a name
   that can be easily used in future sql scripts. */

DECLARE @sql nvarchar(255)
SELECT @sql = 'ALTER TABLE app.Observation DROP CONSTRAINT ' + D.name
FROM sys.key_constraints AS D
WHERE d.name LIKE 'PK__Obs%'
EXECUTE sp_executesql @sql

ALTER TABLE [App].[Observation] ALTER COLUMN [CDWId] varchar(26) NOT NULL
ALTER TABLE  [App].[Observation] ADD CONSTRAINT PK_Observation primary key clustered (CDWId)
GO

-- Patient Report
ALTER TABLE [app].[PatientReport] DROP CONSTRAINT [PK_PatientReport]
ALTER TABLE [app].[PatientReport] ALTER COLUMN [PatientFullICN] varchar(26) NOT NULL
ALTER TABLE [app].[PatientReport] ADD CONSTRAINT PK_PatientReport primary key clustered (PatientFullICN)
GO

-- Patient Search
ALTER TABLE [app].[PatientSearch] DROP CONSTRAINT [PK_PatientSearch]
ALTER TABLE [app].[PatientSearch] ALTER COLUMN [fullIcn] varchar(26) NOT NULL
ALTER TABLE [app].[PatientSearch] ADD CONSTRAINT PK_PatientSearch primary key clustered (fullIcn)
GO

-- Procedure
ALTER TABLE [App].[Procedure] DROP CONSTRAINT [PK_Procedure]
ALTER TABLE [App].[Procedure] ALTER COLUMN [CDWId] varchar(26) NOT NULL
ALTER TABLE [App].[Procedure] ADD CONSTRAINT PK_Procedure primary key clustered (CDWId)

DROP INDEX [App].[Procedure].[IX_Procedure_PatientFullICN]
ALTER TABLE [App].[Procedure] ALTER COLUMN [PatientFullICN] varchar(26) NOT NULL
CREATE INDEX [IX_Procedure_PatientFullICN] ON [App].[Procedure]([PatientFullICN])
GO
