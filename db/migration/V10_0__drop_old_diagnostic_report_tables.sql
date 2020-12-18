/*
  Dropping the old DiagnosticReport and DiagnosticReport_Crosswalk tables in favor of the DiagnosticReport_V2 table
*/

drop table if exists [app].[DiagnosticReport]
drop table if exists [app].[DiagnosticReport_Crosswalk]
