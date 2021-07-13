ALTER TABLE [App].[Practitioner] ALTER COLUMN [CDWIdNumber] bigint NOT NULL
GO

ALTER TABLE [App].[Practitioner] ALTER COLUMN [CDWIdResourceCode] char(1) NOT NULL
GO

ALTER TABLE [App].[Practitioner] DROP constraint PK_Practitioner
GO

ALTER TABLE [App].[Practitioner] ADD constraint PK_Practitioner primary key clustered (CDWIdNumber, CDWIdResourceCode)
GO
