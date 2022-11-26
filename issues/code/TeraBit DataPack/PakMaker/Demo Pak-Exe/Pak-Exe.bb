Include "..\bones.bb"

Graphics 640,480
SetBuffer BackBuffer()

PakOutputDir "" ; This can be set anywhere, a good example would be "C:\windows\Temp"
PakInit "Pak-exe.exe", $59195BC2, "TMP", $ED112152 ; Cut and Paste this from the GUI PakMaker
PakBulkOverWrite = False ; Set this to True when in Development / False when Released

elmo = LoadImage(Pak("media\elmo.png")) : DLPak()   ; DLPak() deletes the last Unpacked File
penny = LoadImage(Pak("media\penny.jpg")) : DLPak() ; Textures do not count, and if a 3D model
 													; It's better to use PakClean()

; As you can see from the above example, you can use a relative path in the Pak name
; This subdirectory(s) will be automatically created for you in the output dir

While Not KeyDown(1)
Cls
DrawBlock elmo,MouseX(),MouseY()
DrawBlock penny,MouseY(),MouseX()
Flip
Wend

EndGraphics
End