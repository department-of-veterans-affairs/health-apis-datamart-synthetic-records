Create table [App].[PractitionerRole]
(
  [CDWIdNumber] [int] not null,
  [CDWIdResourceCode] [char](1) not null,
  [Sta3n] [smallint] not null,
  [Specialty] [varchar](100) null,
  [PractitionerGivenName] [varchar](50) null,
  [PractitionerFamilyName] [varchar](50) null,
  [PractitionerNPI] [varchar](50) null,
  --JSON Payload
  [PractitionerRole] [varchar](MAX),
  [LastUpdated] [date] not null
)
