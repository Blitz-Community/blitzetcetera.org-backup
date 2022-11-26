Type key
 Field name$, link.key[223]
End Type

Include "IndexedBase.bb"

textfile_load "test.txt"
db_save "test.bdb"
db_load "test.bdb"
textfile_save "out.txt"

Function dbkey_read(f,k.key)
End Function

Function dbkey_write(f,k.key)
End Function