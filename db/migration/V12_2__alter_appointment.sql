ALTER TABLE [App].[Appointment] DROP CONSTRAINT PK_Appointment;
EXEC sp_rename '[App].[Appointment].CDWId', 'CdwIdNumber', 'COLUMN';
EXEC sp_rename '[App].[Appointment].ApptType', 'CdwIdResourceType', 'COLUMN';
ALTER TABLE [App].[Appointment] ADD CONSTRAINT PK_Appointment PRIMARY KEY CLUSTERED (CdwIdNumber);
