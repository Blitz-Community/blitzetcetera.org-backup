Include "..\bones.bb"

Graphics 640,480
SetBuffer BackBuffer()

PakOutputDir "C:\OutPutPakDemoDeleteMe" ; If this Dir doesn't exist, it soon will!
PakBulkOverWrite = False ; Set to true in testing / False on release

CreatePakFile "Mydata.pak",$12345678,$87654321 ; Path (if any) and filename of datapak
											 	; then your Key and header mask

AddtoPak "media\elmo.png"             ; Add files to the pak 
AddtoPak "media\penny.jpg"            ; as many as you like

CloseCreatedPak()   ; Turn it into a DataPak

; AppendToExe "Mygame.exe","MyData.Pak" ; Option to append to .exe (not one that's running!!)

PakInit "MyData.Pak", $12345678, "TMP", $87654321 ; Open the pak

elmo = LoadImage(Pak("elmo.png")); : DLpak() ; Delete Last Pak()

While Not KeyDown(1)
Cls
DrawBlock elmo,MouseX(),MouseY()

Flip
Wend

EndGraphics
End