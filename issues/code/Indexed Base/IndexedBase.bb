Global rootkey.key

Function dbkey_add.key(name$,kfrom.key)
DebugLog name$
If name$="" Then Return
rk.key=kfrom
Repeat 
 k.key=dbkeylink(rk,name$)
 If k=Null Then
   nk.key=New key
   nk\name$=name$
   dbkeylink_set rk,nk
   Return nk
 Else
  l=Len(k\name$)
  nl=Len(name$)
  
  For n=l+(nl-l)*(nl<l) To 0 Step -1
   If Left$(name$,n)=Left$(k\name$,n) Then Exit
  Next
  
  If n<l And n<nl Then
   nk.key=New key
   nk\name$=Left$(name$,n)
   dbkeylink_set rk,nk
   rk=nk
 
   k\name$=Mid$(k\name$,n+1)
   dbkeylink_set rk,k

   nk.key=New key
   nk\name$=Mid$(name$,n+1)
   dbkeylink_set rk,nk

   Return nk
  ElseIf nl<l Then
   nk.key=New key
   nk\name$=name$
   dbkeylink_set rk,nk

   k\name$=Mid$(k\name$,n+1)
   dbkeylink_set nk,k
   Return nk
 Else   
   name$=Mid$(name$,l+1)
   If name$="" Then Return k
   rk=k
  End If
 End If
Forever
End Function

Function dbkeylink.key(parent.key,name$)
Return parent\link[Asc(Left$(name$,1))-32]
End Function

Function dbkeylink_set(parent.key,k.key)
parent\link[Asc(Left$(k\name$,1))-32]=k
End Function

Function db_init()
Delete Each key
rootkey=New key
End Function

Function db_load(file$)
db_init
f=ReadFile(file$)
If f=0 Then DebugLog "No access to file "+file$: Return
dbkey_fromfile f,rootkey
CloseFile f
End Function

Function dbkey_fromfile(f,k.key)
k\name$=ReadString(f)
dbkey_read f,k
Repeat
 n=ReadByte(f)
 If n=255 Then Exit
 nk.key=New key
 k\link[n]=nk
 dbkey_fromfile f,nk
Forever
End Function

Function db_save(file$)
f=WriteFile(file$)
dbkey_tofile f,rootkey
CloseFile f
End Function

Function dbkey_tofile(f,k.key)
WriteString f,k\name$
dbkey_write f,k
For n=0 To 223
 If k\link[n]<>Null Then 
  WriteByte f,n
  dbkey_tofile f,k\link[n]
 End If
Next
WriteByte f,255
End Function

Function textfile_load(file$)
db_init
f=ReadFile(file$)
If f=0 Then DebugLog "No access to file "+file$: Return
While Not Eof(f)
 dbkey_add ReadLine$(f),rootkey
Wend
CloseFile f
End Function

Function textfile_save(file$)
f=WriteFile(file$)
dbkey_totextfile f,rootkey
CloseFile f
End Function

Function dbkey_totextfile(f,k.key,m$="")
WriteLine f,m$+k\name$
For n=0 To 223
 If k\link[n]<>Null Then dbkey_totextfile f,k\link[n],m$+k\name$
Next
End Function