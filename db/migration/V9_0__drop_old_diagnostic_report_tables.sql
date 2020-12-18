/*
  Dropping the old DiagnosticReport and DiagnosticReport_Crosswalk tables in favor of the DiagnosticReport_V2 table
*/

drop table if exists [app].[DiagnosticReport]
drop table if exists [app].[DiagnosticReport_Crosswalk]
drop table if exists [app].[DiagnosticReport_V2]

CREATE TABLE [app].[DiagnosticReport](
                                            [CDWId] varchar(26) NOT NULL,
                                            [PatientFullICN] varchar(50) NOT NULL,
                                            [Category] varchar(20) NULL,
                                            [Code] varchar(20) NULL,
                                            [DateUTC] smalldatetime NULL,
                                            [LastUpdated] smalldatetime default getutcdate(),
                                            [DiagnosticReport] varchar(max) NOT NULL
                                                constraint PK_DiagnosticReport primary key clustered (CDWId)
)
GO

CREATE INDEX [IX_DiagnosticReport_PatientFullICN] on [App].[DiagnosticReport]([PatientFullICN])
GO

CREATE INDEX [IX_DiagnosticReport_Category] on [App].[DiagnosticReport]([Category])
GO

CREATE INDEX [IX_DiagnosticReport_Code] on [App].[DiagnosticReport]([Code])
GO

CREATE INDEX [IX_DiagnosticReport_DateUTC] on [App].[DiagnosticReport]([DateUTC])
GO
