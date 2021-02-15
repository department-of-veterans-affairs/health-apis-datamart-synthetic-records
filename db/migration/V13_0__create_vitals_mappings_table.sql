/* Make sure we aren't creating an already existing table. */
DROP TABLE IF EXISTS [App].[vw_Mapped_Values]

/* Create Vital Mapping Table. */
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [App].[vw_Mapped_Values] (
		ValueID [int] NOT NULL,
		CodeID [int] NOT NULL,
		SourceSystemID [smallint] NOT NULL,
		SourceSystemCode [varchar](10) NULL,
		SourceSystemName [varchar](100) NULL,
		SourceValue [nvarchar](500) NULL,
		CodingSystemID [smallint] NULL,
		Code [varchar](100) NULL,
		Display [nvarchar](500) NULL,
		CodeDefinition [nvarchar](4000) NULL,
		CodingSystem [varchar](100) NULL,
		URI [varchar](100) NULL,
		OID [varchar](100) NULL,
		) WITH (DATA_COMPRESSION = PAGE)
GO
