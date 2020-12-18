
ALTER TABLE [app].[Patient] ADD [SSN] [varchar](50) NULL
GO

CREATE INDEX [IX_Patient_SSN] on [App].[Patient]([SSN])
GO
