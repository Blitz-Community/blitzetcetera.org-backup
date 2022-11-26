; PakFile Handling Routines 2.1
; Copyright Lee Page
; TeraBit Software 2000 - 2002

Type UnpackedFiles
	Field file$
End Type

Type ExpDir
	Field dir$
End Type

Dim pakfiles$(4000)			
Dim paklocate(4000)			
Dim PakSize(4000)			

Dim AddPack$(4000)
Dim AddPackSize(4000)
Dim AddPackOffset(4000)

Global AddPackCount
Global AddPackHeaderMask
Global AddPackKey
Global AddFileTrack
Global OutputDir$
Global LastPak$
Global PakBulkOverwrite

Global PackCount			;	Items in the pack file.
Global key					;	The Encrytion Key
Global Packname$			; 	The Default is DATA.PAK
Global exeoffset			;	For use when Data is packed into an .EXE
Global pakAPND$				; 	Sets up the Name to append to the start of the unpacked filenames

Global ChunkName$, Chunklen

; These are the Pak File Extraction Routines
;-----------------------------------------------------------------------

Function PakInit(myfile$,mykey,APND$,headermask)			; PackFile Initialisation

Local Exists
Local Headcount
Local pack
Local Retrieve
Local Offset
Local Block
Local Tem$
Local secbit
Local Hold
Local T
Local Fty
Local cnt
Local Filecnt

key = mykey	
pakApnd$=APND$
If FileType(myfile$)<>0 Then						; Check to see if the Archive Exists
packname$=myfile$									; If so set up Global Pack Name
Else												; Otherwise
packname$="Data.pak"								; Default to Data.Pak
EndIf												

If Upper$(Right$(packname$,4))=".EXE" Or Upper$(Right$(packname$,4))=".SCR" Then	; If the Archive is a .EXE File
retrieve = ReadFile(packname$)						; Open the Archive
SeekFile(retrieve,FileSize(packname$)-10)			; Find the Archive Data Section
exeoffset = ReadInt(retrieve)-1						; Set Global Offset to Data Section
exeoffset=exeoffset - headermask     				; New 
CloseFile(retrieve)									; Close Data Pack
EndIf		

pack = ReadFile(packname$)							; Open Archive

SeekFile(pack,exeoffset)							; Seek to Data Section (0 if DATA.PAK)
offset = ReadInt(pack)
													; Find Header Information
offset = offset Xor headermask         				; New

SeekFile(pack,(offset-1)+exeoffset)					; Go to the Header

PackCount = ReadInt(Pack)							; Read the number of items in the pack
headcount=(FileSize(packname$)-FilePos(pack))+4



block = CreateBank(headcount)
ReadBytes block,pack,0,headcount-4
If key<>0 Then
For filecnt=1 To headcount-4 Step 4
hold=PeekInt(block,filecnt-1)
hold = hold Xor key
PokeInt block,filecnt-1,hold
Next 
EndIf
tem$ = ""
secbit=0
cnt = 1

For t=0 To headcount-5
If PeekByte(block,t)=13 Then 

	If fty = 0 Then 
	pakfiles$(cnt)=Upper$(tem$)
	EndIf

	If fty = 1 Then 
		paklocate(cnt)=tem$
		paklocate(cnt)=paklocate(cnt)+exeoffset
	EndIf

	If fty = 2 Then 
		Paksize(cnt)=tem$
		cnt=cnt+1
	EndIf
fty=fty+1
If fty=3 Then fty=0
tem$=""
;t=t+1
Else
tem$=tem$ + Chr$(PeekByte(block,t))
EndIf
Next 

	
FreeBank block										; Record this info in a Global Array
CloseFile (pack)									; for use by Other Functions
End Function										; Then Close the Archive

;-----------------------------------------------------------------------

Function pak$(pkf$)									; Get Filename to retrieve

If Left$(pkf$,1)="\" Or Left$(pkf$,1)="/" Then pkf$=Right$(pkf$,Len(pkf$)-1)

