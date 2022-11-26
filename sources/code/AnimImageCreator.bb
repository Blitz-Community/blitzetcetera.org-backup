;>>>>>>>>>>>>>>>>>>>>>>>
;>>>AnimImage Creator>>>
;>>>By Supernatendo>>>>>
;>>>>>>>>>>>>>>>>>>>>>>>

Graphics 800,600,0,2
AppTitle "AnimImage Creator","AnimImage Creator Exiting"

numofframes = 3 ;change this to the number of frames in your new AnimImage
framelength = 100 ;change to the length of the frames in your new AnimImage
framewidth = 100 ;change to the width of the frames in your new AnimImage
imagename$ = "image" ;change to the name you would like your new AnimImage to have (without file extension)
imagenum = 1
imagenum1 = 0
newimage = CreateImage(framewidth*numofframes,framelength)
SetBuffer ImageBuffer(newimage)
Repeat 
imageframe = LoadImage(imagename$+imagenum+".bmp")
DrawImage imageframe,framewidth*imagenum1,0
imagenum = imagenum + 1
imagenum1 = imagenum1 + 1
Until imagenum = numofframes + 1
SetBuffer BackBuffer()
Text 0,0,imagename$+".bmp, "+"your new AnimImage looks like this:"
DrawImage newimage,0,20
SaveImage (newimage,imagename$+".bmp")
Flip
Repeat 
Delay(1)
Until 2 + 2 = 5
