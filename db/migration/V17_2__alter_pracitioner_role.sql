drop table [App].[PractitionerRole]
go

Create table [App].[PractitionerRole]
(
  [CDWIdNumber] [bigint] not null,
  [CDWIdResourceCode] [char](1) not null,
  [PractitionerIdNumber] [int] not null,
  [PractitionerResourceCode] [char](1) not null,
  [PractitionerGivenName] [varchar](50) null,
  [PractitionerFamilyName] [varchar](50) null,
  [PractitionerNPI] [varchar](50) null,
  [Active] [bit] null,
  [LastUpdated] [date] not null,
  [PractitionerRole] [varchar](MAX) not null
)
go