Local IsoName
Local cnt
Local item
Local Pack
Local Output
Local Block
Local filecnt
Local Hold
Local Ext$
Local Ex2$
Local outf$

If Instr(pkf$,"\")=0 And Instr(pkf$,"/")=0 Then 
IsoName = True
Else
CreateDirTree IsoPath(pkf$)
EndIf

For cnt=1 To packcount								; Count through Archive list
	If IsoName = True Then
		If IsolateName(Upper$(pakfiles$(cnt)))=Upper$(pkf$) Then		; If it exists in Archive
			item = cnt									; then set item to it's index
			Exit										; and break out of the loop
		EndIf
	Else
		If Upper$(pakfiles$(cnt))=Upper$(pkf$) Then		; If it exists in Archive
			item = cnt									; then set item to it's index
			Exit										; and break out of the loop
		EndIf
	EndIf
Next
If item = 0 Then 									; If no items were found
	Return pkf$										; Exit the subroutine
EndIf

LastPak$ = IsoPath(pkf$)+pakapnd$+IsolateName(pkf$)
For lpk.unpackedfiles = Each unpackedfiles
If Upper$(lpk\file$) = Upper$(lastpak$) Then exists = 1 : Exit
Next

If exists=0 Then
	lpk.UnpackedFiles = New UnpackedFiles 
	lpk\file$ = LastPak$
EndIf

If FileType(IsoPath(pkf$)+pakapnd$+IsolateName(pkf$))=0 Or PakBulkOverwrite = True Then ; Does the File Already Exist?
	pack = ReadFile(packname$)							; Open the Archive
	SeekFile(pack,paklocate(item))						; Locate the Data in the store
	output = WriteFile(IsoPath(pkf$)+pakapnd$+IsolateName(pkf$))		; Open the output File
	block = CreateBank(paksize(item)+4)
	ReadBytes block,pack,0,paksize(item)
	If key<>0 Then
	For filecnt=1 To paksize(item) Step 4
	hold=PeekInt(block,filecnt-1)
	hold = hold Xor key
	PokeInt block,filecnt-1,hold
	Next 
	EndIf
	WriteBytes block,output,0,paksize(item)
	CloseFile(output)
	CloseFile(pack)	
EndIf
	
ext$ = Upper$(Right$(pkf$,4))
ex2$ = Upper$(Right$(pkf$,2))

FreeBank block

If ext$=".3DS" Or ex2$=".X" Or ext$="MD2" Then texcheck(IsoPath(pkf$)+pakapnd$+IsolateName(pkf$))
If ext$=".B3D" Then ParseB3D(IsoPath(pkf$)+pakapnd$+IsolateName(pkf$))
Return IsoPath(pkf$)+pakapnd$+IsolateName(pkf$)								; Return the Filename
End Function

;-----------------------------------------------------------------------

Function texpak$(pkf$)									; Get Filename to retrieve
DebugLog pkf$
If Left$(pkf$,1)="\" Or Left$(pkf$,1)="/" Then pkf$=Right$(pkf$,Len(pkf$)-1)

Local cnt
Local item
Local Pack
Local Output
Local Block
Local filecnt
Local Hold
Local IsoName

If Instr(pkf$,"\")=0 And Instr(pkf$,"/")=0 Then 
IsoName = True
Else
CreateDirTree IsoPath(pkf$)
EndIf

For cnt=1 To packcount								; Count through Archive list
	If IsoName = True Then
		If IsolateName(Upper$(pakfiles$(cnt)))=Upper$(pkf$) Then		; If it exists in Archive
			item = cnt									; then set item to it's index
			Exit										; and break out of the loop
		EndIf
	Else
		If Upper$(pakfiles$(cnt))=Upper$(pkf$) Then		; If it exists in Archive
			item = cnt									; then set item to it's index
			Exit										; and break out of the loop
		EndIf
	EndIf
Next
If item = 0 Then 									; If no items were found
	Return pkf$										; Exit the subroutine
EndIf

outf$ = Upper$(IsoPath(pkf$)+IsolateName(pkf$))
For lpk.unpackedfiles = Each unpackedfiles
If Upper$(lpk\file$) = outf$ Then exists = 1 : Exit
Next

If exists=0 Then
	lpk.UnpackedFiles = New UnpackedFiles 
	lpk\file$ = IsoPath(pkf$)+IsolateName(pkf$)
EndIf

If FileType(outf$)=0 Or PakBulkOverwrite = True Then
	pack = ReadFile(packname$)							; Open the Archive
	SeekFile(pack,paklocate(item))						; Locate the Data in the store
	output = WriteFile(IsoPath(pkf$)+IsolateName(pkf$))	; Open the output File
	block = CreateBank(paksize(item)+4)
	
	ReadBytes block,pack,0,paksize(item)
	If key<>0 Then
	For filecnt=1 To paksize(item) Step 4
	hold=PeekInt(block,filecnt-1)
	hold = hold Xor key
	PokeInt block,filecnt-1,hold
	Next 
	EndIf
	WriteBytes block,output,0,paksize(item)
	
	CloseFile(pack)										; Close Both files
	CloseFile(output)
	FreeBank block
EndIf

Return isopath(pkf$)+isolatename(pkf$)				; Return the Filename
End Function


;-----------------------------------------------------------------------

Function PakClean()									; Clean Up function to remove
Local cnt.unpackedfiles

For cnt.unpackedfiles = Each unpackedfiles
	DeleteFile cnt\file$								; Deletes all files unpacked so far
	Delete cnt
Next												; That are contained in the Archive
End Function										; Keep Backups elsewhere and copy them in.

;-----------------------------------------------------------------------

Function texcheck(modname$)
Local file
Local size
Local png
Local jpg
Local bmp
Local tga

Local pnga
Local jpga
Local bmpa
Local tgaa

Local Bank
Local t
Local ser
Local m
Local tex$
Local k
Local ltp$

file = ReadFile(modname$)

If file = 0 Then Return

putit$ = JustPath(Modname$)
putit$ = Right$(putit$,Len(putit$)-Len(outputdir$))

size = FileSize(modname$)

bank = CreateBank(size+4)

png = svl(".png")
jpg = svl(".jpg")
bmp = svl(".bmp")
tga = svl(".tga")

pnga = svl(".PNG")
jpga = svl(".JPG")
bmpa = svl(".BMP")
tgaa = svl(".TGA")

ReadBytes bank,file,0,size

CloseFile file
ltp$=""
For t = 1 To size
	ser = PeekInt(bank,t)
		If ser = png Or ser = jpg Or ser = bmp Or ser = pnga Or ser = jpga Or ser = bmpa Or ser = tga Or ser = tgaa Then
			m = t
			While PeekByte(bank,m)<>0 And m>0 And PeekByte(bank,m)<>34 And PeekByte(bank,m)>31 And PeekByte(bank,m)<127 
				m=m-1
			Wend
			tex$ = ""
			For k=m+1 To t+3
				tex$=tex$+Chr$(PeekByte(bank,k))
			Next
			tex$=Isolatename(tex$)
			If ltp$<>(putit$+tex$) Then 
				texpak(putit$+tex$)
				ltp$ = putit$+tex$
			EndIf
		EndIf
Next

FreeBank bank
End Function

Function svl(ser$)
Local try
Local retval

try = CreateBank(4)

PokeByte try,0,Asc(Mid$(ser$,1,1))
PokeByte try,1,Asc(Mid$(ser$,2,1))
PokeByte try,2,Asc(Mid$(ser$,3,1))
PokeByte try,3,Asc(Mid$(ser$,4,1))

retval = PeekInt(try,0)

FreeBank try

Return retval

End Function

Function CreatePakFile(destfile$, APKey, Head)
Local Crap
SeedRnd MilliSecs()

AddFileTrack = WriteFile(destfile$)
AddPackCount=0
AddPackHeaderMask = Head
AddPackKey = APKey

WriteInt AddFileTrack, 0
For Crap = 1 To Rand(500)
	WriteByte AddFileTrack,Rand(254)
Next
End Function

Function AddtoPak(myfile$)
Local ScratchBank
Local FullSize
Local FileAxis
Local Crypt
Local Hold

AddPackCount = AddPackCount + 1
AddPack$(AddPackCount) = Myfile$ ; Isolatename$(MyFile$)
AddPackSize(AddPackCount) = FileSize(Myfile$)
AddPackOffset(AddPackCount) = FilePos(AddFileTrack)

FullSize = FileSize(MyFile$)
ScratchBank = CreateBank(FullSize + 8)
FileAxis = ReadFile(MyFile$)

ReadBytes ScratchBank,FileAxis,0,FullSize

If AddPackKey<>0 Then
	For Crypt = 0 To Fullsize+4 Step 4
		hold=PeekInt(ScratchBank,Crypt)
		hold = hold Xor AddPackKey
		PokeInt ScratchBank,Crypt,hold
	Next 
EndIf
WriteBytes ScratchBank,AddFileTrack,0,FullSize

FreeBank ScratchBank
CloseFile FileAxis

End Function

Function CloseCreatedPak()
Local N$=""
Local S$=""
Local O$=""
Local Full$=""
Local Cnt = 0
Local ScratchBank
Local Crypt
Local Hold
Local Crap

For cnt = 1 To AddPackCount

N$ = AddPack$(cnt)
S$ = AddPackSize(cnt)
O$ = AddPackOffSet(cnt)

Full$ = Full$ + N$ + Chr$(13)
Full$ = Full$ + O$ + Chr$(13)
Full$ = Full$ + S$ + Chr$(13)

Next

ScratchBank = CreateBank(Len(full$)+4)

For cnt = 1 To Len(Full$)
PokeByte ScratchBank, cnt-1, Asc(Mid$(full$,cnt,1))
Next

If AddPackKey<>0 Then
	For Crypt = 0 To Len(full$) Step 4
		hold=PeekInt(ScratchBank,Crypt)
		hold = hold Xor AddPackKey
		PokeInt ScratchBank,Crypt,hold
	Next 
EndIf

Hold = FilePos(AddFileTrack)+1

WriteInt AddFileTrack, AddPackCount

WriteBytes ScratchBank, AddFileTrack,0,Len(full$)

FreeBank ScratchBank

SeedRnd MilliSecs()

For Crap = 1 To Rand(500)
	WriteByte AddFileTrack,Rand(255)
Next

SeekFile AddFileTrack, 0

hold = hold Xor AddPackHeaderMask

WriteInt AddFileTrack, Hold

CloseFile AddFileTrack
End Function

Function AppendToExe(myexefile$, myPakName$)
Local TempAxis
Local TempPax
Local EXELen
Local PakLen
Local ScratchBank

PakLen = FileSize(MyPakName$)
EXELen = FileSize(myexefile$)
TempAxis = OpenFile(myexefile$)
TempPax = OpenFile(myPakName$)

ScratchBank = CreateBank(PakLen + 4)

ReadBytes ScratchBank,TempPax,0,PakLen

SeekFile TempAxis,EXELen

WriteBytes ScratchBank, TempAxis, 0, PakLen
WriteInt TempAxis, (EXELen + AddPackHeaderMask + 1)
WriteByte TempAxis,Asc("D") : WriteByte TempAxis,Asc("A") : WriteByte TempAxis,Asc("T") : WriteByte TempAxis,Asc("P") : WriteByte TempAxis,Asc("A") : WriteByte TempAxis,Asc("K")

CloseFile TempAxis
CloseFile TempPax

FreeBank ScratchBank

End Function

Function isolatename$(wha$)
    For t=Len(wha$) To 1 Step -1
        If Mid$(wha$,t,1)="\" Or Mid$(wha$,t,1)="/" Then
            Return Right$(wha$,Len(wha$)-t)
        EndIf
    Next
    Return wha$
End Function

Function IsoPath$(wha$)
    For t=Len(wha$) To 1 Step -1
        If Mid$(wha$,t,1)="\" Or Mid$(wha$,t,1)="/" Then
            Return OutputDir + Left$(wha$,t)
        EndIf
    Next
    Return OutputDir
End Function

Function justPath$(wha$)
    For t=Len(wha$) To 1 Step -1
        If Mid$(wha$,t,1)="\" Or Mid$(wha$,t,1)="/" Then
            Return Left$(wha$,t)
        EndIf
    Next
    Return ""
End Function

Function PakOutputDir(myDir$)
If Len(mydir$)<3 Then 
	mydir$=""
	Return
EndIf
If Right$(mydir$,1)<>"\" Or Right$(mydir$,1)<>"/" Then mydir$=mydir$+"\"
OutputDir$ = mydir$
createdirtree mydir$
End Function

Function DLPak()
Local t.unpackedfiles

For t.unpackedfiles = Each unpackedfiles
If Upper$(t\file$)=Upper$(lastpak$) Then
	DeleteFile LastPak$
	Delete t
EndIf
Next
End Function

Function CreateDirTree(mydir$)
Local t
Local exists
Local nux$

mydir$=mydir$+"\"
If Mid$(mydir$,2,1)=":" Then t = 4 Else t = 1
Repeat 
	exists = 0
	If Mid$(mydir$,t,1)="\" Or Mid$(mydir$,t,1)="/" Then
	
	For mug.expdir = Each expdir
		If Upper$(mug\dir$) = Upper$(Left$(mydir$,t-1)) Then exists = 1 : Exit
	Next

	If exists=0 Then
		nux$=Left$(mydir$,t-1)
		If Right$(nux$,1)="\" Or Right$(nux$,1)="/" Then nux$=Left$(nux$,Len(nux$)-1)
		CreateDir nux$
		mug.expdir = New expdir
		mug\dir$ = Upper$(nux$) 
	EndIf
	EndIf
	t=t+1
Until t=Len(mydir$)
End Function

Function ParseB3D(B3DFile$)
If Upper$(Right$(B3DFile$,4))<>".B3D" Then Return
Local BHand, tind, texf, texb
Local CSize, Fsize, Entries
Local Putit$
putit$ = JustPath(B3DFile$)
putit$ = Right$(putit$,Len(putit$)-Len(outputdir$))

Bhand = ReadFile(B3DFile$)
If Bhand = 0 Then Return
fsize = FileSize(B3DFile$)
ReadChunk(Bhand)
ReadInt(BHand)
While FilePos(Bhand)<Fsize
ReadChunk(Bhand)
mypos = FilePos(Bhand)
Select Chunkname$
Case "TEXS"
	While FilePos(Bhand)<(mypos+Chunklen)
	texpak(putit$+IsolateName$(StringRead$(Bhand)))
	ReadInt(Bhand)
	ReadInt(Bhand)
	ReadFloat(Bhand)
	ReadFloat(Bhand)
	ReadFloat(Bhand)
	ReadFloat(Bhand)
	ReadFloat(Bhand)
	tind = tind +1
	Wend
	SeekFile Bhand,mypos+Chunklen
Default 

SeekFile Bhand,mypos+Chunklen
End Select
Wend
CloseFile bhand
End Function

Function ReadChunk(Strm)
Local chunk$
Chunk$ = ""
chunk$=Chr$(ReadByte(strm))
Chunk$=Chunk$ + Chr$(ReadByte(strm))
Chunk$=Chunk$ + Chr$(ReadByte(strm))
Chunk$=Chunk$ + Chr$(ReadByte(strm))
ChunkName$=Upper$(Chunk$)
Chunklen = ReadInt(Strm)
End Function

Function StringRead$(Strm)
	Local Gb
	Local RT$
	
	Repeat
		gb=ReadByte(strm)
		
		If gb<>0 Then rt$=rt$+Chr$(gb)
		
	Until gb=0
	Return rt$
End Function 