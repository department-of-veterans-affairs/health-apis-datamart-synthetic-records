ALTER TABLE [App].[PractitionerRole] ADD [Active] BIT NULL, [PractitionerIdNumber] INT NOT NULL, [PractitionerResourceCode] CHAR(1) NOT NULL
GO
ALTER TABLE [App].[PractitionerRole] ALTER COLUMN [CdwIdNumber] BIGINT
GO
ALTER TABLE [App].[PractitionerRole] DROP COLUMN [Specialty]
GO
