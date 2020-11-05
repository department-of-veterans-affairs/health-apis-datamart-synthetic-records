/* 
 * Create Implantable Device Table.  
 */

CREATE TABLE [App].[Device](
  [CDWId] [varchar](26) NOT NULL,
	[PatientFullICN] [varchar](50) NOT NULL,
	[Device] [varchar](max) NULL,
	[LastUpdated] [smalldatetime] NULL
)
GO

CREATE index [IX_Device_CDWId] on [App].[Device] ([CDWId])
GO

CREATE index [IX_Device_PatientFullICN] on [App].[Device] ([PatientFullIcn])
GO

CREATE index [IX_Device_LastUpdated] on [App].[Device] ([LastUpdated])
GO
