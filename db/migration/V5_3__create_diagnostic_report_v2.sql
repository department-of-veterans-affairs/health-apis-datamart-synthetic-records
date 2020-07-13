CREATE TABLE [app].[DiagnosticReport_V2](
  [CDWId] varchar(26) NOT NULL,
  [PatientFullICN] varchar(50) NOT NULL,
  [Category] varchar(20) NULL,
  [Code] varchar(20) NULL,
  [DateUTC] smalldatetime NULL,
  [LastUpdated] smalldatetime default getutcdate(),
  [DiagnosticReport] varchar(max) NOT NULL
  constraint PK_DiagnosticReport_V2 primary key clustered (CDWId)
)
GO

CREATE INDEX [IX_DiagnosticReport_V2_PatientFullICN] on [App].[DiagnosticReport_V2]([PatientFullICN])
GO

CREATE INDEX [IX_DiagnosticReport_V2_Category] on [App].[DiagnosticReport_V2]([Category])
GO

CREATE INDEX [IX_DiagnosticReport_V2_Code] on [App].[DiagnosticReport_V2]([Code])
GO

CREATE INDEX [IX_DiagnosticReport_V2_DateUTC] on [App].[DiagnosticReport_V2]([DateUTC])
GO
