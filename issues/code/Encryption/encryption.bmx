WritePakFile("test.png","test.pak")
img:TImage = LoadImage(ReadPakFile("test.pak"))

Function CodeStream:TStream(in:TStream)
  Local bank:TBank = New TBank
  Local out_stream:TStream = OpenStream(bank)
  out_stream.WriteString("Start_Pak")
  out_stream.WriteInt(in.Size())
  While Not Eof(in)
    out_stream.WriteByte( in.ReadByte() + 10 )
  Wend
  out_stream.WriteString("End_Pak")
  out_stream.Seek(0)
  in.Seek(0)
  Return out_stream
End Function

Function DecodeStream:TStream(in:TStream)
  Local bank:TBank = New TBank
  Local out_stream:TStream = OpenStream(bank)
  Local Header$ = in.ReadString(9)
  If Header <> "Start_Pak" Then RuntimeError "pak corrupt"
  Local DataSize% = in.ReadInt()
  For Local dat% = 1 To DataSize
    out_stream.WriteByte ( in.ReadByte() - 10 )
  Next
  Local EndHeader$ = in.ReadString(7)
  If EndHeader <> "End_Pak" Then RuntimeError "pak data corrupt"
  out_stream.Seek(0)
  in.Seek(0)
  Return out_stream
End Function

Function ReadPakFile:TStream(filename$)
  Local file:TStream = ReadFile(filename)
  Local out:TStream = DecodeStream(file)
  CloseFile(file)
  Return out
End Function

Function WritePakFile(filename_in$,filename_out$)
  Local file:TStream = ReadFile(filename_in)
  Local file_out:TStream = WriteFile(filename_out)
  Local out:TStream = CodeStream(file)
  While Not Eof(out)
    file_out.WriteByte(out.ReadByte())
  Wend
  CloseFile file_out out.Close()
End Function