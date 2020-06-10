DROP INDEX [App].[Observation].[IX_Observation_DateUTC]
ALTER TABLE [App].[Observation] ALTER COLUMN [DateUTC] smalldatetime
CREATE INDEX [IX_Observation_DateUTC] ON [App].[Observation]([DateUTC])
GO
