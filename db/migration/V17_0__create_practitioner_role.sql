Create table [App].[PractitionerRole]
(
  [CDWIdNumber] [int] not null,
  [CDWIdResourceCode] [char](1) not null,
  [Specialty] [varchar](255) null,
  [PractitionerGivenName] [varchar](50) null,
  [PractitionerFamilyName] [varchar](50) null,
  [PractitionerNPI] [varchar](50) null,
  --JSON Payload
  [PractitionerRole] [varchar](MAX) not null,
  [LastUpdated] [date] not null
)
