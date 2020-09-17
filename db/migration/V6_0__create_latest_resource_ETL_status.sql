-- This table is only used in Lab environment. It doesn't exist in prod.
-- Instead, prod Datamart has a view that maps to it: App.vw_Latest_Resource_ETL_Status
CREATE TABLE [App].[Latest_Resource_ETL_Status](
	[ResourceName] [varchar](100) NULL,
	[EndDateTimeUTC] [smalldatetime] NULL
)WITH (DATA_COMPRESSION = PAGE)
