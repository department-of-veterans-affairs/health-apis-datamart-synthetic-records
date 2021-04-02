DROP TABLE IF EXISTS [App].[Organization]
GO

CREATE TABLE [App].[Organization](
	[CDWID] [varchar](15) NOT NULL,
	[NPI] [varchar](50) NULL,
	[Name] [varchar](50) NULL,
	[Address] [varchar](50) NULL,
	[City] [varchar](50) NULL,
	[State] [varchar](30) NULL,
	[PostalCode] [varchar](30) NULL,
	[FacilityType] [varchar](20) NULL,
	[StationNumber] [varchar](50) NULL,
	[LastUpdated] [date] NULL,
	[Organization] [varchar](max)
    constraint PK_Organization primary key clustered (CDWID)
)
GO

