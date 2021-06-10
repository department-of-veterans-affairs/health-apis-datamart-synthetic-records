DROP TABLE [App].[Practitioner]
GO

CREATE TABLE [App].[Practitioner]
(
  [CDWId] varchar(15) not null,
  [CDWIdNumber] bigint null,
  [CDWIdResourceCode] char(1) null,
  [FamilyName] varchar(50) null,
  [GivenName] varchar(50) null,
  [NPI] varchar(50) null,
  [LastUpdated] date null,
  [Practitioner] varchar(max) null,
  constraint PK_Practitioner primary key clustered (CDWId)
)
GO
