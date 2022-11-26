Function CopyDirectory(From_$, To_$) 
  Local MyDir, MyFile$ 

  MyDir = ReadDir(From_$) 
  MyFile$ = NextFile$(MyDir) 
  MyFile$ = NextFile$(MyDir) 
  Repeat 
    MyFile$ = NextFile$(MyDir) 
    If MyFile$ = "" Then Exit 

    If FileType(From_$ + "\" + MyFile$) = 2 Then 
      CreateDir To_$ + "\" + MyFile$ 
      CopyDirectory(From_$ + "\" + MyFile$, To_$ + "\" + MyFile$) 
    Else 
      CopyFile From_$ + "\" + MyFile$, To_$ + "\" + MyFile$ 
    End If 
  Forever 
  CloseDir(MyDir)  
End Function
