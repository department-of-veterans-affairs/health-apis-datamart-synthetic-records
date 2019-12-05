Create table [App].[Location]
(
  [CDWID] varchar(15) not null,
  [Name] varchar(50) null,
  [Street] varchar(90) null,
  [City] varchar(40) null,
  [State] varchar(5) null,
  [PostalCode] varchar(10) null,
  [Location] varchar(max) null,
  [ETLBatchId] int null,
  [ETLChunkNum int null,
  [ETLCreateDate] datetime2(0) null,
  constraint PK_Location primary key clustered (CDWID)
)
