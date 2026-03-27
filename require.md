

Duplicate endpoints in `ApiDefined.java` (same path value, same base context):
1. QuestionBank.LIST, QuestionBank.CREATE, QuestionBank.UPDATE, QuestionBank.DELETE all map to "" (same as `QuestionBank.ROOT`).
1. Folder.LIST, Folder.CREATE both map to "" (same as `Folder.ROOT`).
1. QuestionFolder.LIST, QuestionFolder.CREATE both map to "" (same as `QuestionFolder.ROOT`).