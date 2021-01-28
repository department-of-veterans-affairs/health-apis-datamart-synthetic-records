ALTER TABLE [App].[Appointment] ADD [LocationSID] [int] NULL
ALTER TABLE [App].[Appointment] DROP CONSTRAINT DF__Appointme__LastU__6FE99F9F 
ALTER TABLE [APP].[Appointment] ALTER COLUMN [LastUpdated] [date]
