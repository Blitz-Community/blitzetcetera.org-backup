;Shadowtext function
;Chompster Productions (http://chompster0.tripod.com)
;form shadowtext(string,x,y,colorred,colorgreen,colorblue,type,depth)
;Colorred/green/blue = Red/green/blue values
;type = 1 is dropped shadow, 2 is 3D Text, 3 is Gradiated Text
;Depth = Depth of 3D text 

Graphics 640,480,16,2
SetBuffer BackBuffer()
Global fontarial = LoadFont("Arial",36,True,False,False)
SetFont fontarial
Repeat
ClsColor 255,0,0
Cls
d=d+1
g=Sin(d)*10
shadowtext "Say Hello....", 100, 100, 0,0,200,1,0
shadowtext "Wave Goodbye...",100,200,0,200,200,2,g
shadowtext "Oooooh, gradient!",100,300,0,200,0,3,10
Flip
Until KeyHit(1)

Function shadowtext(strin$,x,y,colr,colg,colb,typ,depth)
If typ = 1 Then
Color 0,0,0
Text x+2, y+2, strin
End If
If typ = 2 Then
If colr>100 Then cols = colr - 100
If colb>100 Then colc = colb - 100
If colg>100 Then colh = colg - 100
For z = 1 To depth
Color cols,colh,colc
Text x-z, y-z, strin
Next
End If
If typ = 3
depth = depth + 1
varr = colr/depth
varg = colg/depth
varb = colb/depth
For z = 0 To depth
Color (varr*(depth-z)), (varg*(depth-z)), (varb*(depth-z))
Text x-z,y,strin
Next
End If
Color colr,colg,colb
Text x,y,strin
End Function