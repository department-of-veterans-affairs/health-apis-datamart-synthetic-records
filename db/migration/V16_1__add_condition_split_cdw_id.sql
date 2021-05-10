Create table [App].[Condition]
(
  [CDWId] varchar(26) not null,
  [CDWIdNumber] bigint null,
  [CDWIdResourceCode] char(1) null,
  [PatientFullICN] varchar(50) null,
  [Category] varchar(9) not null,
  [ClinicalStatus] varchar(8) not null,
  [ICD9Code] varchar(50) null,
  [ICD10Code] varchar(50) null,
  [SnomedCode] varchar(50) null,
  [DateUTC] smalldatetime null,
  [LastUpdated] date null,
  [Condition] varchar(max) null,
  constraint PK_Condition primary key clustered (CDWId)
)
GO

create index [IX_Condition_PatientFullICN] on [App].[Condition]([PatientFullICN])
GO

create index [IX_Condition_OnSet] on [App].[Condition]([DateUTC])
GO

create index [IX_Condition_Category] on [App].[Condition]([Category])
GO

create index [IX_Condition_ClinicalStatus] on [App].[Condition]([ClinicalStatus])
GO
