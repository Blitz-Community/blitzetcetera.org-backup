Graphics3D 640,480,32,2
SeedRnd MilliSecs()

While True 

 Text x#,waggle(x#,GraphicsHeight()/2,4),"I'm a boat!!"
  x#=x#+0.5
  If x#+Len("I'm a boat")>GraphicsWidth()+5 x#=0 ;wrap x value

 Text x2#,waggle(x2#,40,4),"This reminds me of a Sin() Effect... but its not..."
  x2#=x2#+2
  If x2#+Len("I'm a boat")>GraphicsWidth()+5 x2#=0 ;wrap x value

 Text x3#,waggle(x3#,GraphicsHeight()-100,4),"Weeeee!!!!!"
  x3#=x3#+3
  If x3#+Len("I'm a boat")>GraphicsWidth()+5 x3#=0 ;wrap x value


 Text x4#,waggle(x4#,GraphicsHeight()/2-50-12,4),"  Sin()-Like-Demo"
 Text x4#,waggle(x4#,GraphicsHeight()/2-50,4),"By: TheDuck"
 x4#=x4#+0.9
  If x4#+Len("I'm a boat")>GraphicsWidth()+5 x4#=0 ;wrap x value


 Flip 
 Cls 
Wend 


Function waggle(x#,y_origin#,power_of_waves#)
  y#=(Pi*Cos(Pi*x#))^power_of_waves#+y_origin
  Return y#
End Function
