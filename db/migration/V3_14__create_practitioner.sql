Create table [App].[Practitioner]
(
  [CDWID] varchar(15) not null,
  [NPI] varchar(50) null,
  [FamilyName] varchar(50) null,
  [GivenName] varchar(50) null,
  [Practitioner] varchar(max) null,
  [ETLBatchId] int null,
  [ETLChunkNum int null,
  [ETLCreateDate] datetime2(0) null,
  constraint PK_Practitioner primary key clustered (CDWID)
)
