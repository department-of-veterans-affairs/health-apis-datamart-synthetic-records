
CREATE TABLE [App].[PractitionerRole_Specialty_Map] (
	[PractitionerRoleIdNumber] [bigint] NOT NULL,
	[PractitionerRoleResourceCode] [char](1) NOT NULL,
	[SpecialtyCode] [varchar](50) NOT NULL,
    CONSTRAINT PK_PractitionerRole_Specialty_Map PRIMARY KEY CLUSTERED (PractitionerRoleIdNumber, PractitionerRoleResourceCode, SpecialtyCode)
)
GO
