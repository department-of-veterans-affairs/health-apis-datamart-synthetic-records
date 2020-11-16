DROP index [IX_Device_CDWId] ON [App].[Device]
GO

ALTER TABLE [App].[Device] ADD constraint PK_DEVICE primary key clustered (CDWID)
GO
