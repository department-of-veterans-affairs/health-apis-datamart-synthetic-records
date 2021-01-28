/*
 * Create Appointment Table.
 */

CREATE TABLE [App].[Appointment](
        [CDWId] [BIGINT] NOT NULL,
        [ApptType] char(1) DEFAULT 'A',
        [PatientFullICN] [varchar](50) NOT NULL,
        [LocationSID] [int] NULL,
        [DateUTC] [smalldatetime] NULL,
        [LastUpdated] [DATE] DEFAULT getutcdate(),
        [Appointment]  varchar(max)
        CONSTRAINT PK_Appointment PRIMARY KEY CLUSTERED (CDWId)
)
GO

CREATE INDEX [cdx_Appointment_DateUTC] ON [App].[Appointment] ([DateUTC])
GO

CREATE INDEX [idx_Appointment_PatientFullICN_Include] ON [App].[Appointment] ([PatientFullICN])
GO
