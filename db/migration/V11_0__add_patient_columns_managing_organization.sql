
ALTER TABLE [app].[Patient] ADD [ManagingOrganization] [varchar](50) NULL
GO

CREATE INDEX [IX_Patient_ManagingOrganization] on [App].[Patient]([ManagingOrganization])
GO
