ALTER TABLE [App].[Condition] ALTER COLUMN [CDWIdNumber] bigint not null
ALTER TABLE [App].[Condition] ALTER COLUMN [CDWIdResourceCode] char(1) not null
ALTER TABLE [App].[Condition] DROP constraint [PK_Condition]
ALTER TABLE [App].[Condition] DROP COLUMN [CDWId]
ALTER TABLE [App].[Condition] ADD constraint PK_Condition primary key clustered (CDWIdNumber, CDWIdResourceCode)
GO
