/*
 * Create Appointment Table.
 */

CREATE TABLE [App].[Appointment](
        [CDWId] [varchar](26) NOT NULL,
        [PatientFullICN] [varchar](50) NOT NULL,
        [DateUTC] [smalldatetime] NULL,
        [LastUpdated] [smalldatetime] DEFAULT getutcdate(),
        [Appointment]  varchar(max)
        CONSTRAINT PK_Appointment PRIMARY KEY CLUSTERED (CDWId)
)
GO

CREATE INDEX [cdx_Appointment_DateUTC] ON [App].[Appointment] ([DateUTC])
GO

CREATE INDEX [idx_Appointment_PatientFullICN_Include] ON [App].[Appointment] ([PatientFullICN])
GO
