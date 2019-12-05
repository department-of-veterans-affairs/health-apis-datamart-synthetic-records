Create table [App].[Organization]
(
  [CDWID] varchar(15) not null,
  [NPI] varchar(50) null,
  [ProviderID] varchar(50) null,
  [EDIID] varchar(50) null,
  [AgencyID] varchar(50) null,
  [Name] varchar(50) null,
  [Address] varchar(50) null,
  [City] varchar(50) null,
  [State] varchar(30) null,
  [PostalCode] varchar(30) null,
  [Organization] varchar(max) null,
  [ETLBatchId] int null,
  [ETLChunkNum int null,
  [ETLCreateDate] datetime2(0) null,
  constraint PK_Organization primary key clustered (CDWID)
)
