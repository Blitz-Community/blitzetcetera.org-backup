Function CopyFile:Int(SourceFile:String, DestinationFile:String, AutoReplace:Int=False, BufSize:Int=4096)
	
	If FileType(SourceFile) <> 1 Then Return False
	If FileType(DestinationFile) = 1 And Not AutoReplace Then Return False
	
	Local SourceStream:TStream = OpenStream(SourceFile,True,False)
	Local DestStream:TStream = OpenStream(DestinationFile,False,True)
	
	CopyStream(SourceStream, DestStream, BufSize)
	CloseStream(SourceStream)
	CloseStream(DestStream)
	
	Return True
End Function
